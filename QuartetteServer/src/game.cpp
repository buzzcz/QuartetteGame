#include "game.h"

Game::Game(int id, int number_of_players, Player p) : id(id), number_of_players(number_of_players) {
	add_player(p);
//	TODO: create thread to handle players
}

int Game::get_id() {
	return id;
}

int Game::getNumber_of_players() {
	return number_of_players;
}

list<Player> Game::get_players() {
	return players;
}

void Game::add_player(Player p) {
	players.push_back(p);
}

bool Game::remove_player(Player p) {
	list<Player>::iterator iter;
	for (iter = players.begin(); iter != players.end(); iter++) {
		if ((*iter).get_name().compare(p.get_name()) == 0) {
			break;
		}
	}
	bool contains = (iter != players.end());
	if (contains) {
		players.erase(iter);
	}
	return contains;
}
