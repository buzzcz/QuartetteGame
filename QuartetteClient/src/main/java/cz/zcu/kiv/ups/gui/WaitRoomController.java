package cz.zcu.kiv.ups.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Jaroslav Klaus
 */
@Component
public class WaitRoomController {

	@Autowired
	private MainWindowController mainWindowController;

	@FXML
	public void exit() {
		mainWindowController.exitGame();
	}
}
