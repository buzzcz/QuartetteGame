#include "Game.h"

Game::Game(int capacity, fd_set *serverClients, list<Game *> *serverGames) : capacity(capacity),
                                                                             serverClients(serverClients),
                                                                             serverGames(serverGames) {
	uuid_t uuid;
	uuid_generate_random(uuid);
	char s[37];
	uuid_unparse(uuid, s);
	id.assign(s);
	std::thread(&Game::start, this).detach();
}

string Game::getId() {
	return id;
}

int Game::getCapacity() {
	return capacity;
}

list<Player *> Game::getPlayers() {
	return players;
}

Player *Game::getWhosTurn() {
	return whosTurn;
}

void Game::addPlayer(Player *p) {
	players.push_back(p);
	FD_SET(p->getFd(), &clientSocks);
}

void Game::removePlayer(Player *p) {
	FD_CLR(p->getFd(), &clientSocks);
	FD_SET(p->getFd(), serverClients);
	players.remove(p);
}

bool Game::isFull() {
	return players.size() == capacity;
}

void Game::broadcast(Message m, Player *p) {
	printf("Broadcast in game %s.\n", id.c_str());
	for (Player *p1 : players) {
		if (p == NULL || p != p1) {
			m.sendMessage(p1->getFd());
		}
	}
}

string Game::getStateOfGame(Player *p) {
	string state = std::to_string(capacity - 1);
	for (Player *p1 : players) {
		if (p != p1) {
			state += ",";
			state += p1->getName();
			state += ",";
			state += std::to_string(p1->getCards().size());
		}
	}
	state += ",";
	state += std::to_string(p->getCards().size());
	for (Card c : p->getCards()) {
		state += ",";
		state += cardNames[c];
	}
	printf("State of game %s for player %s is \"%s\".\n", id.c_str(), p->getName().c_str(), state.c_str());
	return state;
}

void Game::start() {
	run = true;
	setupGame();
}

void Game::checkForMessages() {
	fd_set tests = clientSocks;
	int returnValue;
	struct timeval t;
	t.tv_sec = 0;
	t.tv_usec = 10000;
	returnValue = select(FD_SETSIZE, &tests, (fd_set *) 0, (fd_set *) 0, &t);
	if (returnValue < 0) {
		printf("Select error.\n");
//		TODO: error
	}
	// exclude stdin, stdout, stderr
	for (fd = 3; fd < FD_SETSIZE; fd++) {
		if (FD_ISSET(fd, &tests)) {
			int toRead = 0;
			ioctl(fd, FIONREAD, &toRead);
			if (toRead > 0) {
				Message *m = new Message();
				m->receiveMessage(fd);
				processMessage(*m);
			} else {
//				TODO: Is this block necessary??
				printf("Client %d disconnected.\n", fd);
				close(fd);
				FD_CLR(fd, &clientSocks);
			}
		}
	}
}

void Game::processMessage(Message m) {
	switch (m.getType()) {
		case MOVE:
			if (isFull()) {
				sendMoveAnswer(m, getPlayerByFd(fd));
			}
			break;
		case DISCONNECTING:
			failGame(getPlayerByFd(fd));
			break;
		default:
			break;
	}
}

void Game::setupGame() {
	FD_ZERO(&clientSocks);
	std::srand((unsigned int) std::time(0));
	shuffleCards();
	while (run) {
		checkForMessages();
		if (isFull()) {
			manageGame();
		}
	}
}

void Game::manageGame() {
	dealCards();
	bool ok = false;
	int i = 0;
	while (!ok) {
		i++;
		ok = checkCards();
		if (!ok) {
			if (i == 50) {
				std::srand((unsigned int) std::time(0));
			}
			shuffleCards();
			dealCards();
		}
	}
	sendStartGame();
	Player *p = players.front();
	sendYourTurn(p);
	while (run) {
		checkForMessages();
	}
	endGame();
}

void Game::endGame() {
	list<Player *> temp = players;
	for (Player *p : temp) {
		removePlayer(p);
	}
	serverGames->remove(this);
}

