package cz.zcu.kiv.ups;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jaroslav Klaus
 */
@Slf4j
public class SpringFxmlLoader {

	private ApplicationContext context;

	public SpringFxmlLoader(ApplicationContext context) {
		this.context = context;
	}

	public Object load(Class<?> cl, String url) {
		FXMLLoader loader = getLoader();
		return load(loader, cl, url);
	}

	public Object load(FXMLLoader loader, Class<?> cl, String url) {
		if (loader != null) {
			try (InputStream fxmlStream = cl.getResourceAsStream(url)) {
				return loader.load(fxmlStream);
			} catch (IOException e) {
				log.error("Cannot load " + url, e);
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Load Error");
				alert.setHeaderText("Could not load resource.");
				alert.setContentText("Resource " + url + " could not be loaded.");
				alert.showAndWait();
			}
		}
		return null;
	}

	public FXMLLoader getLoader() {
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(context::getBean);
		return loader;
	}
}
