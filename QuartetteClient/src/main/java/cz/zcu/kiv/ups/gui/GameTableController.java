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
 * Controller of Game Table screen.
 *
 * @author Jaroslav Klaus
 */
@Component
public class GameTableController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	/**
	 * List of my cards.
	 */
	private ObservableList<String> cards = FXCollections.observableList(new LinkedList<>());

	/**
	 * List View of my cards.
	 */
	@FXML
	private ListView<String> cardsListView;

	/**
	 * List of my opponents.
	 */
	private ObservableList<String> opponents = FXCollections.observableList(new LinkedList<>());

	/**
	 * List View of my opponents.
	 */
	@FXML
	private ListView<String> opponentsListView;

	/**
	 * History of game.
	 */
	private ObservableList<String> history = FXCollections.observableList(new LinkedList<>());

	/**
	 * List View of game history.
	 */
	@FXML
	public ListView<String> historyListView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cardsListView.setItems(cards);
		opponentsListView.setItems(opponents);
		historyListView.setItems(history);
	}

	/**
	 * Sets new cards to list.
	 * @param cards new cards to set.
	 */
	public void setCards(List<String> cards) {
		this.cards.clear();
		this.cards.addAll(cards);
	}

	/**
	 * Sets new opponents to list.
	 * @param opponents new opponents to set.
	 */
	public void setOpponents(List<String> opponents) {
		this.opponents.clear();
		this.opponents.addAll(opponents);
	}

	/**
	 * Exits game and shows menu.
	 */
	@FXML
	public void exit() {
		mainWindowController.exitGame();
	}
}
