#ifndef QUARTETTESERVER_PLAYER_H
#define QUARTETTESERVER_PLAYER_H

#include <list>
#include <string>
#include "card.h"

using std::string;
using std::list;

/**
 * Represents player in game.
 */
class Player {

	/**
	 * Player's file descriptor.
	 */
	int fd;

	/**
	 * Player's nickname.
	 */
	string name;

	/**
	 * Player's cards.
	 */
	list<Card> cards;

public:

	/**
	 * Constructor for player with file descriptor and nickname.
	 */
	Player(int fd, string name);

	/**
	 * Getter for file descriptor.
	 * @return file descriptor.
	 */
	int getFd();

	/**
	 * Getter for nickname.
	 * @return nickname.
	 */
	string getName();

	/**
	 * Getter for cards.
	 * @return list of cards.
	 */
	list<Card> getCards();

};

#endif //QUARTETTESERVER_PLAYER_H
