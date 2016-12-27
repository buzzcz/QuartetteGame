package cz.zcu.kiv.ups.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Jaroslav Klaus
 */
@Component
public class ListOfGamesController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	@FXML
	private ListView<String> gamesListView;

	@Setter
	private ObservableList<String> games;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gamesListView.setItems(games);
		gamesListView.setOnMouseClicked(event -> {
//			TODO: Handle connect to game
		});
	}
}
