#ifndef QUARTETTESERVER_PLAYER_H
#define QUARTETTESERVER_PLAYER_H

#include <string>

using std::string;

class Player {
	int fd;
	string name;
public:
	Player(int, string);

	int get_fd();

	string get_name();
};

#endif //QUARTETTESERVER_PLAYER_H
