#ifndef QUARTETTESERVER_GAME_H
#define QUARTETTESERVER_GAME_H

#include <list>
#include <thread>
#include "player.h"
#include "message.h"

using std::list;

class Game {
	int id;
	int capacity;
	list<Player *> players;

	string getStateOfGame(Player *);

	void setupGame();

	void manageGame();
public:

	Game(int, int, Player *);

	int getId();

	int getCapacity();

	list<Player *> getPlayers();

	void addPlayer(Player *);

	bool removePlayer(Player *);

	bool isPlayerInGame(string);

	bool shouldStart();

	void broadcast(Message, Player * = NULL);

	void start();
};

#endif //QUARTETTESERVER_GAME_H
