#ifndef QUARTETTESERVER_GAME_H
#define QUARTETTESERVER_GAME_H

#include <list>
#include <thread>
#include <mutex>
#include <deque>
#include <algorithm>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/ioctl.h>
#include <uuid/uuid.h>
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
	string id;

	/**
	 * Indicates maximum number of players in game.
	 */
	int capacity;

	/**
	 * Status of game.
	 * ACTIVE means that game is running, NOT_ACTIVE means that game hasn't begun yet.
	 */
	Status status;

	/**
	 * Set of server clients.
	 */
	fd_set *serverClientsFdSet;

	/**
	 * List of server clients.
	 */
	list<Player *> *serverClients;

	/**
	 * List of games on server.
	 */
	list<Game *> *serverGames;

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
	 * File Descriptor of last client.
	 */
	int fd;

	/**
	 * File descriptor of player that caused failing the game.
	 */
	int errorFd;

	/**
	 * Set of client sockets.
	 */
	fd_set clientSocks;

	std::mutex &serverClientsMutex;

	std::mutex &serverGamesMutex;

	/**
	 * Runs select and receives new messages.
	 * @return 0 if everything is ok, fd to be closed otherwise.
	 */
	void checkForMessages();

	/**
	 * Processes received message.
	 * @param m message to process.
	 * @return 0 if everything is ok, fd to be closed otherwise.
	 */
	void processMessage(Message m);

	/**
	 * Setups game before start.
	 */
	void setupGame();

	/**
	 * Manages game and it's clients.
	 * @return 0 if everything is ok, fd to be closed otherwise.
	 */
	void manageGame();

	/**
	 * Ends game. Returns players' fds to server etc.
	 * @param fd param to be closed and not returned to server.
	 */
	void endGame();

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
	Player *getPlayerByFd(int fd);

	/**
	 * Removes player from game.
	 * @param p player to remove.
	 * @param backToServer true if player should be returned to server, false if connection to player should be closed.
	 */
	void removePlayer(Player *p, bool backToServer);

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
	 * Checks if any player has quartette.
	 * @return false if any player has quartette, true otherwise.
	 */
	bool checkCards();

	/**
	 * Checks if any player has quartette. If so, shuffles and deals cards again.
	 */
	void prepareToRun();

	/**
	 * Broadcasts message to all players in game. If player is specified, message is not sent to him.
	 * @param m message to broadcast.
	 * @param p if specified, message won't be broadcasted to this player.
	 */
	void broadcast(Message m, Player *p = NULL);

	/**
	 * Validates move, sends answer and info to others.
	 * @param m message with info about move.
	 * @param to player who's move it is.
	 * @return 0 if everything is ok, fd to be closed otherwise.
	 */
	void sendMoveAnswer(Message m, Player *to);

	/**
	 * Sets time of last received keep-alive.
	 */
	void keepAlive();

public:

	/**
	 * Constructor to create new game with id and capacity.
	 * @param capacity maximum number of players in this game.
	 * @param serverClientsFdSet fd_set of server clients.
	 * @param serverClients list of server clients.
	 * @param serverGames list of games on server.
	 */
	Game(int capacity, fd_set *serverClientsFdSet, list<Player *> *serverClients, list<Game *> *serverGames,
	     std::mutex &serverClientsMutex, std::mutex &serverGamesMutex);

	/**
	 * Getter for id of game.
	 * @return id of game.
	 */
	string getId();

	/**
	 * Getter for maximum number of players in game.
	 * @return maximum number of player in game.
	 */
	int getCapacity();

	/**
	 * Getter for status of game.
	 * @return status of game.
	 */
	Status getStatus();

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
	 * @param p player to add.
	 */
	void addPlayer(Player *p);

	/**
	 * Finds player with specified name in game.
	 * @param name name of the player to find.
	 * @return player with specified name or NULL if not found.
	 */
	Player *getPlayerByName(string name);

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

	/**
	 * Broadcasts message due to unresponsive player or due to player correctly exiting.
	 * @param p player because of whom the game failed.
	 */
	void failGame(Player *p, bool removePlayer);

	/**
	 * Reconnects player into game.
	 * @param p player to reconnect.
	 */
	void reconnectPlayer(Player *p, int newFd);

};

#endif //QUARTETTESERVER_GAME_H
