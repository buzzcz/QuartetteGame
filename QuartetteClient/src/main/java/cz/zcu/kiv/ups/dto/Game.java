package cz.zcu.kiv.ups.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents game to which player can connect.
 *
 * @author Jaroslav Klaus
 */
@AllArgsConstructor
@Data
public class Game {

	/**
	 * Id of game.
	 */
	private String id;

	/**
	 * Number of connected players.
	 */
	private int numberOfConnected;

	/**
	 * Number of maximum players.
	 */
	private int capacity;

}
