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

	string getMessageToSend();
public:

	Message();

	Message(int, string);

	int getType();

	unsigned long getSize();

	string getData();

	void receiveMessage(int, size_t);

	void sendMessage(int);
};

#endif //QUARTETTESERVER_MESSAGE_H
