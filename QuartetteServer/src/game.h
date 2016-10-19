#ifndef QUARTETTESERVER_GAME_H
#define QUARTETTESERVER_GAME_H

#include "player.h"

struct game {
	struct player *players;

	struct game *next;
};

#endif
