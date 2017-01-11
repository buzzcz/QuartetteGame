#include "Server.h"

bool Server::run = false;

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

	signal(SIGPIPE, SIG_IGN);
	signal(SIGINT, Server::stop);
	signal(SIGTERM, Server::stop);

	time(&Statistics::start);

	std::thread(&Server::sendKeepAlives, this).detach();
	std::thread(&Server::checkKeepAlives, this).detach();

	run = true;

	while (run) {
		fd_set tests;
		{
			std::lock_guard<std::mutex> lock(clientsMutex);
			tests = clientSocks;
		}
		struct timeval t;
		t.tv_sec = 0;
		t.tv_usec = 10000;
		returnValue = select(FD_SETSIZE, &tests, (fd_set *) 0, (fd_set *) 0, &t);
		if (returnValue < 0) {
			continue;
		}
		// exclude stdin, stdout, stderr
		for (fd = 3; fd < FD_SETSIZE; fd++) {
			if (FD_ISSET(fd, &tests)) {
				if (fd == serverSocket) {
					std::lock_guard<std::mutex> lock(clientsMutex);
					clientSocket = accept(serverSocket, (struct sockaddr *) &clientAddr, &addrLen);
					Player *p = new Player(clientSocket);
					clients.push_back(p);
					FD_SET(clientSocket, &clientSocks);
					Statistics::acceptedConnections.fetch_add(1);
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
						closeFd(0, false);
					}
				}
			}
		}
	}

	time(&Statistics::end);
	printStats();

	return 0;
}

void Server::processMessage(Message m) {
	switch (m.getType()) {
		case KEEP_ALIVE:
			keepAlive();
			break;
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
		case UNPARSEABLE:
		default:
			closeFd(0, true);
			break;
	}
}

void Server::closeFd(int fdToClose, bool error) {
	if (fdToClose == 0) {
		fdToClose = fd;
	}
	std::lock_guard<std::mutex> lock(clientsMutex);
	Player *p = getClientByFd(fdToClose);
	clients.remove(p);
	close(fdToClose);
	FD_CLR(fdToClose, &clientSocks);
	if (error) {
		printf("Client %d sent unparseable message, disconnecting.\n", fdToClose);
		Statistics::closedClients.fetch_add(1);
	}
}

void Server::sendGameList() {
	string data;
	data += std::to_string(games.size());

	std::lock_guard<std::mutex> lock1(gamesMutex);
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
	if (i == string::npos) {
		closeFd(0, true);
		return;
	}
	string nick = data.substr(0, i);
	data.erase(0, i + 1);
	int capacity;
	try {
		capacity = std::stoi(data, NULL, 10) + 1;
	} catch (std::invalid_argument e) {
		closeFd(0, true);
		return;
	}

	std::lock_guard<std::mutex> lock(clientsMutex);
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

	std::lock_guard<std::mutex> lock1(gamesMutex);
	Game *newGame = new Game(capacity, &clientSocks, &clients, &games, std::ref(clientsMutex), std::ref(gamesMutex));
	Player *p = getClientByFd(fd);
	p->setName(nick);
	clients.remove(p);
	FD_CLR(fd, &clientSocks);
	newGame->addPlayer(p);
	games.push_back(newGame);

	Message m1(CREATE_GAME_ANSWER, "0");
	m1.sendMessage(fd);
	printf("Created game %s.\n", newGame->getId().c_str());
}

