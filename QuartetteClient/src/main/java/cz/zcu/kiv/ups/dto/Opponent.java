package cz.zcu.kiv.ups.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents opponent in game
 *
 * @author Jaroslav Klaus
 */
@AllArgsConstructor
@Data
public class Opponent {

	/**
	 * Opponent's name.
	 */
	private String name;

	/**
	 * Number of cards in his hand.
	 */
	private int numberOfCards;

	/**
	 * Increments number of cards.
	 */
	public void addCard() {
		numberOfCards++;
	}

	/**
	 * Decrements number of cards.
	 */
	public void removeCard() {
		numberOfCards--;
	}

}
