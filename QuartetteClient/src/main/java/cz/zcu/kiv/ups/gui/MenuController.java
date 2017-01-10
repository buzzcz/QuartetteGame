package cz.zcu.kiv.ups.gui;

import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller of Menu screen.
 *
 * @author Jaroslav Klaus
 */
@Component
public class MenuController {

	@Autowired
	private MainWindowController mainWindowController;

	/**
	 * Sends list of games request.
	 */
	@FXML
	private void listOfGamesRequest() {
		mainWindowController.listOfGamesRequest();
	}

	/**
	 * Sends create new game request.
	 */
	@FXML
	private void createGameRequest() {
		mainWindowController.createGameRequest();
	}

	/**
	 * Sends reconnect request.
	 */
	@FXML
	private void reconnectRequest() {
		mainWindowController.reconnectRequest();
	}

	@FXML
	private void disconnect() {
		mainWindowController.showLogin();
	}

}
