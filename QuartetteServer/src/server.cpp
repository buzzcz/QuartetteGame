#include "server.h"

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

	while (run) {
		tests = clientSocks;
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
						Message m;
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

	for (list<Game>::iterator iter = games.begin(); iter != games.end(); iter++) {
		data += ",";
		data += std::to_string((*iter).getId());
		data += std::to_string((*iter).getPlayers().size());
		data += std::to_string((*iter).getNumberOfPlayers());
	}

	Message m(2, data);
	data = m.getMessageToSend();
	send(fd, data.c_str(), data.length(), 0);
}

void Server::connectToGame(Message m) {
//	TODO: connect to game and respond

}

void Server::createGame(Message m) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if i == string::npos error
	string nick = data.substr(i);
	Player p(fd, nick);
	data.erase(i + 1);
	int numberOfPlayers = strtol(data.substr(i).c_str(), NULL, 10);

	Game newGame(numberOfGames++, numberOfPlayers, p);
	games.push_back(newGame);

	Message m1(6, std::to_string(newGame.getId()));
	data = m1.getMessageToSend();
	send(fd, data.c_str(), data.length(), 0);
}
