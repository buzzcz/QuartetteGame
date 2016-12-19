#include "game.h"

Game::Game(unsigned long id, int capacity, Player *p) : id(id), capacity(capacity) {
	addPlayer(p);
	std::thread(start());
}

unsigned long Game::getId() {
	return id;
}

int Game::getCapacity() {
	return capacity;
}

list<Player *> Game::getPlayers() {
	return players;
}

void Game::addPlayer(Player *p) {
	players.push_back(p);
//	TODO: add fd into fd_set for listening
}

void Game::removePlayer(Player *p) {
	players.remove(p);
}

bool Game::isFull() {
	return players.size() == capacity;
}

void Game::broadcast(Message m, Player *p) {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		if (p == NULL || p != (*iter)) {
			Player p1 = *(*iter);
			m.sendMessage(p1.getFd());
		}
	}
}

string Game::getStateOfGame(Player *p) {
	string state = std::to_string(capacity - 1);
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		if (p != (*iter)) {
			Player p1 = *(*iter);
			state += ",";
			state += p1.getName();
			state += ",";
			state += std::to_string(p1.getCards().size());
		}
	}
	state += ",";
	state += std::to_string(p->getCards().size());
	for (list<Card>::iterator iter = p->getCards().begin(); iter != p->getCards().end(); iter++) {
		state += ",";
		state += cardNames[*iter];
	}
	return state;
}

void Game::start() {
	run = true;
	setupGame();
	manageGame();
}

void Game::setupGame() {
//	TODO: setup game as in Server::create
	shuffleCards();
	while (run) {
		if (isFull()) {
			manageGame();
		}
	}
}

void Game::manageGame() {
//	TODO: manage game as in Server::run
	dealCards();
}

void Game::sendStartGame() {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Message m(7, getStateOfGame(*iter));
		m.sendMessage((*iter)->getFd());
	}
}

void Game::sendYourTurn(Player *p) {
	Message m(8, "");
	m.sendMessage(p->getFd());

	Message m1(9, p->getName());
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Player p1 = *(*iter);
		if (p1.getName().compare(p->getName()) != 0) {
			m1.sendMessage(p1.getFd());
		}
	}
}

Player *Game::findPlayerByName(string name) {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Player p = *(*iter);
		if (p.getName().compare(name) == 0) {
			return *iter;
		}
	}
	return NULL;
}

Player *Game::findPlayerByFd(int fd) {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Player p = *(*iter);
		if (p.getFd() == fd) {
			return *iter;
		}
	}
	return NULL;
}

void Game::moveCard(Card c, Player *from, Player *to) {
	from->removeCard(c);
	to->addCard(c);

	if (from->getCards().size() == 0) {
		Message m(15, "");
		m.sendMessage(from->getFd());
		Message m1(16, from->getName());
		broadcast(m1, from);
	}

	if (to->hasQuartette()) {
		Message m(13, "");
		m.sendMessage(to->getFd());
		Message m1(14, to->getName());
		broadcast(m1, to);
	}
}

void Game::shuffleCards() {
	for (int i = 0; i < numberOfCards; i++) {
		Card c = static_cast<Card>(i);
		allCards.push_back(c);
	}
	std::random_shuffle(allCards.begin(), allCards.end());
}

void Game::dealCards() {
	list<Player *>::iterator playerIter = players.begin();
	for (int i = 0; i < numberOfCards; i++) {
		Card c = *allCards.begin();
		allCards.pop_front();
		Player p = *(*playerIter);
		p.addCard(c);
		playerIter++;
		if (playerIter == players.end()) {
			playerIter = players.begin();
		}
	}
}

void Game::failGame(Player *p) {
	Message m(17, p->getName());
	broadcast(m, p);
	run = false;
}
