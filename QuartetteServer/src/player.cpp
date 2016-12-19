#include "player.h"

Player::Player(int fd, string name) : fd(fd), name(name) {}

int Player::getFd() {
	return fd;
}

string Player::getName() {
	return name;
}

Status Player::getStatus() {
	return status;
}

list<Card> Player::getCards() {
	return cards;
}

bool Player::hasCard(Card c) {
	for (list<Card>::iterator iter = cards.begin(); iter != cards.end(); iter++) {
		if (c == *iter) {
			return true;
		}
	}
	return false;
}

void Player::addCard(Card c) {
	cards.push_back(c);
}

void Player::removeCard(Card c) {
	cards.remove(c);
}

bool Player::hasQuartette() {
	return (hasCard(card1A) && hasCard(card2A) && hasCard(card3A) && hasCard(card4A)) ||
	       (hasCard(card1B) && hasCard(card2B) && hasCard(card3B) && hasCard(card4B)) ||
	       (hasCard(card1C) && hasCard(card2C) && hasCard(card3C) && hasCard(card4C)) ||
	       (hasCard(card1D) && hasCard(card2D) && hasCard(card3D) && hasCard(card4D));
}
