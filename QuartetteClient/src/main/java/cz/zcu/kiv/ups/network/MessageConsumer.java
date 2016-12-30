package cz.zcu.kiv.ups.network;

import cz.zcu.kiv.ups.dto.Message;
import cz.zcu.kiv.ups.gui.MainWindowController;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of Consumer Interface. Receives and consumes messages.
 *
 * @author Jaroslav Klaus
 */
@Service
@Slf4j
public class MessageConsumer implements ConsumerInterface {

	@Autowired
	private MainWindowController mainWindowController;

	@Autowired
	private Connection connection;

	@Override
	public void consumeMessage() {
		Message message = connection.receiveMessage();
		if (message == null) {
			return;
		}
		switch (message.getType()) {
			case LIST_OF_GAMES_ANSWER:
				Platform.runLater(() -> mainWindowController.showListOfGames(message));
				break;
			case CONNECT_ANSWER:
				Platform.runLater(() -> mainWindowController.connectRequestAnswer(message));
				break;
			case CREATE_GAME_ANSWER:
				Platform.runLater(() -> mainWindowController.createGameAnswer(message));
				break;
			case START_OF_GAME:
				Platform.runLater(() -> mainWindowController.startGame(message));
				break;
			case YOUR_TURN:
				Platform.runLater(mainWindowController::yourTurn);
				break;
			case SOMEONES_TURN:
				Platform.runLater(() -> mainWindowController.someonesTurn(message));
				break;
			case YOUR_MOVE_ANSWER:
				Platform.runLater(() -> mainWindowController.yourMoveAnswer(message));
				break;
			case SOMEONES_MOVE:
				Platform.runLater(() -> mainWindowController.someonesMove(message));
				break;
			case YOU_WON:
				Platform.runLater(mainWindowController::youWon);
				break;
			case SOMEONE_WON:
				Platform.runLater(() -> mainWindowController.someoneWon(message));
				break;
			case YOU_LOST:
				Platform.runLater(mainWindowController::youLost);
				break;
			case SOMEONE_LOST:
				Platform.runLater(() -> mainWindowController.someoneLost(message));
				break;
			case PLAYER_UNREACHABLE:
				Platform.runLater(() -> mainWindowController.playerUnreachable(message));
				break;
			case RECONNECT_ANSWER:
				Platform.runLater(() -> mainWindowController.reconnectAnswer(message));
				break;
			default:
				break;
		}
	}
}
