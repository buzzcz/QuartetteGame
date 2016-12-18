#include "game.h"

Game::Game(unsigned long id, int capacity, Player *p) : id(id), capacity(capacity) {
	addPlayer(p);
	std::thread(start());
//	TODO: wait for other players
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
}

bool Game::removePlayer(Player *p) {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Player p1 = *(*iter);
		if (p1.getName().compare((*p).getName()) == 0) {
			players.erase(iter);
			return true;
		}
	}
	return false;
}

bool Game::isPlayerInGame(string name) {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Player p = *(*iter);
		if (p.getName().compare(name) == 0) {
			return true;
		}
	}
	return false;
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
	setupGame();
	manageGame();
}

void Game::setupGame() {
//	TODO: setup game as in Server::create
	for (int i = 0; i < numberOfCards; i++) {
		Card c = static_cast<Card>(i);
		allCards.push_back(c);
	}
	std::random_shuffle(allCards.begin(), allCards.end());
}

void Game::manageGame() {
//	TODO: manage game as in Server::run
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
