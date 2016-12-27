package cz.zcu.kiv.ups;

import cz.zcu.kiv.ups.gui.MainWindowController;
import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.network.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Jaroslav Klaus
 */
@Service
public class MessageConsumer implements ConsumerInterface {

	@Autowired
	private MainWindowController mainWindowController;

	@Autowired
	private Connection connection;

	@Override
	@Scheduled(fixedDelay = 50)
	public void consumeMessage() {
//		TODO: How to start method after creating connection??
		Message message = connection.getMessage();
		if (message == null) {
			return;
		}
		switch (message.getType()) {
			case 2:
				mainWindowController.showListOfGames(message);
				break;
			case 4:

				break;
		}
	}

}
