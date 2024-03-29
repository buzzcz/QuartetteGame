#include "Player.h"

Player::Player(int fd) : fd(fd) {
	status = ACTIVE;
	time(&lastReceivedKeepAlive);
}

Player::Player(int fd, string name) : fd(fd), name(name) {
	status = ACTIVE;
	time(&lastReceivedKeepAlive);
}

int Player::getFd() {
	return fd;
}

void Player::setFd(int newFd) {
	fd = newFd;
}

string Player::getName() {
	return name;
}

void Player::setName(string n) {
	name = n;
}

Status Player::getStatus() {
	return status;
}

void Player::setStatus(Status s) {
	status = s;
}

list<Card> Player::getCards() {
	return cards;
}

bool Player::hasCard(Card c) {
	for (Card c1 : cards) {
		if (c == c1) {
			return true;
		}
	}
	return false;
}

void Player::clearCards() {
	cards.clear();
}

void Player::addCard(Card c) {
	cards.push_back(c);
}

void Player::removeCard(Card c) {
	cards.remove(c);
}

bool Player::hasQuartette() {
	return (hasCard(CARD_1A) && hasCard(CARD_1B) && hasCard(CARD_1C) && hasCard(CARD_1D)) ||
	       (hasCard(CARD_2A) && hasCard(CARD_2B) && hasCard(CARD_2C) && hasCard(CARD_2D)) ||
	       (hasCard(CARD_3A) && hasCard(CARD_3B) && hasCard(CARD_3C) && hasCard(CARD_3D)) ||
	       (hasCard(CARD_4A) && hasCard(CARD_4B) && hasCard(CARD_4C) && hasCard(CARD_4D)) ||
	       (hasCard(CARD_5A) && hasCard(CARD_5B) && hasCard(CARD_5C) && hasCard(CARD_5D)) ||
	       (hasCard(CARD_6A) && hasCard(CARD_6B) && hasCard(CARD_6C) && hasCard(CARD_6D)) ||
	       (hasCard(CARD_7A) && hasCard(CARD_7B) && hasCard(CARD_7C) && hasCard(CARD_7D)) ||
	       (hasCard(CARD_8A) && hasCard(CARD_8B) && hasCard(CARD_8C) && hasCard(CARD_8D));
}

time_t Player::getLastReceivedKeepAlive() {
	return lastReceivedKeepAlive;
}

void Player::setLastReceivedKeepAlive(time_t time) {
	lastReceivedKeepAlive = time;
}
