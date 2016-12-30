#ifndef QUARTETTESERVER_GAME_H
#define QUARTETTESERVER_GAME_H

#include <list>
#include <thread>
#include <deque>
#include <algorithm>
#include "Player.h"
#include "Message.h"

using std::list;
using std::deque;

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
	 * Deque of all cards shuffled at the beginning of the game and then redistributed to players.
	 */
	deque<Card> allCards;

	/**
	 * Indicates whether game thread should run.
	 */
	bool run;

	/**
	 * Player who's turn it is.
	 */
	Player *whosTurn;

	/**
	 * Setups game before start.
	 */
	void setupGame();

	/**
	 * Manages game and it's clients.
	 */
	void manageGame();

	/**
	 * Sends start game message to all players in game.
	 */
	void sendStartGame();

	/**
	 * Sends your turn message to the player and someone's turn message to other players.
	 * @param p player whose turn it is.
	 */
	void sendYourTurn(Player *p);

	/**
	 * Finds player with specified file descriptor.
	 * @param fd file descriptor of the player to find.
	 * @return player with specified file descriptor or NULL if not found.
	 */
	Player *findPlayerByFd(int fd);

	/**
	 * Moves card from one player to another one.
	 * @param c card to move.
	 * @param from player from whom the card should be moved.
	 * @param to player to whom the card should be moved.
	 */
	void moveCard(Card c, Player *from, Player *to);

	/**
	 * Shuffles cards to be dealt to players.
	 */
	void shuffleCards();

	/**
	 * Deals cards to player.
	 */
	void dealCards();

	/**
	 * Broadcasts message due to unresponsive player or due to player correctly exiting.
	 */
	void failGame(Player *p);

	/**
	 * Broadcasts message to all players in game. If player is specified, message is not sent to him.
	 */
	void broadcast(Message m, Player *p = NULL);

	/**
	 * Validates move, sends answer and info to others.
	 * @param m message with info about move.
	 */
	void sendMoveAnswer(Message m, Player *to);

public:

	/**
	 * Constructor to create new game with id, capacity and first player.
	 */
	Game(unsigned long id, int capacity, Player *p);

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
	 * Getter for player who's turn it is.
	 * @return player who's turn it is.
	 */
	Player *getWhosTurn();

	/**
	 * Adds player to game.
	 */
	void addPlayer(Player *p);

	/**
	 * Removes player from game.
	 */
	void removePlayer(Player *p);

	/**
	 * Finds player with specified name in game.
	 * @param name name of the player to find.
	 * @return player with specified name or NULL if not found.
	 */
	Player *findPlayerByName(string name);

	/**
	 * Indicates whether number of players in game is same as maximum number of players in game.
	 * @return true if number of players equals maximum number of players in game, false otherwise.
	 */
	bool isFull();

	/**
	 * Creates string with game state according to the specified player - number of opponents, names of opponent and
	 * card counts of opponents.
	 * @return state of game according to the specified player.
	 */
	string getStateOfGame(Player *p);

	/**
	 * Starts game thread.
	 */
	void start();

};

#endif //QUARTETTESERVER_GAME_H
