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
 * @author Jaroslav Klaus
 */
@Component
public class LoginController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	@Autowired
	private ParseCmdLine parseCmdLine;

	@FXML
	private TextField hostnameTextField;

	@FXML
	private TextField portTextField;

	@FXML
	private TextField nicknameTextField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setParams(parseCmdLine.getHostname(), parseCmdLine.getPort(), parseCmdLine.getNickname());
	}

	private void setParams(String hostname, int port, String nickname) {
		hostnameTextField.setText(hostname);
		portTextField.setText(String.valueOf(port));
		nicknameTextField.setText(nickname);
	}

	@FXML
	public void logIn() {
//		TODO: Check nickname for unsupported characters
		mainWindowController.openConnection(hostnameTextField.getText(), Integer.parseInt(portTextField.getText()),
				nicknameTextField.getText());
	}
}
