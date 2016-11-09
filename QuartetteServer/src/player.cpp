#include "player.h"

Player::Player(int fd, string name) : fd(fd), name(name) {}

int Player::get_fd() {
	return fd;
}

string Player::get_name() {
	return name;
}
