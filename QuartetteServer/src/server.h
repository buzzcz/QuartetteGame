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
#include "game.h"
#include "message.h"

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
	 * Indicates how many byte are in socket to read.
	 */
	size_t toRead;

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
	bool run;

	/**
	 * Indicates number of games.
	 */
	unsigned long numberOfGames;

	/**
	 * List of running games.
	 */
	list<Game *> games;

	/**
	 * Creates server - checks input values, binds and listens on socket.
	 * @return 0 if creating of server is successful, error number otherwise.
	 */
	int create(string address, uint16_t port);

	/**
	 * Processes incoming message.
	 */
	void processMessage(Message m);

	/**
	 * Send list of games.
	 */
	void sendGameList();

	/**
	 * Creates new game and adds first player into it.
	 */
	void createGame(Message m);

	/**
	 * Connects player to game.
	 */
	void connectToGame(Message m);

	/**
	 * Searches list of game for a game with specified id.
	 * @return game with specified id or NULL if game wasn't found.
	 */
	Game *getGameById(int id);

	/**
	 * Reconnects player to game.
	 * @param m message with data.
	 */
	void reconnectToGame(Message m);

public:

	/**
	 * Starts server.
	 * @return 0 if everything is successful, error number otherwise.
	 */
	int start(string address, uint16_t port);

};

#endif //QUARTETTESERVER_SERVER_H
