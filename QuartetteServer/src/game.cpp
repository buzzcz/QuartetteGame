#include "game.h"

Game::Game(int id, int numberOfPlayers, Player p) : id(id), numberOfPlayers(numberOfPlayers) {
	addPlayer(p);
//	TODO: create thread to handle players
}

int Game::getId() {
	return id;
}

int Game::getNumberOfPlayers() {
	return numberOfPlayers;
}

list<Player> Game::getPlayers() {
	return players;
}

void Game::addPlayer(Player p) {
	players.push_back(p);
}

bool Game::removePlayer(Player p) {
	list<Player>::iterator iter;
	for (iter = players.begin(); iter != players.end(); iter++) {
		if ((*iter).getName().compare(p.getName()) == 0) {
			break;
		}
	}
	bool contains = (iter != players.end());
	if (contains) {
		players.erase(iter);
	}
	return contains;
}
