package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.dto.Card;
import cz.zcu.kiv.ups.dto.Message;
import cz.zcu.kiv.ups.dto.Opponent;
import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.network.MessageConsumer;
import cz.zcu.kiv.ups.utils.AlertsAndDialogs;
import cz.zcu.kiv.ups.utils.SpringFxmlLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import lombok.Getter;
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
 * Controller of Main Window and handler of most of the logic.
 *
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

	/**
	 * VBox in Main Window.
	 */
	@FXML
	private VBox mainWindowVBox;

	/**
	 * Controller of the Game Table screen.
	 */
	private GameTableController gameTableController;

	/**
	 * User's nickname.
	 */
	@Getter
	private String nickname;

	/**
	 * Content in Main Window to show desired screen.
	 */
	private VBox content;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Login.fxml");
		mainWindowVBox.getChildren().add(content);
	}

	/**
	 * Opens connection to specified server, sets nickname and starts scheduled reading of messages.
	 *
	 * @param hostname hostname.
	 * @param port     port.
	 * @param nickname nickname.
	 */
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

	/**
	 * Shows menu.
	 */
	public void showMenu() {
		mainWindowVBox.getChildren().remove(content);
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Menu.fxml");
		mainWindowVBox.getChildren().add(content);
	}

	/**
	 * Sends list of games request.
	 */
	public void listOfGamesRequest() {
		Message m = new Message(1, "");
		connection.sendMessage(m);
	}

	/**
	 * Shows choice dialog and sends create game request.
	 */
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

	/**
	 * Sends reconnect request.
	 */
	public void reconnectRequest() {
//		TODO: How to remember game id??
	}

	/**
	 * Shows list of games screen.
	 *
	 * @param message message with list of game.
	 */
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
		SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader(context);
		FXMLLoader loader = springFxmlLoader.getLoader();
		content = (VBox) springFxmlLoader.load(loader, getClass(), "ListOfGames.fxml");
		ListOfGamesController ctrl = loader.getController();
		ctrl.setGames(games);
		mainWindowVBox.getChildren().add(content);
	}

	/**
	 * Processes connect request answer and shows wait screen or error dialog.
	 *
	 * @param message message with answer.
	 */
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
						+ ".", "Connecting to game is impossible due to full capacity of the game.");
				break;
			case 2:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Nickname Error", "Could not connect to game"
						+ ".", "Connecting to game is impossible due to player with the same nickname.");
				break;
			case 3:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Game Error", "Could not connect to game.",
						"Connecting to game is impossible due to no longer existing game.");
				break;
			default:
				break;
		}
	}

	/**
	 * Processes create game answer and shows wait schreen or error dialog.
	 *
	 * @param message message with answer.
	 */
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

	/**
	 * Sends exit game message and shows menu.
	 */
	public void exitGame() {
		gameTableController = null;
		Message message = new Message(20, "");
		connection.sendMessage(message);
		showMenu();
	}

	/**
	 * Shows Game Table screen.
	 *
	 * @param message message with info about game.
	 */
	public void startGame(Message message) {
		String[] parts = message.getData().split(",");
		showGameTable(parts, 0, "Game is starting.");
	}

	/**
	 * Indicates to player that it's his turn.
	 */
	public void yourTurn() {
		if (gameTableController != null) {
			gameTableController.setMyTurn(true);
			gameTableController.getHistory().add("It's your turn.");
		}
	}

	/**
	 * Notifies player that it's someone's turn.
	 *
	 * @param message message with info about who's turn is it.
	 */
	public void someonesTurn(Message message) {
		if (gameTableController != null) {
			String msg = String.format("It's %s's turn.", message.getData());
			gameTableController.getHistory().add(msg);
		}
	}

	/**
	 * Updates game state based on result of player's move.
	 *
	 * @param message message with result of player's move.
	 */
	public void moveAnswer(Message message) {
		int result = Integer.parseInt(message.getData());
		if (gameTableController != null && result == 0) {
			gameTableController.moveSuccessful();
		}
	}

	/**
	 * Updates game state based on result of someone's move.
	 *
	 * @param message message with result
	 */
	public void someonesMoveAnswer(Message message) {
		String[] parts = message.getData().split(",");
		int result = Integer.parseInt(parts[0]);
		if (gameTableController != null) {
			String first = parts[1];
			Card card = Card.getCardByName(parts[2]);
			String second = parts[3];
			if (result == 0) {
				gameTableController.getHistory().add(String.format("%s gave %s to %s.", second, card.getName(),
						first));
				gameTableController.someonesMoveSuccessful(first, card, second);
			} else if (result == 1) {
				gameTableController.getHistory().add(String.format("%s doesn't have %s to give to %s.", second, card,
						first));
			}
		}
	}

	/**
	 * Shows End of game screen with specified text.
	 *
	 * @param text text to show.
	 */
	public void showEndOfGame(String text) {
		mainWindowVBox.getChildren().remove(content);
		SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader(context);
		FXMLLoader loader = springFxmlLoader.getLoader();
		content = (VBox) springFxmlLoader.load(loader, getClass(), "EndOfGame.fxml");
		EndOfGameController ctrl = loader.getController();
		ctrl.setInfoLabelText(text);
		mainWindowVBox.getChildren().addAll(content);
	}

	/**
	 * Shows End of game screen with message saying you won.
	 */
	public void youWon() {
		gameTableController = null;
		showEndOfGame("Congratulations, you won!");
	}

	/**
	 * Shows End of game screen with message saying who won.
	 *
	 * @param message message with info about who won.
	 */
	public void someoneWon(Message message) {
		gameTableController = null;
		showEndOfGame(String.format("Sorry, %s won.", message.getData()));
	}

	/**
	 * Shows End of game screen with message saying you lost.
	 */
	public void youLost() {
		gameTableController = null;
		showEndOfGame("Sorry, you lost.");
	}

	/**
	 * Adds message to history about player who lost.
	 *
	 * @param message message with info about who lost.
	 */
	public void someoneLost(Message message) {
		if (gameTableController != null) {
			gameTableController.getHistory().add(String.format("%s lost.", message.getData()));
		}
	}

	/**
	 * Ends game due to unreachable player.
	 *
	 * @param message message with info about unreachable player.
	 */
	public void playerUnreachable(Message message) {
		gameTableController = null;
		showEndOfGame(String.format("Sorry, %s is unreachable", message.getData()));
	}

	/**
	 * Processes reconnect answer and shows Wait screen, Game table or error dialog.
	 *
	 * @param message message with reconnect answer.
	 */
	public void reconnectAnswer(Message message) {
		String[] parts = message.getData().split(",");
		int result = Integer.parseInt(parts[0]);
		String title = "Reconnect Error";
		String header = "Could not reconnect to game.";
		switch (result) {
			case 0:
				showGameTable(parts, 1, "Reconnected to game.");
				break;
			case 1:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, title, header, "No player with specified " +
						"nickname in game.");
				break;
			case 2:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, title, header, "Specified player is not " +
						"considered not responding. If it was really you, try again later.");
				break;
			case 3:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, title, header, "Desired game was not found.");
				break;
			case 4:
				mainWindowVBox.getChildren().remove(content);
				content = (VBox) new SpringFxmlLoader(context).load(getClass(), "WaitRoom.fxml");
				mainWindowVBox.getChildren().add(content);
				break;
		}
	}

	/**
	 * Parses data from message according to input values and shows Game table screen.
	 *
	 * @param parts       data from message.
	 * @param index       index where to start parsing.
	 * @param historyInfo message to show in history.
	 */
	private void showGameTable(String[] parts, int index, String historyInfo) {
		int numberOfOpponents = Integer.parseInt(parts[index]);
		index++;
		List<Opponent> opponents = new LinkedList<>();
		for (int i = 0; i < numberOfOpponents; i++, index += 2) {
			Opponent opponent = new Opponent(parts[index], Integer.parseInt(parts[index + 1]));
			opponents.add(opponent);
		}

		int numberOfCards = Integer.parseInt(parts[index]);
		index++;
		List<Card> cards = new LinkedList<>();
		for (int i = 0; i < numberOfCards; i++) {
			cards.add(Card.getCardByName(parts[index + i]));
		}

		mainWindowVBox.getChildren().remove(content);
		SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader(context);
		FXMLLoader loader = springFxmlLoader.getLoader();
		content = (VBox) springFxmlLoader.load(loader, getClass(), "GameTable.fxml");
		GameTableController ctrl = loader.getController();
		ctrl.setCards(cards);
		ctrl.setOpponents(opponents);
		ctrl.getHistory().add(historyInfo);
		gameTableController = ctrl;
		mainWindowVBox.getChildren().addAll(content);
	}

}