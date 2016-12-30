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
			printf("IP address is not valid\n");
			return (returnValue == 0 ? -1 : returnValue);
		}
	}
	if (port > 1 && port < 65535) {
		serverAddr.sin_port = htons(port);
	} else {
		printf("Port must be bigger then 1 and smaller then 65636\n");
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

	numberOfGames = 0;

//	TODO: statistics
//	TODO: sigIntHandler (Ctrl + C)
	while (run) {
		fd_set tests = clientSocks;
		returnValue = select(FD_SETSIZE, &tests, (fd_set *) 0, (fd_set *) 0, (struct timeval *) 0);
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
					toRead = 0;
					ioctl(fd, FIONREAD, &toRead);
					if (toRead > 0) {
						Message *mp = new Message();
						Message m = *mp;
						m.receiveMessage(fd, toRead);
						processMessage(m);
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
		case 1:
			sendGameList();
			break;
		case 3:
			connectToGame(m);
			break;
		case 5:
			createGame(m);
			break;
		default:
			break;
	}
}

void Server::sendGameList() {
	string data;
	data += std::to_string(numberOfGames);

	for (list<Game *>::iterator iter = games.begin(); iter != games.end(); iter++) {
		Game g = *(*iter);
		data += ",";
		data += std::to_string(g.getId());
		data += ",";
		data += std::to_string(g.getPlayers().size());
		data += ",";
		data += std::to_string(g.getCapacity());
	}

	Message m(2, data);
	m.sendMessage(fd);
}

void Server::createGame(Message m) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if i == string::npos error
	string nick = data.substr(0, i);
	Player *p = new Player(fd, nick);
	data.erase(0, i + 1);
	int capacity = std::stoi(data.c_str(), NULL, 10) + 1;
	if (capacity <= 2) {
		Message m1(6, "-1");
		m1.sendMessage(fd);
		return;
	}

//	TODO: if numberOfGames == ULONG_MAX, send error
	Game *newGame = new Game(numberOfGames++, capacity, p);
	games.push_back(newGame);

	Message m1(6, std::to_string((*newGame).getId()));
	m1.sendMessage(fd);
}

void Server::connectToGame(Message m) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if == string::npos error
	string nick = data.substr(0, i);
	data.erase(0, i + 1);
	int id = std::stoi(data.c_str(), NULL, 10);

	Game *g = getGameById(id);
	if (g == NULL) {
		Message m1(4, "3");
		m1.sendMessage(fd);
		return;
	}

	if (g->getCapacity() == g->getPlayers().size()) {
		Message m1(4, "1");
		m1.sendMessage(fd);
		return;
	}

	Player *p = g->findPlayerByName(nick);
	if (p != NULL) {
		Message m1(4, "2");
		m1.sendMessage(fd);
		return;
	}

	g->addPlayer(new Player(fd, nick));
	Message m1(4, "0");
	m1.sendMessage(fd);

	if (g->isFull()) {
		for (list<Player *>::iterator iter = g->getPlayers().begin(); iter != g->getPlayers().end(); iter++) {
			Player p1 = *(*iter);
			FD_CLR(p1.getFd(), &clientSocks);
		}
	}
}

Game *Server::getGameById(int id) {
	for (list<Game *>::iterator iter = games.begin(); iter != games.end(); iter++) {
		Game g = *(*iter);
		if (g.getId() == id) {
			return (*iter);
		}
	}
	return NULL;
}

void Server::reconnectToGame(Message m) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if == string::npos error
	string nick = data.substr(0, i);
	data.erase(0, i + 1);
	int id = std::stoi(data.c_str(), NULL, 10);

	Game *g = getGameById(id);
	if (g == NULL) {
		Message m1(19, "3");
		m1.sendMessage(fd);
		return;
	}

	Player *p = g->findPlayerByName(nick);
	if (p == NULL) {
		Message m1(19, "1");
		m1.sendMessage(fd);
		return;
	}

	if (p->getStatus() == ACTIVE) {
		Message m1(19, "2");
		m1.sendMessage(fd);
		return;
	}

//	TODO: modify player and resume game
}