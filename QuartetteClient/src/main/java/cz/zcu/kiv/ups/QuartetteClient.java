package cz.zcu.kiv.ups;


import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.utils.ParseCmdLine;
import cz.zcu.kiv.ups.utils.SpringFxmlLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

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
		primaryStage.setOnCloseRequest(event -> {
			ThreadPoolTaskScheduler scheduler = context.getBean(ThreadPoolTaskScheduler.class);
			scheduler.shutdown();
			Connection connection = context.getBean(Connection.class);
			connection.close();
		});
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

	@Bean
	public ThreadPoolTaskScheduler getTaskScheduler() {
		return new ThreadPoolTaskScheduler();
	}
}
