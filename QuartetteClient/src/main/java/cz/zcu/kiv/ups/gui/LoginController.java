package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.utils.ParseCmdLine;
import javafx.application.Platform;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setParams(parseCmdLine.getHostname(), parseCmdLine.getPort());
	}

	/**
	 * Sets parameters into screen.
	 *
	 * @param hostname hostname.
	 * @param port     port.
	 */
	private void setParams(String hostname, int port) {
		hostnameTextField.setText(hostname);
		portTextField.setText(String.valueOf(port));
	}

	/**
	 * Opens connection to specified server.
	 */
	@FXML
	private void logIn() {
		Platform.runLater(() -> {
			mainWindowController.showWaitForServer();
			mainWindowController.openConnection(hostnameTextField.getText(), Integer.parseInt(portTextField.getText()));
		});
	}
}
