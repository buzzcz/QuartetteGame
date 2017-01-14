package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.dto.Card;
import cz.zcu.kiv.ups.dto.Game;
import cz.zcu.kiv.ups.dto.Message;
import cz.zcu.kiv.ups.dto.MessageType;
import cz.zcu.kiv.ups.dto.Opponent;
import cz.zcu.kiv.ups.network.ConsumerInterface;
import cz.zcu.kiv.ups.network.KeepAliveCheckerInterface;
import cz.zcu.kiv.ups.network.KeepAliveSenderInterface;
import cz.zcu.kiv.ups.network.NetworkInterface;
import cz.zcu.kiv.ups.utils.AlertsAndDialogs;
import cz.zcu.kiv.ups.utils.ParseCmdLine;
import cz.zcu.kiv.ups.utils.SpringFxmlLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;

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
	private NetworkInterface connection;

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	@Autowired
	private ConsumerInterface consumer;

	@Autowired
	private KeepAliveSenderInterface sender;

	@Autowired
	private KeepAliveCheckerInterface checker;

	@Autowired
	private ParseCmdLine parseCmdLine;

	@Autowired
	private GameTableController gameTableController;

	/**
	 * VBox in Main Window.
	 */
	@FXML
	private VBox mainWindowVBox;

	/**
	 * Content in Main Window to show desired screen.
	 */
	private VBox content;

	/**
	 * User's nickname.
	 */
	@Getter
	@Setter
	private String nickname;

	/**
	 * Indicates whether player is in game.
	 */
	private boolean inGame = false;

	private List<ScheduledFuture<?>> futures = new LinkedList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		mainWindowVBox.getChildren().remove(content);
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Login.fxml");
		mainWindowVBox.getChildren().add(content);
		if (nickname == null) {
			nickname = parseCmdLine.getNickname();
		}
	}

	/**
	 * Opens connection to specified server, sets nickname and starts scheduled reading of messages.
	 *
	 * @param hostname hostname.
	 * @param port     port.
	 */
	void openConnection(String hostname, int port) {
		String error = connection.open(hostname, port);
		if (error == null) {
			showMenu();
			gameTableController.setReceivedKeepAlive(LocalDateTime.now());
			futures.add(scheduler.scheduleWithFixedDelay(consumer::consumeMessage, 50));
			futures.add(scheduler.scheduleWithFixedDelay(sender::sendKeepAlive, 2500));
			futures.add(scheduler.scheduleWithFixedDelay(checker::checkKeepAlive, 2500));
			log.info(String.format("Connected to %s:%d.", hostname, port));
		} else {
			log.error(String.format("Could not open connection: %s.", error));
			AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Connection Error", "Could not open connection.",
					error);
			showLogin();
		}
	}

	/**
	 * Shuts down scheduler and closes connection.
	 */
	private void closeConnection() {
		for (ScheduledFuture<?> f : futures) {
			f.cancel(true);
		}
		futures.clear();
		connection.close();
	}

	public void showLogin() {
		exitGame(false);
		closeConnection();
		initialize(null, null);
	}

	/**
	 * Shows menu.
	 */
	void showMenu() {
		mainWindowVBox.getChildren().remove(content);
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Menu.fxml");
		mainWindowVBox.getChildren().add(content);
	}

	/**
	 * Sends list of games request.
	 */
	void listOfGamesRequest() {
		showWaitForServer();
		Message m = new Message(MessageType.LIST_OF_GAMES_REQUEST, "");
		connection.sendMessage(m);
		log.info("Sending request for list of games.");
	}

	/**
	 * Shows choice dialog and sends create game request.
	 */
	void createGameRequest() {
		List<Integer> numbers = new LinkedList<>();
		for (int i = 2; i < 32; i++) {
			numbers.add(i);
		}
		Optional<Pair<Integer, String>> result = AlertsAndDialogs.showAndWaitChoiceTextDialog("Create New Game",
				"Create new game.", "Select number of desired opponents:", numbers, null, "Enter your nickname:",
				"Nickname", nickname);
		result.ifPresent(pair -> {
			if (pair.getValue().contains(",") || pair.getValue().contains(";") || pair.getValue().contains("\n") ||
					pair.getValue().contains("\r\n")) {
				log.error(String.format("Nickname %s contains unsupported characters.", pair.getValue()));
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Nickname Error", "Nickname contains " +
						"unsupported characters.", "Nickname cannot contain ',', ';', '\\n' and \"\\r\\n\"");
				createGameRequest();
				return;
			}
			showWaitForServer();
			nickname = pair.getValue();
			Message m = new Message(MessageType.CREATE_GAME_REQUEST, String.format("%s,%d", nickname, pair.getKey()));
			connection.sendMessage(m);
			log.info(String.format("Sending request for creating game with nickname %s and %d opponents.", nickname,
					pair.getKey()));
		});
	}

	/**
	 * Sends reconnect request.
	 */
	void reconnectRequest() {
		Optional<String> result = AlertsAndDialogs.showAndWaitTextInputDialog("Reconnect To Game", "Reconnect to " +
				"game.", "Enter nickname:", "Nickname", nickname);
		result.ifPresent(s -> {
			if (s.contains(",") || s.contains(";") || s.contains("\n") || s.contains("\r\n")) {
				log.error(String.format("Nickname %s contains unsupported characters.", s));
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Nickname Error", "Nickname contains " +
						"unsupported characters.", "Nickname cannot contain ',', ';', '\\n' and \"\\r\\n\"");
				reconnectRequest();
				return;
			}
			showWaitForServer();
			nickname = s;
			Message m = new Message(MessageType.RECONNECT_REQUEST, nickname);
			connection.sendMessage(m);
			log.info(String.format("Sending request for reconnecting with nickname %s.", nickname));
		});
	}

	/**
	 * Shows list of games screen.
	 *
	 * @param message message with list of game.
	 */
	public void showListOfGames(Message message) {
		String[] parts = message.getData().split(",");
		int numberOfGames = Integer.parseInt(parts[0]);
		List<Game> games = new LinkedList<>();
		for (int i = 0, index = 1; i < numberOfGames; i++, index += 3) {
			Game game = new Game(parts[index], Integer.parseInt(parts[index + 1]), Integer.parseInt(parts[index + 2]));
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
	 * Shows wait room.
	 *
	 * @param text text to show.
	 */
	private void showWaitRoom(String text) {
		mainWindowVBox.getChildren().remove(content);
		SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader(context);
		FXMLLoader loader = springFxmlLoader.getLoader();
		content = (VBox) springFxmlLoader.load(loader, getClass(), "WaitRoom.fxml");
		WaitRoomController ctrl = loader.getController();
		ctrl.setInfoLabelText(text);
		mainWindowVBox.getChildren().addAll(content);
	}

	/**
	 * Shows wait room saying you're waiting for other players.
	 */
	private void showWaitForOthers() {
		inGame = true;
		showWaitRoom("Waiting for other players.");
	}

	/**
	 * Shows wait room saying you're waiting for server response.
	 */
	void showWaitForServer() {
		showWaitRoom("Waiting for server response.");
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
				showWaitForOthers();
				break;
			case 1:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Capacity Error", "Could not connect to game"
						+ ".", "Connecting to game is impossible due to full capacity of the game.");
				showMenu();
				break;
			case 2:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Nickname Error", "Could not connect to game"
						+ ".", "Connecting to game is impossible because player with the same nickname is already on"
						+ " server. Choose another nickname.");
				showMenu();
				break;
			case 3:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "No Game Error", "Could not connect to " +
								"game.",
						"Connecting to game is impossible due to no longer existing game.");
				showMenu();
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
		if (code == 0) {
			showWaitForOthers();
		} else if (code == 1) {
			log.error("Could not create game - player with the same nickname is already on server.");
			AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Nickname Error", "Could not create new " +
					"game.", "Creating new game is impossible - player with the same nickname is already on server.");
			showMenu();
		} else {
			log.error("Could not create game - number of opponents was smaller then 2 or higher then 31");
			AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Capacity Error", "Could not create new " +
					"game.", "Creating new game is impossible - number of opponents was smaller then 2 or higher " +
					"then 31.");
			showMenu();
		}
	}

	/**
	 * Sends exit game message and shows menu.
	 * @param showMenu indicates whether menu should be shown.
	 */
	void exitGame(boolean showMenu) {
		if (inGame) {
			inGame = false;
			Message message = new Message(MessageType.DISCONNECTING, "");
			connection.sendMessage(message);
			log.info("Exiting game.");
		}
		if (showMenu) {
			showMenu();
		}
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
		if (inGame) {
			gameTableController.setMyTurn(true);
			gameTableController.getHistory().add("It's your turn.");
			log.info("It's your turn.");
		}
	}

	/**
	 * Notifies player that it's someone's turn.
	 *
	 * @param message message with info about who's turn is it.
	 */
	public void someonesTurn(Message message) {
		if (inGame) {
			String msg = String.format("It's %s's turn.", message.getData());
			gameTableController.getHistory().add(msg);
			log.info(msg);
		}
	}

	/**
	 * Updates game state based on result of player's move.
	 *
	 * @param message message with result of player's move.
	 */
	public void yourMoveAnswer(Message message) {
		if (inGame) {
			int result = Integer.parseInt(message.getData());
			if (result == 0) {
				gameTableController.moveSuccessful();
				log.info(String.format("I took %s from %s.", gameTableController.getLastMove().getValue().getName(),
						gameTableController.getLastMove().getKey().getName()));
			} else {
				String info = String.format("%s does not have %s.", gameTableController.getLastMove().getKey().getName
						(), gameTableController.getLastMove().getValue().getName());
				gameTableController.getHistory().add(info);
				log.info(info);
			}
		}
	}

	/**
	 * Updates game state based on result of someone's move.
	 *
	 * @param message message with result
	 */
	public void someonesMove(Message message) {
		if (inGame) {
			String[] parts = message.getData().split(",");
			int result = Integer.parseInt(parts[0]);
			String first = parts[1];
			Card card = Card.getCardByName(parts[2]);
			String second = parts[3];
			if (result == 0) {
				gameTableController.someonesMoveSuccessful(first, card, second);
			} else if (result == 1) {
				String info = String.format("%s doesn't have %s to give to %s.", second, card
						.getName(), first);
				gameTableController.getHistory().add(info);
				log.info(info);
			}
		}
	}

	/**
	 * Shows End of game screen with specified text.
	 *
	 * @param text text to show.
	 */
	private void showEndOfGame(String text) {
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
		if (inGame) {
			inGame = false;
			showEndOfGame("Congratulations, you won!");
			log.info("I won.");
		}
	}

	/**
	 * Shows End of game screen with message saying who won.
	 *
	 * @param message message with info about who won.
	 */
	public void someoneWon(Message message) {
		if (inGame) {
			inGame = false;
			showEndOfGame(String.format("Sorry, %s won.", message.getData()));
			log.info(String.format("%s won.", message.getData()));
		}
	}

	/**
	 * Shows End of game screen with message saying you lost.
	 */
	public void youLost() {
		if (inGame) {
			inGame = false;
			showEndOfGame("Sorry, you lost.");
			log.info("I lost.");
		}
	}

	/**
	 * Adds message to history about player who lost.
	 *
	 * @param message message with info about who lost.
	 */
	public void someoneLost(Message message) {
		if (inGame) {
			gameTableController.someoneLost(message.getData());
			log.info(String.format("%s lost.", message.getData()));
		}
	}

	/**
	 * Ends game due to unreachable player.
	 *
	 * @param message message with info about unreachable player.
	 */
	public void playerUnreachable(Message message) {
		if (inGame) {
			inGame = false;
			showEndOfGame(String.format("Sorry, %s is unreachable", message.getData()));
		}
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
						"nickname found on server.");
				showMenu();
				break;
			case 2:
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, title, header, "Specified player is still " +
						"active. If it was really you, try again later.");
				showMenu();
				break;
			case 3:
				showWaitForOthers();
				break;
			default:
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
		gameTableController.setCards(cards);
		gameTableController.setOpponents(opponents);
		gameTableController.getHistory().clear();
		gameTableController.getHistory().add(historyInfo);
		inGame = true;
		mainWindowVBox.getChildren().addAll(content);
	}

	/**
	 * Sets new time for received keep-alive.
	 */
	public void keepAlive() {
		gameTableController.setReceivedKeepAlive(LocalDateTime.now());
	}

}