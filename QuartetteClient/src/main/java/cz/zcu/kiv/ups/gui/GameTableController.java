package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.dto.Card;
import cz.zcu.kiv.ups.dto.Opponent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
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
	private ObservableList<Card> cards = FXCollections.observableList(new LinkedList<>());

	/**
	 * List View of my cards.
	 */
	@FXML
	private ListView<Card> cardsListView;

	/**
	 * List of my opponents.
	 */
	private ObservableList<Opponent> opponents = FXCollections.observableList(new LinkedList<>());

	/**
	 * List View of my opponents.
	 */
	@FXML
	private ListView<Opponent> opponentsListView;

	/**
	 * History of game.
	 */
	@Getter
	private ObservableList<String> history = FXCollections.observableList(new LinkedList<>());

	/**
	 * List View of game history.
	 */
	@FXML
	public ListView<String> historyListView;

	/**
	 * Player's last move.
	 */

	public Pair<Opponent, Card> lastMove;

	/**
	 * Indicated whether it is my turn or not.
	 */
	@Setter
	private boolean myTurn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cardsListView.setItems(cards);
		cardsListView.setCellFactory(param -> new ListCell<Card>() {
			@Override
			protected void updateItem(Card item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});

		opponentsListView.setItems(opponents);
		opponentsListView.setCellFactory(param -> new ListCell<Opponent>() {
			@Override
			protected void updateItem(Opponent item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
				} else {
					setText(String.format("Name: %s, number of cards: %d", item.getName(), item.getNumberOfCards()));
				}
			}
		});
		opponentsListView.setOnMouseClicked(event -> {
			if (myTurn) {
//				TODO: Show dialog with cards and send move.
				myTurn = false;
			}
		});

		historyListView.setItems(history);
	}

	/**
	 * Sets new cards to list.
	 *
	 * @param cards new cards to set.
	 */
	public void setCards(List<Card> cards) {
		this.cards.clear();
		this.cards.addAll(cards);
	}

	/**
	 * Sets new opponents to list.
	 *
	 * @param opponents new opponents to set.
	 */
	public void setOpponents(List<Opponent> opponents) {
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

	/**
	 * Adds card from move to player's cards and decrements opponent's number of cards.
	 */
	public void moveSuccessful() {
		cards.add(lastMove.getValue());
		opponents.get(opponents.indexOf(lastMove.getKey())).removeCard();
		cardsListView.refresh();
		opponentsListView.refresh();
	}

	/**
	 * Updates number of cards of specified players.
	 *
	 * @param first  player who got new card.
	 * @param card   card that was moved between players.
	 * @param second player from whom is the card taken.
	 */
	public void someonesMoveSuccessful(String first, Card card, String second) {
		for (Opponent o : opponents) {
			if (o.getName().equals(first)) {
				o.addCard();
				break;
			}
		}
		if (second.equals(mainWindowController.getNickname())) {
			cards.remove(card);
			cardsListView.refresh();
		} else {
			for (Opponent o : opponents) {
				if (o.getName().equals(second)) {
					o.removeCard();
					break;
				}
			}
		}
		opponentsListView.refresh();
	}
}
