package cz.zcu.kiv.ups.network;

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
			case 2:
				Platform.runLater(() -> mainWindowController.showListOfGames(message));
				break;
			case 4:
				Platform.runLater(() -> mainWindowController.connectRequestAnswer(message));
				break;
			case 6:
				Platform.runLater(() -> mainWindowController.createGameAnswer(message));
				break;
			case 7:
				Platform.runLater(() -> mainWindowController.startGame(message));
				break;
			case 8:
				Platform.runLater(mainWindowController::yourTurn);
				break;
			default:
				break;
		}
	}
}
