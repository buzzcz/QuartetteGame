#include "Server.h"

int Server::create(string address, uint16_t port) {
	unsigned long i;
	int returnValue;

	for (i = 0; i < address.length(); i++) {
		address.at(i) = (char) toupper(address.at(i));
	}

	serverSocket = socket(AF_INET, SOCK_STREAM, 0);
	memset(&serverAddr, 0, sizeof(struct sockaddr_in));
	serverAddr.sin_family = AF_INET;
	if (address.compare("INADDR_ANY") == 0) {
		serverAddr.sin_addr.s_addr = htons(INADDR_ANY);
	} else if (address.compare("LOCALHOST") == 0) {
		serverAddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
	} else {
		returnValue = inet_pton(serverAddr.sin_family, address.c_str(), &serverAddr.sin_addr);
		if (i != 1) {
			printf("IP address is not valid.\n");
			return (returnValue == 0 ? -1 : returnValue);
		}
	}
	if (port > 1 && port < 65535) {
		serverAddr.sin_port = htons(port);
	} else {
		printf("Port must be bigger then 1 and smaller then 65636.\n");
		return (port == 0 ? -1 : port);
	}

	returnValue = bind(serverSocket, (struct sockaddr *) &serverAddr, sizeof(struct sockaddr_in));
	if (returnValue == 0) {
		printf("Bind OK.\n");
	} else {
		printf("Bind error.\n");
		return returnValue;
	}

	returnValue = listen(serverSocket, 5);
	if (returnValue == 0) {
		printf("Listen OK.\n");
	} else {
		printf("Listen error.\n");
		return returnValue;
	}

	FD_ZERO(&clientSocks);
	FD_SET(serverSocket, &clientSocks);
	return 0;
}

int Server::start(string address, uint16_t port) {
	int returnValue = create(address, port);

	if (returnValue != 0) {
		return returnValue;
	}

	run = true;

//	TODO: statistics
//	TODO: sigIntHandler (Ctrl + C)
	while (run) {
		fd_set tests = clientSocks;
		struct timeval t;
		t.tv_sec = 0;
		t.tv_usec = 10000;
		returnValue = select(FD_SETSIZE, &tests, (fd_set *) 0, (fd_set *) 0, &t);
		if (returnValue < 0) {
			printf("Select error.\n");
			return returnValue;
		}
		// exclude stdin, stdout, stderr
		for (fd = 3; fd < FD_SETSIZE; fd++) {
			if (FD_ISSET(fd, &tests)) {
				if (fd == serverSocket) {
					clientSocket = accept(serverSocket, (struct sockaddr *) &clientAddr, &addrLen);
					FD_SET(clientSocket, &clientSocks);
					printf("New connection accepted.\n");
				} else {
					int toRead = 0;
					ioctl(fd, FIONREAD, &toRead);
					if (toRead > 0) {
						Message *m = new Message();
						m->receiveMessage(fd);
						processMessage(*m);
					} else {
						printf("Client %d disconnected.\n", fd);
						close(fd);
						FD_CLR(fd, &clientSocks);
					}
				}
			}
		}
	}

	return 0;
}

void Server::processMessage(Message m) {
	switch (m.getType()) {
		case LIST_OF_GAMES_REQUEST:
			sendGameList();
			break;
		case CONNECT_REQUEST:
			connectToGame(m);
			break;
		case CREATE_GAME_REQUEST:
			createGame(m);
			break;
		case RECONNECT_REQUEST:
			reconnectToGame(m);
			break;
		default:
			break;
	}
}

void Server::sendGameList() {
	string data;
	data += std::to_string(games.size());

	for (Game *g : games) {
		data += ",";
		data += g->getId();
		data += ",";
		data += std::to_string(g->getPlayers().size());
		data += ",";
		data += std::to_string(g->getCapacity());
	}

	Message m(LIST_OF_GAMES_ANSWER, data);
	m.sendMessage(fd);
	printf("Sending game list \"%s\" to %d.\n", data.c_str(), fd);
}

