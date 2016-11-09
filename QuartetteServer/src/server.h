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
	int server_socket;
	int client_socket;
	int fd;
	socklen_t addr_len;
	size_t to_read;
	struct sockaddr_in server_addr;
	struct sockaddr_in client_addr;
	fd_set client_socks;
	fd_set tests;
	bool run;
	int number_of_games;
	list<Game> games;

	int create(string, uint16_t);

public:
	void add_game(int, Player);

	int start(string, uint16_t);
};

#endif //QUARTETTESERVER_SERVER_H
