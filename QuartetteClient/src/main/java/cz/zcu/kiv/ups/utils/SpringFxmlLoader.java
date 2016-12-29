package cz.zcu.kiv.ups.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility to integrate JavaFX and Spring.
 *
 * @author Jaroslav Klaus
 */
@Slf4j
public class SpringFxmlLoader {

	/**
	 * Application context.
	 */
	private ApplicationContext context;

	/**
	 * Constructor to create utility.
	 *
	 * @param context application context.
	 */
	public SpringFxmlLoader(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * Loads resource from specified url.
	 *
	 * @param cl  class from which to get resource.
	 * @param url url of resource to load.
	 * @return loaded resource.
	 */
	public Object load(Class<?> cl, String url) {
		FXMLLoader loader = getLoader();
		return load(loader, cl, url);
	}

	/**
	 * Loads resource from specified url with given FXML Loader.
	 *
	 * @param loader FXML Loader to use for loading.
	 * @param cl     class from which to get resource.
	 * @param url    url of resource to load.
	 * @return loaded resource.
	 */
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

	/**
	 * Creates FXML Loader managed by Spring.
	 *
	 * @return FXML Loader managed by Spring.
	 */
	public FXMLLoader getLoader() {
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(context::getBean);
		return loader;
	}
}
