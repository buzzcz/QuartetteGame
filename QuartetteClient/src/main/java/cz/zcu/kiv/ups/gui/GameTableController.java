package cz.zcu.kiv.ups.gui;

import cz.zcu.kiv.ups.dto.Card;
import cz.zcu.kiv.ups.dto.Message;
import cz.zcu.kiv.ups.dto.MessageType;
import cz.zcu.kiv.ups.dto.Opponent;
import cz.zcu.kiv.ups.network.Connection;
import cz.zcu.kiv.ups.utils.AlertsAndDialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller of Game Table screen.
 *
 * @author Jaroslav Klaus
 */
@Component
@Slf4j
public class GameTableController implements Initializable {

	@Autowired
	private MainWindowController mainWindowController;

	@Autowired
	private Connection connection;

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
	private ListView<String> historyListView;

	/**
	 * Player's last move.
	 */
	@Getter
	private Pair<Opponent, Card> lastMove;

	/**
	 * Indicated whether it is my turn or not.
	 */
	@Setter
	private boolean myTurn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cardsListView.setItems(new SortedList<>(cards, Comparator.comparing(Card::getName)));
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
					setText(String.format("%s has %d cards.", item.getName(), item.getNumberOfCards()));
				}
			}
		});
		opponentsListView.setOnMouseClicked(event -> {
			if (myTurn) {
				Opponent o = opponentsListView.getSelectionModel().getSelectedItem();
				if (o != null) {
					List<String> choices = new LinkedList<>();
					for (Card c : Card.values()) {
						choices.add(c.getName());
					}
					Optional<String> result = AlertsAndDialogs.showAndWaitChoiceDialog("1A", choices, "Your Move",
							"Select a card.", "Select a card you want to get from selected player.");

					result.ifPresent(s -> {
						Message m = new Message(MessageType.MOVE, String.format("%s,%s", o.getName(), s));
						connection.sendMessage(m);
						myTurn = false;
						lastMove = new Pair<>(o, Card.getCardByName(s));
						log.info(String.format("I want %s from %s.", s, o.getName()));
					});
				}
			}
		});

		historyListView.setItems(history);
	}

	/**
	 * Sets new cards to list.
	 *
	 * @param cards new cards to set.
	 */
	void setCards(List<Card> cards) {
		this.cards.clear();
		this.cards.addAll(cards);
	}

	/**
	 * Sets new opponents to list.
	 *
	 * @param opponents new opponents to set.
	 */
	void setOpponents(List<Opponent> opponents) {
		this.opponents.clear();
		this.opponents.addAll(opponents);
	}

	/**
	 * Exits game and shows menu.
	 */
	@FXML
	private void exit() {
		mainWindowController.exitGame();
	}

	/**
	 * Adds card from move to player's cards and decrements opponent's number of cards.
	 */
	void moveSuccessful() {
		cards.add(lastMove.getValue());
		opponents.get(opponents.indexOf(lastMove.getKey())).removeCard();
		cardsListView.refresh();
		opponentsListView.refresh();
		log.info(String.format("I got %s from %s.", lastMove.getValue().getName(), lastMove.getKey().getName()));
	}

	/**
	 * Updates number of cards of specified players.
	 *
	 * @param first  player who got new card.
	 * @param card   card that was moved between players.
	 * @param second player from whom is the card taken.
	 */
	void someonesMoveSuccessful(String first, Card card, String second) {
		for (Opponent o : opponents) {
			if (o.getName().equals(first)) {
				o.addCard();
				break;
			}
		}
		if (second.equals(mainWindowController.getNickname())) {
			cards.remove(card);
			cardsListView.refresh();
			second = "me";
		} else {
			for (Opponent o : opponents) {
				if (o.getName().equals(second)) {
					o.removeCard();
					break;
				}
			}
		}
		opponentsListView.refresh();
		String info = String.format("%s took %s from %s.", first, card.getName(), second);
		history.add(info);
		log.info(info);
	}
}
