#ifndef QUARTETTESERVER_SERVER_H
#define QUARTETTESERVER_SERVER_H

#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/ioctl.h>
#include <stdlib.h>
#include <malloc.h>
#include <arpa/inet.h>
#include <list>
#include "game.h"
#include "message.h"

using std::list;

class Server {
	int serverSocket;
	int clientSocket;
	int fd;
	socklen_t addrLen;
	size_t toRead;
	struct sockaddr_in serverAddr;
	struct sockaddr_in clientAddr;
	fd_set clientSocks;
	fd_set tests;
	bool run;
	int numberOfGames;
	list<Game> games;

	int create(string, uint16_t);

	void processMessage(Message);

	void sendGameList();

	void connectToGame(Message);

	void createGame(Message);

	Game getGameById(int);

public:
	int start(string, uint16_t);
};

#endif //QUARTETTESERVER_SERVER_H
