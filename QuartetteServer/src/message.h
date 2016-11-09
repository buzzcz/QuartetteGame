#ifndef QUARTETTESERVER_MESSAGE_H
#define QUARTETTESERVER_MESSAGE_H

#include <string>
#include <sys/socket.h>
#include <stdlib.h>

using std::string;

class Message {
	int type;
	unsigned long size;
	string data;

public:
	Message();

	Message(int, string);

	void receive_message(int, size_t);
};

#endif //QUARTETTESERVER_MESSAGE_H
