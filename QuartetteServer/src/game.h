#ifndef QUARTETTESERVER_GAME_H
#define QUARTETTESERVER_GAME_H

#include <list>
#include <thread>
#include "player.h"
#include "message.h"

using std::list;

/**
 * Represents game.
 */
class Game {

	/**
	 * Id of game.
	 */
	unsigned long id;

	/**
	 * Indicates maximum number of players in game.
	 */
	int capacity;

	/**
	 * List of players in game.
	 */
	list<Player *> players;

	/**
	 * Creates string with game state according to the specified player - number of opponents, names of opponent and
	 * card counts of opponents.
	 * @return state of game according to the specified player.
	 */
	string getStateOfGame(Player *);

	/**
	 * Setups game before start.
	 */
	void setupGame();

	/**
	 * Manages game and it's clients.
	 */
	void manageGame();

public:

	/**
	 * Constructor to create new game with id, capacity and first player.
	 */
	Game(unsigned long, int, Player *);

	/**
	 * Getter for id of game.
	 * @return id of game.
	 */
	unsigned long getId();

	/**
	 * Getter for maximum number of players in game.
	 * @return maximum number of player in game.
	 */
	int getCapacity();

	/**
	 * Getter for list of players in game.
	 * @return list of players in game.
	 */
	list<Player *> getPlayers();

	/**
	 * Adds player to game.
	 */
	void addPlayer(Player *);

	/**
	 * Removes player from game.
	 * @return true if success, false otherwise.
	 */
	bool removePlayer(Player *);

	/**
	 * Searches for player with nickname in game.
	 * @return true if player with nickname was found in game, false otherwise.
	 */
	bool isPlayerInGame(string);

	/**
	 * Indicates whether number of players in game is same as maximum number of players in game.
	 * @return true if number of players equals maximum number of players in game, false otherwise.
	 */
	bool isFull();

	/**
	 * Broadcasts message to all players in game. If player is specified, message is not sent to him.
	 */
	void broadcast(Message, Player * = NULL);

	/**
	 * Starts game thread.
	 */
	void start();

};

#endif //QUARTETTESERVER_GAME_H
