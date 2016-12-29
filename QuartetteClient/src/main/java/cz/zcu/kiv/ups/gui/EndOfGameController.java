package cz.zcu.kiv.ups.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller of End Of Game screen.
 *
 * @author Jaroslav Klaus
 */
@Component
public class EndOfGameController {

	@Autowired
	private MainWindowController mainWindowController;

	@FXML
	private Label infoLabel;

	/**
	 * Sets infoLabel text.
	 *
	 * @param text text to show.
	 */
	public void setInfoLabelText(String text) {
		infoLabel.setText(text);
	}

	/**
	 * Shows menu.
	 */
	@FXML
	public void menu() {
		mainWindowController.showMenu();
	}
}
