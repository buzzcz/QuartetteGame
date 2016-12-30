#include "Game.h"

Game::Game(unsigned long id, int capacity, Player *p) : id(id), capacity(capacity) {
	addPlayer(p);
	std::thread(start());
}

unsigned long Game::getId() {
	return id;
}

int Game::getCapacity() {
	return capacity;
}

list<Player *> Game::getPlayers() {
	return players;
}

Player *Game::getWhosTurn() {
	return whosTurn;
}

void Game::addPlayer(Player *p) {
	players.push_back(p);
//	TODO: add fd into fd_set for listening
}

void Game::removePlayer(Player *p) {
	players.remove(p);
//	TODO: Remove fd from fd_set
}

bool Game::isFull() {
	return players.size() == capacity;
}

void Game::broadcast(Message m, Player *p) {
	for (Player *p1 : players) {
		if (p == NULL || p != p1) {
			m.sendMessage(p1->getFd());
		}
	}
}

string Game::getStateOfGame(Player *p) {
	string state = std::to_string(capacity - 1);
	for (Player *p1 : players) {
		if (p != p1) {
			state += ",";
			state += p1->getName();
			state += ",";
			state += std::to_string(p1->getCards().size());
		}
	}
	state += ",";
	state += std::to_string(p->getCards().size());
	for (Card c : p->getCards()) {
		state += ",";
		state += cardNames[c];
	}
	return state;
}

void Game::start() {
	run = true;
	setupGame();
	manageGame();
}

void Game::setupGame() {
//	TODO: setup game as in Server::create
	FD_ZERO(&clientSocks);
	for (Player *p : players) {

	}
	shuffleCards();
	while (run) {
		if (isFull()) {
			manageGame();
		}
	}
}

void Game::manageGame() {
	dealCards();
	sendStartGame();
	while (run) {
//	    TODO: manage game as in Server::run
	}
}

void Game::sendStartGame() {
	for (Player *p : players) {
		Message m(START_OF_GAME, getStateOfGame(p));
		m.sendMessage(p->getFd());
	}
}

void Game::sendYourTurn(Player *p) {
	whosTurn = p;
	Message m(YOUR_TURN, "");
	m.sendMessage(p->getFd());

	Message m1(SOMEONES_TURN, p->getName());
	broadcast(m1, p);
}

Player *Game::findPlayerByName(string name) {
	for (Player *p : players) {
		if (p->getName().compare(name) == 0) {
			return p;
		}
	}
	return NULL;
}

Player *Game::findPlayerByFd(int fd) {
	for (Player *p : players) {
		if (p->getFd() == fd) {
			return p;
		}
	}
	return NULL;
}

void Game::moveCard(Card c, Player *from, Player *to) {
	from->removeCard(c);
	to->addCard(c);

	if (from->getCards().size() == 0) {
		Message m(YOU_LOST, "");
		m.sendMessage(from->getFd());
		Message m1(SOMEONE_LOST, from->getName());
		broadcast(m1, from);
		removePlayer(from);
	}

	if (to->hasQuartette()) {
		Message m(YOU_WON, "");
		m.sendMessage(to->getFd());
		Message m1(SOMEONE_WON, to->getName());
		broadcast(m1, to);
	}
}

void Game::shuffleCards() {
	for (int i = 0; i < NUMBER_OF_CARDS; i++) {
		Card c = static_cast<Card>(i);
		allCards.push_back(c);
	}
	std::random_shuffle(allCards.begin(), allCards.end());
}

void Game::dealCards() {
	list<Player *>::iterator playerIter = players.begin();
	for (int i = 0; i < NUMBER_OF_CARDS; i++) {
		Card c = *allCards.begin();
		allCards.pop_front();
		Player p = *(*playerIter);
		p.addCard(c);
		playerIter++;
		if (playerIter == players.end()) {
			playerIter = players.begin();
		}
	}
}

void Game::failGame(Player *p) {
	Message m(PLAYER_UNREACHABLE, p->getName());
	broadcast(m, p);
	run = false;
}

void Game::sendMoveAnswer(Message m, Player *to) {
	string data = m.getData();
	unsigned long i = data.find(",");
//	TODO: if == string::npos error
	string fromNick = data.substr(0, i);
	data.erase(0, i + 1);
	string cardName = data;
	Card card = static_cast<Card>(std::distance(std::begin(cardNames), std::find(std::begin(cardNames), std::end
			(cardNames), cardName)));
	Player *from = findPlayerByName(fromNick);

	int has = 1;
	if (from->hasCard(card)) {
		has = 0;
	}
	Message m1(YOUR_MOVE_ANSWER, std::to_string(has));
	m1.sendMessage(to->getFd());
	data = std::to_string(has);
	data += ",";
	data += to->getName();
	data += ",";
	data += cardNames[card];
	data += ",";
	data += from->getName();
	Message m2(SOMEONES_MOVE, data);
	broadcast(m2, to);
	if (has == 0) {
		moveCard(card, from, to);
	}
}
