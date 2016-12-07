#include "player.h"

Player::Player(int fd, string name) : fd(fd), name(name) {}

int Player::getFd() {
	return fd;
}

string Player::getName() {
	return name;
}
