#ifndef QUARTETTESERVER_GAME_H
#define QUARTETTESERVER_GAME_H

#include <list>
#include "player.h"

using std::list;

class Game {
	int id;
	int numberOfPlayers;
	list<Player> players;
public:
	Game(int, int, Player);

	int getId();

	int getNumberOfPlayers();

	list<Player> getPlayers();

	void addPlayer(Player);

	bool removePlayer(Player);
};

#endif //QUARTETTESERVER_GAME_H
