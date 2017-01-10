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
		} else if (c == '\r') {
			continue;
		}
		line += c;
	}
	Statistics::receivedBytes.fetch_add(line.size());
	unsigned long i = line.find(";");
	if (i == string::npos) {
		type = UNPARSEABLE;
		printf("Could not parse received message.\n");
		return;
	}
	string sType = line.substr(0, i);
	line.erase(0, i + 1);
	i = line.find(";");
	if (i == string::npos) {
		type = UNPARSEABLE;
		printf("Could not parse received message.\n");
		return;
	}
	string sSize = line.substr(0, i);
	line.erase(0, i + 1);
	try {
		type = static_cast<MessageType>(std::stoi(sType, NULL, 10));
		size = std::stoul(sSize, NULL, 10);
	} catch (std::invalid_argument e) {
		type = UNPARSEABLE;
		printf("Type or size is not a number.\n");
		return;
	}
	data = line;
	if (size != data.size()) {
		type = UNPARSEABLE;
		printf("Received data size mismatch.\n");
		return;
	}
	Statistics::receivedMessages.fetch_add(1);
	printf("Client %d sent %d;%lu;%s.\n", fd, type, size, data.c_str());
}

string Message::getMessageToSend() {
	return std::to_string(type) + ";" + std::to_string(size) + ";" + data + "\n";
}

void Message::sendMessage(int fd) {
	string data = getMessageToSend();
	send(fd, data.c_str(), data.length(), 0);
	Statistics::sentBytes.fetch_add(data.size());
	Statistics::sentMessages.fetch_add(1);
	printf("Sending %d;%lu;%s to client %d.\n", type, size, this->data.c_str(), fd);
}
