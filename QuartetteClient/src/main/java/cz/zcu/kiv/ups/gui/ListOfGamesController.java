package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.dto.Game;
import cz.zcu.kiv.ups.dto.Message;
import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.utils.AlertsAndDialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

	@Autowired
	private Connection connection;

	/**
	 * List of games.
	 */
	private ObservableList<Game> games = FXCollections.observableList(new LinkedList<>());

	/**
	 * List View of games.
	 */
	@FXML
	private ListView<Game> gamesListView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gamesListView.setItems(games);
		gamesListView.setCellFactory(param -> new ListCell<Game>() {
			@Override
			protected void updateItem(Game item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
				} else {
					setText(String.format("Id: %d, connected players: %d, capacity: %d.", item.getId(), item
							.getNumberOfConnected(), item.getCapacity()));
				}
			}
		});
		gamesListView.setOnMouseClicked(event -> {
//			TODO: Handle connect to game
			Optional<ButtonType> result = AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.CONFIRMATION, "Connect to" +
					" Game", "Do you want to connect to selected game?", null);
			if (result.get() == ButtonType.OK) {
				Game game = gamesListView.getSelectionModel().getSelectedItem();
				Message m = new Message(3, String.format("%s,%d", mainWindowController.getNickname(), game.getId()));
				connection.sendMessage(m);
			}
		});
	}

	/**
	 * Sets new games to list.
	 *
	 * @param games new games to set.
	 */
	public void setGames(List<Game> games) {
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
