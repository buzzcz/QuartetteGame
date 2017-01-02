package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.utils.ParseCmdLine;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of Login screen.
 *
 * @author Jaroslav Klaus
 */
@Component
public class LoginController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	@Autowired
	private ParseCmdLine parseCmdLine;

	/**
	 * Text Field with hostname.
	 */
	@FXML
	private TextField hostnameTextField;

	/**
	 * Text Field with port.
	 */
	@FXML
	private TextField portTextField;

	/**
	 * Text Field with nickname.
	 */
	@FXML
	private TextField nicknameTextField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setParams(parseCmdLine.getHostname(), parseCmdLine.getPort(), parseCmdLine.getNickname());
	}

	/**
	 * Sets parameters into screen.
	 *
	 * @param hostname hostname.
	 * @param port     port.
	 * @param nickname nickname.
	 */
	private void setParams(String hostname, int port, String nickname) {
		hostnameTextField.setText(hostname);
		portTextField.setText(String.valueOf(port));
		nicknameTextField.setText(nickname);
	}

	/**
	 * Opens connection to specified server.
	 */
	@FXML
	private void logIn() {
//		TODO: Check nickname for unsupported characters
		mainWindowController.openConnection(hostnameTextField.getText(), Integer.parseInt(portTextField.getText()),
				nicknameTextField.getText());
	}
}
