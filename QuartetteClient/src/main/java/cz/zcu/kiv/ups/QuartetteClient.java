package cz.zcu.kiv.ups;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * @author Jaroslav Klaus
 */
@SpringBootApplication
@Configuration
@EnableScheduling
public class QuartetteClient extends Application {

	private static String[] parameters;

	public static void main(String[] args) {
		parameters = args;
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		ApplicationContext context = SpringApplication.run(QuartetteClient.class, parameters);
		ParseCmdLine parseCmdLine = context.getBean(ParseCmdLine.class);

		SpringFxmlLoader loader = new SpringFxmlLoader(context);
		Parent root = (Parent) loader.load(getClass(), "gui/MainWindow.fxml");
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Bean
	public ParseCmdLine getParseCmdLine() {
		return new ParseCmdLine(parameters);
	}
}