void Game::sendStartGame() {
	for (Player *p : players) {
		Message m(START_OF_GAME, getStateOfGame(p));
		m.sendMessage(p->getFd());
	}
	printf("Game %s started.\n", id.c_str());
}

void Game::sendYourTurn(Player *p) {
	whosTurn = p;
	Message m(YOUR_TURN, "");
	m.sendMessage(p->getFd());

	Message m1(SOMEONES_TURN, p->getName());
	broadcast(m1, p);

	printf("%s's turn in game %s.\n", p->getName().c_str(), id.c_str());
}

Player *Game::getPlayerByName(string name) {
	for (Player *p : players) {
		if (p->getName().compare(name) == 0) {
			return p;
		}
	}
	return NULL;
}

Player *Game::getPlayerByFd(int fd) {
	for (Player *p : players) {
		if (p->getFd() == fd) {
			return p;
		}
	}
	return NULL;
}

void Game::moveCard(Card c, Player *from, Player *to) {
	from->removeCard(c);
	to->addCard(c);
	printf("Moved card from %s to %s in game %s.\n", from->getName().c_str(), to->getName().c_str(), id.c_str());
}

void Game::shuffleCards() {
	for (int i = 0; i < NUMBER_OF_CARDS; i++) {
		Card c = static_cast<Card>(i);
		allCards.push_back(c);
	}
	std::random_shuffle(allCards.begin(), allCards.end());
}

void Game::dealCards() {
	for (Player *p : players) {
		p->clearCards();
	}
	list<Player *>::iterator playerIter = players.begin();
	for (int i = 0; i < NUMBER_OF_CARDS; i++) {
		Card c = *allCards.begin();
		allCards.pop_front();
		Player *p = *playerIter;
		p->addCard(c);
		playerIter++;
		if (playerIter == players.end()) {
			playerIter = players.begin();
		}
	}
}

bool Game::checkCards() {
	bool ok = true;
	for (Player *p : players) {
		if (p->hasQuartette()) {
			ok = false;
			break;
		}
	}
	return ok;
}

void Game::failGame(Player *p) {
	Message m(PLAYER_UNREACHABLE, p->getName());
	broadcast(m, p);
	run = false;
	printf("%s is unreachable or exited game %s.\n", p->getName().c_str(), id.c_str());
}

void Game::sendMoveAnswer(Message m, Player *to) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if == string::npos error
	string fromNick = data.substr(0, i);
	data.erase(0, i + 1);
	string cardName = data;
	Card card = static_cast<Card>(std::distance(std::begin(cardNames), std::find(std::begin(cardNames), std::end
			(cardNames), cardName)));
	Player *from = getPlayerByName(fromNick);

	int has = 1;
	if (from->hasCard(card)) {
		has = 0;
		moveCard(card, from, to);
	}
	Message m1(MOVE_ANSWER, std::to_string(has));
	m1.sendMessage(to->getFd());
	data = std::to_string(has);
	data += ",";
	data += to->getName();
	data += ",";
	data += cardNames[card];
	data += ",";
	data += from->getName();
	Message m2(SOMEONES_MOVE, data);
	broadcast(m2, to);
	printf("%s wanted %s from %s in game %s.\n", to->getName().c_str(), cardName.c_str(), from->getName().c_str(), id
			.c_str());
	if (has == 0) {

		if (from->getCards().size() == 0) {
			Message m3(YOU_LOST, "");
			m3.sendMessage(from->getFd());
			Message m4(SOMEONE_LOST, from->getName());
			broadcast(m4, from);
			removePlayer(from);
			printf("%s lost in game %s.\n", from->getName().c_str(), id.c_str());
		}

		if (to->hasQuartette()) {
			Message m3(YOU_WON, "");
			m3.sendMessage(to->getFd());
			Message m4(SOMEONE_WON, to->getName());
			broadcast(m4, to);
			printf("%s won in game %s.\n", to->getName().c_str(), id.c_str());
			run = false;
			return;
		}

		sendYourTurn(to);
	} else {
		sendYourTurn(from);
	}
}
