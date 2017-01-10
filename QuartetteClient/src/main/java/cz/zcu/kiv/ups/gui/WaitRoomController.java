package cz.zcu.kiv.ups.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

	@FXML
	private Label infoLabel;

	/**
	 * Exits game and shows menu.
	 */
	@FXML
	private void exit() {
		mainWindowController.exitGame(true);
	}

	/**
	 * Sets text into info label.
	 * @param text text to set.
	 */
	public void setInfoLabelText(String text) {
		infoLabel.setText(text);
	}

}
