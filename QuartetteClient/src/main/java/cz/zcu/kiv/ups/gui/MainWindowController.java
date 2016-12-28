package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.network.Message;
import cz.zcu.kiv.ups.network.MessageConsumer;
import cz.zcu.kiv.ups.utils.AlertsAndDialogs;
import cz.zcu.kiv.ups.utils.SpringFxmlLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Jaroslav Klaus
 */
@Component
@Slf4j
public class MainWindowController implements Initializable {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private Connection connection;

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	@Autowired
	private MessageConsumer consumer;

	@FXML
	private VBox mainWindowVBox;

	private String nickname;

	private VBox content;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Login.fxml");
		mainWindowVBox.getChildren().add(content);
	}

	public void openConnection(String hostname, int port, String nickname) {
		String error = connection.open(hostname, port);
		if (error == null) {
			this.nickname = nickname;
			showMenu();
			scheduler.scheduleWithFixedDelay(consumer::consumeMessage, 50);
		} else {
			log.error("Could not open connection.");
			AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Connection Error", "Could not open connection.",
					error);
		}
	}

	public void showMenu() {
		mainWindowVBox.getChildren().remove(content);
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Menu.fxml");
		mainWindowVBox.getChildren().add(content);
	}

	public void listOfGamesRequest() {
		Message m = new Message(1, "");
		connection.sendMessage(m);
	}

	public void createGameRequest() {
		List<Integer> numbers = new LinkedList<>();
		for (int i = 2; i < 32; i++) {
			numbers.add(i);
		}
		Optional<Integer> result = AlertsAndDialogs.showAndWaitChoiceDialog(3, numbers, "Create New Game", "Create" +
				" new game.", "Select number of desired opponents:");
		result.ifPresent(number -> {
			Message m = new Message(5, nickname + "," + number);
			connection.sendMessage(m);
		});
	}

	public void reconnectRequest() {
//		TODO: How to remember game id??
	}

	public void showListOfGames(Message message) {
		String[] parts = message.getData().split(",");
		int numberOfGames = Integer.parseInt(parts[0]);
		List<String> games = new LinkedList<>();
		for (int i = 0, index = 1; i < numberOfGames; i++, index += 3) {
			String game = String.format("Id: %s, connected: %d, capacity: %d", parts[index], Integer.parseInt
					(parts[index + 1]), Integer.parseInt(parts[index + 2]));
			games.add(game);
		}
		mainWindowVBox.getChildren().remove(content);
		SpringFxmlLoader springFxmlLoaderLoader = new SpringFxmlLoader(context);
		FXMLLoader loader = springFxmlLoaderLoader.getLoader();
		content = (VBox) springFxmlLoaderLoader.load(loader, getClass(), "ListOfGames.fxml");
		ListOfGamesController ctrl = loader.getController();
		ctrl.fillGames(games);
		mainWindowVBox.getChildren().add(content);
	}

	public void connectRequestAnswer(Message message) {
		int code = Integer.parseInt(message.getData());
		switch (code) {
			case 0:
				mainWindowVBox.getChildren().remove(content);
				content = (VBox) new SpringFxmlLoader(context).load(getClass(), "WaitRoom.fxml");
				mainWindowVBox.getChildren().add(content);
				break;
			case 1:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Capacity Error", "Could not connect to game"
						+ ".", "Connecting " + "to game is impossible due to full capacity of the game.");
				break;
			case 2:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Nickname Error", "Could not connect to game"
						+ ".", "Connecting " + "to game is impossible due to player with the same nickname.");
				break;
			case 3:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Game Error", "Could not connect to game.",
						"Connecting to" + " game is impossible due to no longer existing game.");
				break;
			default:
				break;
		}
	}

	public void createGameAnswer(Message message) {
		int code = Integer.parseInt(message.getData());
		if (code >= 0) {
			mainWindowVBox.getChildren().remove(content);
			content = (VBox) new SpringFxmlLoader(context).load(getClass(), "WaitRoom.fxml");
			mainWindowVBox.getChildren().add(content);
		} else {
			AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Create Game Error", "Could not create new " +
					"game.", "Creating new game is impossible due to number of opponents lower then 2.");
		}
	}

	public void exitGame() {
		Message message = new Message(20, "");
		connection.sendMessage(message);
		showMenu();
	}

}