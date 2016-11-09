#ifndef QUARTETTESERVER_GAME_H
#define QUARTETTESERVER_GAME_H

#include <list>
#include "player.h"

using std::list;

class Game {
	int id;
	int number_of_players;
	list<Player> players;
public:
	Game(int, int, Player);

	int get_id();

	int getNumber_of_players();

	list<Player> get_players();

	void add_player(Player);

	bool remove_player(Player);
};

#endif //QUARTETTESERVER_GAME_H
