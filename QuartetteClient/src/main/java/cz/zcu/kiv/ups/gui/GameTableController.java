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
public class GameTableController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	private ObservableList<String> cards = FXCollections.observableList(new LinkedList<>());

	@FXML
	private ListView<String> cardsListView;

	private ObservableList<String> opponents = FXCollections.observableList(new LinkedList<>());

	@FXML
	private ListView<String> opponentsListView;

	private ObservableList<String> history = FXCollections.observableList(new LinkedList<>());

	@FXML
	public ListView<String> historyListView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cardsListView.setItems(cards);
		opponentsListView.setItems(opponents);
		historyListView.setItems(history);
	}

	public void fillCards(List<String> cards) {
		this.cards.clear();
		this.cards.addAll(cards);
	}

	public void fillOpponents(List<String> opponents) {
		this.opponents.clear();
		this.opponents.addAll(opponents);
	}

	@FXML
	public void exit() {
		mainWindowController.exitGame();
	}
}
