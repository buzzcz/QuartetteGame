#ifndef QUARTETTESERVER_PLAYER_H
#define QUARTETTESERVER_PLAYER_H

#include <string>

using std::string;

/**
 * Represents player in game.
 */
class Player {

	/**
	 * Players file descriptor.
	 */
	int fd;

	/**
	 * Players nickname.
	 */
	string name;

public:

	/**
	 * Constructor for player with file descriptor and nickname.
	 */
	Player(int, string);

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

};

#endif //QUARTETTESERVER_PLAYER_H