void Server::connectToGame(Message m) {
	string data = m.getData();
	unsigned long i = data.find(",");
	if (i == string::npos) {
		closeFd(0, true);
		return;
	}
	string nick = data.substr(0, i);
	data.erase(0, i + 1);
	string id = data;

	std::lock_guard<std::mutex> lock(clientsMutex);
	std::lock_guard<std::mutex> lock1(gamesMutex);
	Game *g = getGameById(id);
	if (g == NULL) {
		Message m1(CONNECT_ANSWER, "3");
		m1.sendMessage(fd);
		printf("Error in connect to game %s - game no longer exists.\n", id.c_str());
		return;
	}

	if (g->isFull() || g->getStatus() == ACTIVE) {
		Message m1(CONNECT_ANSWER, "1");
		m1.sendMessage(fd);
		printf("Error in connect to game %s - game is full.\n", id.c_str());
		return;
	}

	if (isPlayerOnServer(nick)) {
		Message m1(CONNECT_ANSWER, "2");
		m1.sendMessage(fd);
		printf("Error in connect to game %s - player with the same nickname is already on server.\n", id.c_str());
		return;
	}

	Player *p = getClientByFd(fd);
	p->setName(nick);
	clients.remove(p);
	FD_CLR(fd, &clientSocks);
	g->addPlayer(p);
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

Game *Server::getGameByPlayersName(string name) {
	for (Game *g : games) {
		if (g->getPlayerByName(name) != NULL) {
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

Player *Server::getClientByFd(int fd) {
	for (Player *p: clients) {
		if (p->getFd() == fd) {
			return p;
		}
	}
	return NULL;
}

void Server::reconnectToGame(Message m) {
	string name = m.getData();

	std::lock_guard<std::mutex> lock(clientsMutex);
	std::lock_guard<std::mutex> lock1(gamesMutex);
	Game *g = getGameByPlayersName(name);
	if (g == NULL) {
		Message m1(RECONNECT_ANSWER, "1");
		m1.sendMessage(fd);
		printf("Error in reconnect to game - player not found in any game.\n");
		return;
	}

	Player *p = g->getPlayerByName(name);
	if (p->getStatus() == ACTIVE) {
		Message m1(RECONNECT_ANSWER, "2");
		m1.sendMessage(fd);
		printf("Error in reconnect to game %s - player %s is still active.\n", g->getId().c_str(), name.c_str());
		return;
	}

	if (!g->isFull()) {
		clients.remove(getClientByFd(fd));
		g->reconnectPlayer(p, fd);
		Message m1(RECONNECT_ANSWER, "3");
		m1.sendMessage(fd);
		printf("Reconnected to game %s - game hasn't started yet.\n", g->getId().c_str());
		return;
	}

	clients.remove(getClientByFd(fd));
	g->reconnectPlayer(p, fd);
	string data = "0";
	data += ",";
	data += g->getStateOfGame(p);
	Message m1(RECONNECT_ANSWER, data);
	m1.sendMessage(fd);

	if (g->getWhosTurn() == p) {
		Message m2(YOUR_TURN, "");
		m2.sendMessage(fd);
	} else {
		Message m2(SOMEONES_TURN, g->getWhosTurn()->getName());
		m2.sendMessage(fd);
	}
	printf("Reconnected %s to game %s.\n", name.c_str(), g->getId().c_str());
}

void Server::keepAlive() {
	std::lock_guard<std::mutex> lock(clientsMutex);
	Player *p = getClientByFd(fd);
	if (p != NULL) {
		time_t now;
		time(&now);
		p->setLastReceivedKeepAlive(now);
		p->setStatus(ACTIVE);
	}
}

void Server::sendKeepAlives() {
	Message m(KEEP_ALIVE, "");
	while (run) {
		{
			std::lock_guard<std::mutex> lock(clientsMutex);
			std::lock_guard<std::mutex> lock1(gamesMutex);
			for (Player *p : clients) {
				m.sendMessage(p->getFd());
			}

			for (Game *g : games) {
				for (Player *p : g->getPlayers()) {
					m.sendMessage(p->getFd());
				}
			}
		}
		std::this_thread::sleep_for(std::chrono::milliseconds(2500));
	}
}

void Server::checkKeepAlives() {
	while (run) {
		{
			std::lock_guard<std::mutex> lock(clientsMutex);
			std::lock_guard<std::mutex> lock1(gamesMutex);
			printf("Checking keep-alive clients.\n");
			list<Player *> l = clients;
			for (Player *p : l) {
				time_t now;
				time(&now);
				double diff = difftime(now, p->getLastReceivedKeepAlive());
				if (p->getStatus() == NOT_ACTIVE && diff >= 25) {
					printf("Closing unresponding client %d.\n", p->getFd());
					closeFd(p->getFd(), true);
				} else if (p->getStatus() == ACTIVE && diff >= 5) {
					printf("Waiting for unresponding client %d.\n", p->getFd());
					p->setStatus(NOT_ACTIVE);
				}
			}

			printf("Checking keep-alive players.\n");
			list<Game *> lg = games;
			for (Game *g : lg) {
				l = g->getPlayers();
				for (Player *p : l) {
					time_t now;
					time(&now);
					double diff = difftime(now, p->getLastReceivedKeepAlive());
					if (p->getStatus() == NOT_ACTIVE && diff >= 25) {
						printf("Closing unresponding player %d.\n", p->getFd());
						if (g->isFull() || g->getStatus() == ACTIVE) {
							g->failGame(p, true);
						} else {
							if (g->getPlayers().size() == 1) {
								run = false;
							} else {
								g->removePlayer(p, false);
							}
						}
					} else if (p->getStatus() == ACTIVE && diff >= 5) {
						printf("Waiting for unresponding player %d.\n", p->getFd());
						p->setStatus(NOT_ACTIVE);
					}
				}
			}
		}
		std::this_thread::sleep_for(std::chrono::milliseconds(2500));
	}
}

void Server::printStats() {
	double runTime = difftime(Statistics::end, Statistics::start);
	printf("\nSTATISTICS:\n");
	printf("Server run for %f seconds,\n", runTime);
	printf("received %lu bytes and %lu messages,\n", Statistics::receivedBytes.load(), Statistics::receivedMessages
			.load());
	printf("sent %lu bytes and %lu messages,\n", Statistics::sentBytes.load(), Statistics::sentMessages.load());
	printf("accepted %lu connections and disconnected %lu clients because of errors.\n",
	       Statistics::acceptedConnections.load(), Statistics::closedClients.load());
}

void Server::stop(int sigNum) {
	run = false;
}
