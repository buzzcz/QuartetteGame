package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.dto.Game;
import cz.zcu.kiv.ups.dto.Message;
import cz.zcu.kiv.ups.dto.MessageType;
import cz.zcu.kiv.ups.network.NetworkInterface;
import cz.zcu.kiv.ups.utils.AlertsAndDialogs;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ListOfGamesController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	@Autowired
	private NetworkInterface connection;

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
					setText(String.format("Connected players: %d, capacity: %d.", item.getNumberOfConnected(), item
							.getCapacity()));
				}
			}
		});
		gamesListView.setOnMouseClicked(event -> {
			Game game = gamesListView.getSelectionModel().getSelectedItem();
			if (game != null) {
				Optional<String> result = AlertsAndDialogs.showAndWaitTextInputDialog("Connect To Game",
						"Connect to game.", "Enter nickname:", "Nickname", mainWindowController.getNickname());
				result.ifPresent(s -> {
					Platform.runLater(mainWindowController::showWaitForServer);
					mainWindowController.setNickname(s);
					Message m = new Message(MessageType.CONNECT_REQUEST, String.format("%s,%s", s, game.getId()));
					connection.sendMessage(m);
					log.info(String.format("Connecting %s to game %s.", s, game.getId()));
				});
			}
		});
	}

	/**
	 * Sets new games to list.
	 *
	 * @param games new games to set.
	 */
	void setGames(List<Game> games) {
		this.games.clear();
		this.games.addAll(games);
	}

	/**
	 * Goes back to menu.
	 */
	@FXML
	private void back() {
		mainWindowController.showMenu();
	}
}
