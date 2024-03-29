#ifndef QUARTETTESERVER_MESSAGE_H
#define QUARTETTESERVER_MESSAGE_H

#include <string>
#include <sys/socket.h>
#include <malloc.h>
#include <stdexcept>
#include "MessageType.h"
#include "Statistics.h"

using std::string;

/**
 * Represents message sent by server or client.
 */
class Message {

	/**
	 * Indicates type of the message.
	 */
	MessageType type;

	/**
	 * Size of message data.
	 */
	unsigned long size;

	/**
	 * Message data.
	 */
	string data;

	/**
	 * Creates string of message type, size and data to be sent.
	 * @return string of message type, size and data
	 */
	string getMessageToSend();

public:

	/**
	 * Constructor to create empty message.
	 */
	Message();

	/**
	 * Constructor to create message with type and data.
	 * @param type type of message.
	 * @param data message data.
	 */
	Message(MessageType type, string data);

	/**
	 * Getter for type of message.
	 * @return type of message.
	 */
	MessageType getType();

	/**
	 * Getter for message data.
	 * @return message data.
	 */
	string getData();

	/**
	 * Receives new message from specified file descriptor and maximal size.
	 * @param fd receive message from this file descriptor.
	 */
	void receiveMessage(int fd);

	/**
	 * Sends message to specified file descriptor.
	 * @param fd send message to this file descriptor.
	 */
	void sendMessage(int fd);

};

#endif //QUARTETTESERVER_MESSAGE_H
