package cz.zcu.kiv.ups.gui;

import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller of Wait Room screen.
 *
 * @author Jaroslav Klaus
 */
@Component
public class WaitRoomController {

	@Autowired
	private MainWindowController mainWindowController;

	/**
	 * Exits game and shows menu.
	 */
	@FXML
	public void exit() {
		mainWindowController.exitGame();
	}
}
