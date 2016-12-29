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
 * Controller of List of Games screen.
 *
 * @author Jaroslav Klaus
 */
@Component
public class ListOfGamesController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	/**
	 * List of games.
	 */
	private ObservableList<String> games = FXCollections.observableList(new LinkedList<>());

	/**
	 * List View of games.
	 */
	@FXML
	private ListView<String> gamesListView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gamesListView.setItems(games);
		gamesListView.setOnMouseClicked(event -> {
//			TODO: Handle connect to game
		});
	}

	/**
	 * Sets new games to list.
	 *
	 * @param games new games to set.
	 */
	public void setGames(List<String> games) {
		this.games.clear();
		this.games.addAll(games);
	}

	/**
	 * Goes back to menu.
	 */
	@FXML
	public void back() {
		mainWindowController.showMenu();
	}
}
