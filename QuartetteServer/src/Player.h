#ifndef QUARTETTESERVER_PLAYER_H
#define QUARTETTESERVER_PLAYER_H

#include <list>
#include "Card.h"
#include "Status.h"

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
	 * Player's status.
	 */
	Status status;

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
	 * Setter for file descriptor.
	 * @param newFd file descriptor.
	 */
	void setFd(int newFd);

	/**
	 * Getter for nickname.
	 * @return nickname.
	 */
	string getName();

	/**
	 * Getter for status.
	 * @return status.
	 */
	Status getStatus();

	/**
	 * Setter for status.
	 * @param s status.
	 */
	void setStatus(Status s);

	/**
	 * Getter for cards.
	 * @return list of cards.
	 */
	list<Card> getCards();

	/**
	 * Clears list of cards.
	 */
	void clearCards();

	/**
	 * Checks if player has specified card.
	 * @param c card to be looked for.
	 * @return true if player has the card, false otherwise.
	 */
	bool hasCard(Card c);

	/**
	 * Adds card to player's cards.
	 * @param c card to be added.
	 */
	void addCard(Card c);

	/**
	 * Removes card from player's cards.
	 * @param c card to be removed.
	 */
	void removeCard(Card c);

	/**
	 * Checks whether player has quartette.
	 * @return true if player has quartette, false otherwise.
	 */
	bool hasQuartette();

};

#endif //QUARTETTESERVER_PLAYER_H
