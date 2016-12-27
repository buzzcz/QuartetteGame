package cz.zcu.kiv.ups;

import javafx.fxml.FXMLLoader;
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
		FXMLLoader loader = getLoader(cl, url);
		if (loader != null) {
			try {
				return loader.load();
			} catch (IOException e) {
				log.error("Cannot load " + url, e);
			}
		}
		return null;
	}

	public FXMLLoader getLoader(Class<?> cl, String url) {
		try (InputStream fxmlStream = cl.getResourceAsStream(url)) {
			FXMLLoader loader = new FXMLLoader();
			loader.setControllerFactory(clazz -> context.getBean(clazz));
			return loader;
		} catch (IOException e) {
			log.error("Cannot open " + url, e);
		}
		return null;
	}
}
