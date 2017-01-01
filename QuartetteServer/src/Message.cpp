#include "Message.h"

Message::Message() {}

Message::Message(MessageType type, string data) : type(type), data(data) {
	size = data.length();
}

MessageType Message::getType() {
	return type;
}

string Message::getData() {
	return data;
}

void Message::receiveMessage(int fd) {
	string line = "";

	while (true) {
		char c;
		recv(fd, &c, 1, 0);
		if (c == '\n') {
			break;
		}
		line += c;
	}
	unsigned long i = line.find(";");
//	TODO: if == string::npos error
	string sType = line.substr(0, i);
	line.erase(0, i + 1);
	i = line.find(";");
//	TODO: if == string::npos error
	string sSize = line.substr(0, i);
	line.erase(0, i + 1);
	try {
		type = static_cast<MessageType>(std::stoi(sType, NULL, 10));
		size = std::stoul(sSize, NULL, 10);
	} catch (std::invalid_argument e) {
//		TODO: error
	}
	data = line;
	printf("Client %d sent %d;%lu;%s.\n", fd, type, size, data);
}

string Message::getMessageToSend() {
	return std::to_string(type) + ";" + std::to_string(size) + ";" + data + "\n";
}

void Message::sendMessage(int fd) {
	string data = getMessageToSend();
	send(fd, data.c_str(), data.length(), 0);
	printf("Sending \"%s\" to client %d.\n", data, fd);
}
