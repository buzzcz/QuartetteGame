package cz.zcu.kiv.ups.gui;

import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jaroslav Klaus
 */
@Component
public class MenuController {

	@Autowired
	private MainWindowController mainWindowController;

	@FXML
	public void listOfGamesRequest() {
		mainWindowController.listOfGamesRequest();
	}

	@FXML
	public void createGameRequest() {
		mainWindowController.createGameRequest();
	}

	@FXML
	public void reconnectRequest() {
		mainWindowController.reconnectRequest();
	}

}
