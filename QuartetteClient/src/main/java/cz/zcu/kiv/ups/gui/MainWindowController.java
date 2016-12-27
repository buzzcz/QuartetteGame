package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.SpringFxmlLoader;
import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.network.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Jaroslav Klaus
 */
@Component
@Slf4j
public class MainWindowController implements Initializable {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private Connection connection;

	@FXML
	private VBox mainWindowVBox;

	private String nickname;

	private VBox content;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Login.fxml");
		mainWindowVBox.getChildren().add(content);
	}

	public void createConnection(String hostname, int port, String nickname) {
		String error = connection.open(hostname, port);
		if (error == null) {
			this.nickname = nickname;
			mainWindowVBox.getChildren().remove(content);
			content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Menu.fxml");
			mainWindowVBox.getChildren().add(content);
		} else {
			log.error("Could not open connection.");
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Connection Error");
			alert.setHeaderText("Could not open connection.");
			alert.setContentText(error);
			alert.showAndWait();
		}
	}

	public void listOfGamesRequest() {
		Message m = new Message(1, "");
		connection.putMessage(m);
	}

	public void createGameRequest() {
//		TODO: Create dialog to enter number of opponents.
	}

	public void reconnectRequest() {
//		TODO: How to get game id??
	}

}