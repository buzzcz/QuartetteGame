package cz.zcu.kiv.ups.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents cards in deck.
 *
 * @author Jaroslav Klaus
 */
public enum Card {

	CARD_1A("1A"),
	CARD_1B("1B"),
	CARD_1D("1D"),
	CARD_1C("1C"),
	CARD_2A("2A"),
	CARD_2B("2B"),
	CARD_2C("2C"),
	CARD_2D("2D"),
	CARD_3A("3A"),
	CARD_3B("3B"),
	CARD_3C("3C"),
	CARD_3D("3D"),
	CARD_4A("4A"),
	CARD_4B("4B"),
	CARD_4C("4C"),
	CARD_4D("4D"),
	CARD_5A("5A"),
	CARD_5B("5B"),
	CARD_5C("5C"),
	CARD_5D("5D"),
	CARD_6A("6A"),
	CARD_6B("6B"),
	CARD_6C("6C"),
	CARD_6D("6D"),
	CARD_7A("7A"),
	CARD_7B("7B"),
	CARD_7C("7C"),
	CARD_7D("7D"),
	CARD_8A("8A"),
	CARD_8B("8B"),
	CARD_8C("8C"),
	CARD_8D("8D");

	/**
	 * Constructor to create card with its name.
	 *
	 * @param name name of the card.
	 */
	Card(String name) {
		this.name = name;
	}

	/**
	 * Name of the card.
	 */
	@Getter
	private final String name;

	/**
	 * Map of cards by their names.
	 */
	private static final Map<String, Card> cardByName;

	static {
		cardByName = new HashMap<>();
		for (Card c : Card.values()) {
			cardByName.put(c.getName(), c);
		}
	}

	/**
	 * Returns card instance by its name.
	 *
	 * @param name name of the card.
	 * @return card instance.
	 */
	public static Card getCardByName(String name) {
		return cardByName.get(name);
	}

}
