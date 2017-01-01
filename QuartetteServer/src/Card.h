#ifndef QUARTETTESERVER_CARD_H
#define QUARTETTESERVER_CARD_H

#include <string>

using std::string;

/**
 * Represents cards.
 */
enum Card {
	CARD_1A,
	CARD_2A,
	CARD_3A,
	CARD_4A,
	CARD_5A,
	CARD_6A,
	CARD_7A,
	CARD_8A,
	CARD_1B,
	CARD_2B,
	CARD_3B,
	CARD_4B,
	CARD_5B,
	CARD_6B,
	CARD_7B,
	CARD_8B,
	CARD_1C,
	CARD_2C,
	CARD_3C,
	CARD_4C,
	CARD_5C,
	CARD_6C,
	CARD_7C,
	CARD_8C,
	CARD_1D,
	CARD_2D,
	CARD_3D,
	CARD_4D,
	CARD_5D,
	CARD_6D,
	CARD_7D,
	CARD_8D,
	NUMBER_OF_CARDS
};

/**
 * Card string representations.
 */
//TODO: Consider creating map instead.
const string cardNames[] = {
		"1A",
		"2A",
		"3A",
		"4A",
		"5A",
		"6A",
		"7A",
		"8A",
		"1B",
		"2B",
		"3B",
		"4B",
		"5B",
		"6B",
		"7B",
		"8B",
		"1C",
		"2C",
		"3C",
		"4C",
		"5C",
		"6C",
		"7C",
		"8C",
		"1D",
		"2D",
		"3D",
		"4D",
		"5D",
		"6D",
		"7D",
		"8D"
};

#endif //QUARTETTESERVER_CARD_H
