#ifndef QUARTETTESERVER_SERVER_H
#define QUARTETTESERVER_SERVER_H

#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/ioctl.h>
#include <malloc.h>
#include <arpa/inet.h>
#include <list>
#include <thread>
#include <mutex>
#include <signal.h>
#include <chrono>
#include "Game.h"

using std::list;

/**
 * Represents Server that serves clients that are not in game.
 */
class Server {

	/**
	 * Server Socket.
	 */
	int serverSocket;

	/**
	 * Client Socket of last client.
	 */
	int clientSocket;

	/**
	 * File Descriptor of last client.
	 */
	int fd;

	/**
	 * Length of client address.
	 */
	socklen_t addrLen;

	/**
	 * Server Address.
	 */
	struct sockaddr_in serverAddr;

	/**
	 * Client Address of last client.
	 */
	struct sockaddr_in clientAddr;

	/**
	 * Set of client sockets.
	 */
	fd_set clientSocks;

	/**
	 * Indicates whether server loop should run.
	 */
	static bool run;

	/**
	 * List of server clients.
	 */
	list<Player *> clients;

	/**
	 * List of running games.
	 */
	list<Game *> games;

	/**
	 * Mutex to avoid race condition on clients list.
	 */
	std::mutex clientsMutex;

	/**
	 * Mutex to avoid race condition on games list.
	 */
	std::mutex gamesMutex;

	/**
	 * Creates server - checks input values, binds and listens on socket.
	 * @param address address of server.
	 * @param port port of server.
	 * @return 0 if creating of server is successful, error number otherwise.
	 */
	int create(string address, uint16_t port);

	/**
	 * Processes incoming message.
	 * @param m message to process.
	 */
	void processMessage(Message m);

	/**
	 * Closes connection to file descriptor.
	 * @param fdToClose file descriptor to close. If not specified, last file descriptor is closed.
	 */
	void closeFd(int fdToClose, bool error);

	/**
	 * Send list of games.
	 */
	void sendGameList();

	/**
	 * Creates new game and adds first player into it.
	 * @param m message with info about game.
	 */
	void createGame(Message m);

	/**
	 * Connects player to game.
	 * @param m message with info abou game.
	 */
	void connectToGame(Message m);

	/**
	 * Searches list of game for a game with specified id.
	 * @param id id of game to find.
	 * @return game with specified id or NULL if game wasn't found.
	 */
	Game *getGameById(string id);

	/**
	 * Finds game with specified player in it.
	 * @param nickname of player that should be in the game.
	 * @return game with player in it or null.
	 */
	Game *getGameByPlayersName(string name);

	/**
	 * Finds player by nickname on server.
	 * @param name nickname of player to find.
	 * @return true if player is on server, false otherwise.
	 */
	bool isPlayerOnServer(string name);

	/**
	 * Finds server client by file descriptor.
	 * @param fd file descriptor of client to find.
	 * @return found client or NULL.
	 */
	Player *getClientByFd(int fd);

	/**
	 * Reconnects player to game.
	 * @param m message with data.
	 */
	void reconnectToGame(Message m);

	/**
	 * Sets new time of last keep-alive to client.
	 */
	void keepAlive();

	/**
	 * Sends keep-alive message to all clients and players.
	 */
	void sendKeepAlives();

	/**
	 * Checks times of last received keep-alives of all clients / players and either changes client's / player's status
	 * or disconnects client / player.
	 */
	void sendAndCheckKeepAlives();

	/**
	 * Prints statistics.
	 */
	void printStats();

	/**
	 * Stops the server.
	 */
	static void stop(int sigNum);

public:

	/**
	 * Starts server.
	 * @param address address of server.
	 * @param port port of server.
	 * @return 0 if everything is successful, error number otherwise.
	 */
	int start(string address, uint16_t port);

};

#endif //QUARTETTESERVER_SERVER_H