void Server::createGame(Message m) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if i == string::npos error
	string nick = data.substr(0, i);
	data.erase(0, i + 1);
	int capacity = std::stoi(data, NULL, 10) + 1;

	if (isPlayerOnServer(nick)) {
		Message m1(CREATE_GAME_ANSWER, "1");
		m1.sendMessage(fd);
		printf("Error while creating game - player with the nickname is already on server.\n");
		return;
	}

	if (capacity <= 2) {
		Message m1(CREATE_GAME_ANSWER, "2");
		m1.sendMessage(fd);
		printf("Error while creating game - capacity is smaller then 3 or higher then 32.\n");
		return;
	}

	Game *newGame = new Game(capacity, &clientSocks, &games);
	Player *p = new Player(fd, nick);
	newGame->addPlayer(p);
	FD_CLR(fd, &clientSocks);
	games.push_back(newGame);

	Message m1(CREATE_GAME_ANSWER, "0");
	m1.sendMessage(fd);
	printf("Created game %s.\n", newGame->getId().c_str());
}

void Server::connectToGame(Message m) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if == string::npos error
	string nick = data.substr(0, i);
	data.erase(0, i + 1);
	string id = data;

	Game *g = getGameById(id);
	if (g == NULL) {
		Message m1(CONNECT_ANSWER, "3");
		m1.sendMessage(fd);
		printf("Error in connect to game %s - game no longer exists.\n", id.c_str());
		return;
	}

	if (g->getCapacity() == g->getPlayers().size()) {
		Message m1(CONNECT_ANSWER, "1");
		m1.sendMessage(fd);
		printf("Error in connect to game %s - game is full.\n", id.c_str());
		return;
	}

	Player *p = g->getPlayerByName(nick);
	if (p != NULL) {
		Message m1(CONNECT_ANSWER, "2");
		m1.sendMessage(fd);
		printf("Error in connect to game %s - player with the same nickname is already in game.\n", id.c_str());
		return;
	}

	g->addPlayer(new Player(fd, nick));
	FD_CLR(fd, &clientSocks);
	Message m1(CONNECT_ANSWER, "0");
	m1.sendMessage(fd);
	printf("Connected player %s to game %s.\n", nick.c_str(), id.c_str());
}

Game *Server::getGameById(string id) {
	for (Game *g : games) {
		if (g->getId().compare(id) == 0) {
			return g;
		}
	}
	return NULL;
}

bool Server::isPlayerOnServer(string name) {
	bool is = false;
	for (Game *g : games) {
		if (g->getPlayerByName(name) != NULL) {
			is = true;
			break;
		}
	}
	return is;
}

void Server::reconnectToGame(Message m) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if == string::npos error
	string nick = data.substr(0, i);
	data.erase(0, i + 1);
	string id = data;

	Game *g = getGameById(id);
	if (g == NULL) {
		Message m1(RECONNECT_ANSWER, "3");
		m1.sendMessage(fd);
		printf("Error in reconnect to game %s - game no longer exists.\n", id.c_str());
		return;
	}

	Player *p = g->getPlayerByName(nick);
	if (p == NULL) {
		Message m1(RECONNECT_ANSWER, "1");
		m1.sendMessage(fd);
		printf("Error in reconnect to game %s - player %s not found in game.\n", id.c_str(), nick.c_str());
		return;
	}

	if (p->getStatus() == ACTIVE) {
		Message m1(RECONNECT_ANSWER, "2");
		m1.sendMessage(fd);
		printf("Error in reconnect to game %s - player %s is still active.\n", id.c_str(), nick.c_str());
		return;
	}

	if (!g->isFull()) {
		Message m1(RECONNECT_ANSWER, "4");
		m1.sendMessage(fd);
//		TODO: Modify player's fd.
		printf("Reconnected to game %s - game hasn't started yet.\n", id.c_str());
		return;
	}

	Message m1(RECONNECT_ANSWER, g->getStateOfGame(p));
	m1.sendMessage(fd);

	if (g->getWhosTurn() == p) {
		Message m2(YOUR_TURN, "");
		m.sendMessage(fd);
	} else {
		Message m2(SOMEONES_TURN, g->getWhosTurn()->getName());
		m2.sendMessage(fd);
	}

	int oldFd = p->getFd();
//	TODO: Remove old fd from fd_set and add new
	p->setFd(fd);
	FD_CLR(fd, &clientSocks);
	p->setStatus(ACTIVE);
//	TODO: Solve locking - what if in between messages someone makes move??
	printf("Reconnected to game %s.\n", id.c_str());
}
