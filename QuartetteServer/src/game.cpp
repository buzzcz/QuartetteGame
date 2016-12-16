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
	list<Player *>::iterator iter;
	for (iter = players.begin(); iter != players.end(); iter++) {
		Player p1 = *(*iter);
		if (p1.getName().compare((*p).getName()) == 0) {
			players.erase(iter);
			return true;
		}
	}
	return false;
}

bool Game::isPlayerInGame(string nick) {
	list<Player *>::iterator iter;
	for (iter = players.begin(); iter != players.end(); iter++) {
		Player p = *(*iter);
		if (p.getName().compare(nick) == 0) {
			return true;
		}
	}
	return false;
}

bool Game::isFull() {
	return players.size() == capacity;
}

void Game::broadcast(Message m, Player *p) {
	list<Player *>::iterator iter;
	for (iter = players.begin(); iter != players.end(); iter++) {
		if (p == NULL || p != (*iter)) {
			Player p1 = *(*iter);
			m.sendMessage(p1.getFd());
		}
	}
}

string Game::getStateOfGame(Player *p) {
	string state = std::to_string(capacity - 1);
	string playerState;
	list<Player *>::iterator iter;
	for (iter = players.begin(); iter != players.end(); iter++) {
		if (p != (*iter)) {
			Player p1 = *(*iter);
			state += ",";
			state += p1.getName();
			state += ",";
//		    TODO: add cards count
		} else {
			Player p1 = *(*iter);
//		    TODO: add cards count and list of cards
		}
	}
	state += ",";
	state += playerState;
	return state;
}

void Game::start() {
	setupGame();
	manageGame();
}

void Game::setupGame() {
//	TODO: setup game as in Server::create
}

void Game::manageGame() {
//	TODO: manage game as in Server::run
}
