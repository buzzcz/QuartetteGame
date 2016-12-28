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
public class ListOfGamesController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	@FXML
	private ListView<String> gamesListView;

	private ObservableList<String> games = FXCollections.observableList(new LinkedList<>());

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gamesListView.setItems(games);
		gamesListView.setOnMouseClicked(event -> {
//			TODO: Handle connect to game
		});
	}

	public void fillGames(List<String> games) {
		this.games.clear();
		this.games.addAll(games);
	}

	@FXML
	public void back() {
		mainWindowController.showMenu();
	}
}
