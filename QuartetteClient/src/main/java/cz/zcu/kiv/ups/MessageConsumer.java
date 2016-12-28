package cz.zcu.kiv.ups;

import cz.zcu.kiv.ups.gui.MainWindowController;
import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.network.Message;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
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
		Message message = connection.getMessage();
		if (message == null) {
			return;
		}
		switch (message.getType()) {
			case 2:
				Platform.runLater(() -> mainWindowController.showListOfGames(message));
				break;
			case 4:
				
				break;
		}
	}
}
