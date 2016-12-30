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
}

bool Game::isFull() {
	return players.size() == capacity;
}

void Game::broadcast(Message m, Player *p) {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		if (p == NULL || p != (*iter)) {
			Player p1 = *(*iter);
			m.sendMessage(p1.getFd());
		}
	}
}

string Game::getStateOfGame(Player *p) {
	string state = std::to_string(capacity - 1);
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		if (p != (*iter)) {
			Player p1 = *(*iter);
			state += ",";
			state += p1.getName();
			state += ",";
			state += std::to_string(p1.getCards().size());
		}
	}
	state += ",";
	state += std::to_string(p->getCards().size());
	for (list<Card>::iterator iter = p->getCards().begin(); iter != p->getCards().end(); iter++) {
		state += ",";
		state += cardNames[*iter];
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
	shuffleCards();
	while (run) {
		if (isFull()) {
			manageGame();
		}
	}
}

void Game::manageGame() {
//	TODO: manage game as in Server::run
	dealCards();
}

void Game::sendStartGame() {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Message m(START_OF_GAME, getStateOfGame(*iter));
		m.sendMessage((*iter)->getFd());
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
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Player p = *(*iter);
		if (p.getName().compare(name) == 0) {
			return *iter;
		}
	}
	return NULL;
}

Player *Game::findPlayerByFd(int fd) {
	for (list<Player *>::iterator iter = players.begin(); iter != players.end(); iter++) {
		Player p = *(*iter);
		if (p.getFd() == fd) {
			return *iter;
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
	Card card = static_cast<Card>(std::find(cardNames, cardNames + cardNames->size(), cardName));
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
