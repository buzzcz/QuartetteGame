package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.QuartetteClient;
import cz.zcu.kiv.ups.ParseCmdLine;
import cz.zcu.kiv.ups.SpringFxmlLoader;
import cz.zcu.kiv.ups.network.TcpConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
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
	@Getter
	private TcpConnection connection;

	@FXML
	private VBox mainWindowVBox;
	private VBox content;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		content = (VBox) new SpringFxmlLoader(context).load(getClass(), "Login.fxml");
		mainWindowVBox.getChildren().add(content);
	}

	public void createConnection(String hostname, int port) {
		connection.open(hostname, port);
	}
}