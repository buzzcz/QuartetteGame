#ifndef QUARTETTESERVER_CARD_H
#define QUARTETTESERVER_CARD_H

#include <string>

using std::string;

/**
 * Represents cards.
 */
enum Card {
	CARD_1A,
	CARD_1B,
	CARD_1C,
	CARD_1D,
	CARD_2A,
	CARD_2B,
	CARD_2C,
	CARD_2D,
	CARD_3A,
	CARD_3B,
	CARD_3C,
	CARD_3D,
	CARD_4A,
	CARD_4B,
	CARD_4C,
	CARD_4D,
	CARD_5A,
	CARD_5B,
	CARD_5C,
	CARD_5D,
	CARD_6A,
	CARD_6B,
	CARD_6C,
	CARD_6D,
	CARD_7A,
	CARD_7B,
	CARD_7C,
	CARD_7D,
	CARD_8A,
	CARD_8B,
	CARD_8C,
	CARD_8D,
	NUMBER_OF_CARDS
};

/**
 * Card string representations.
 */
//TODO: Consider creating map instead.
const string cardNames[] = {
		"1A",
		"1B",
		"1C",
		"1D",
		"2A",
		"2B",
		"2C",
		"2D",
		"3A",
		"3B",
		"3C",
		"3D",
		"4A",
		"4B",
		"4C",
		"4D",
		"5A",
		"5B",
		"5C",
		"5D",
		"6A",
		"6B",
		"6C",
		"6D",
		"7A",
		"7B",
		"7C",
		"7D",
		"8A",
		"8B",
		"8C",
		"8D"
};

#endif //QUARTETTESERVER_CARD_H
