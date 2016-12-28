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
	return (hasCard(card1A) && hasCard(card1B) && hasCard(card1C) && hasCard(card1D)) ||
	       (hasCard(card2A) && hasCard(card2B) && hasCard(card2C) && hasCard(card2D)) ||
	       (hasCard(card3A) && hasCard(card3B) && hasCard(card3C) && hasCard(card3D)) ||
	       (hasCard(card4A) && hasCard(card4B) && hasCard(card4C) && hasCard(card4D)) ||
	       (hasCard(card5A) && hasCard(card5B) && hasCard(card5C) && hasCard(card5D)) ||
	       (hasCard(card6A) && hasCard(card6B) && hasCard(card6C) && hasCard(card6D)) ||
	       (hasCard(card7A) && hasCard(card7B) && hasCard(card7C) && hasCard(card7D)) ||
	       (hasCard(card8A) && hasCard(card8B) && hasCard(card8C) && hasCard(card8D));
}
