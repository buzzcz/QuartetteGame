package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.MessageConsumer;
import cz.zcu.kiv.ups.SpringFxmlLoader;
import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.network.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Connection Error");
			alert.setHeaderText("Could not open connection.");
			alert.setContentText(error);
			alert.showAndWait();
		}
	}

	public void showMenu() {
		mainWindowVBox.getChildren().remove(content);
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Menu.fxml");
		mainWindowVBox.getChildren().add(content);
	}

	public void listOfGamesRequest() {
		Message m = new Message(1, "");
		connection.putMessage(m);
	}

	public void createGameRequest() {
		List<Integer> numbers = new LinkedList<>();
		for (int i = 2; i < 32; i++) {
			numbers.add(i);
		}
		ChoiceDialog<Integer> dialog = new ChoiceDialog<>(3, numbers);
		dialog.setTitle("Create New Game");
		dialog.setHeaderText("Create new game.");
		dialog.setContentText("Select number of desired opponents:");
		Optional<Integer> result = dialog.showAndWait();
		result.ifPresent(number -> {
			Message m = new Message(5, nickname + "," + number);
			connection.putMessage(m);
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
		FXMLLoader loader = new SpringFxmlLoader(context).getLoader();
		try {
			content = loader.load(getClass().getResourceAsStream("ListOfGames.fxml"));
		} catch (IOException e) {
			log.error("Error loading ListOfGames.fxml", e);
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Load Error");
			alert.setHeaderText("Could not load resource.");
			alert.setContentText("Resource ListOfGames.fxml could not be loaded.");
			alert.showAndWait();
			return;
		}
		ListOfGamesController ctrl = loader.getController();
		ctrl.fillGames(games);
		mainWindowVBox.getChildren().add(content);
	}

	public void connectedToGame(Message message) {

	}

}