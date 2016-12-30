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

void Message::receiveMessage(int fd, size_t toRead) {
	char buffer;
	string sType = "", sSize = "";
	int count = 0;

	for (int i = 0; i < toRead; i++) {
		if (count == 2) {
			break;
		}
		recv(fd, &buffer, 1, 0);
		if (buffer == ';') {
			count++;
			continue;
		}
		if (count == 0) {
			sType += buffer;
		} else if (count == 1) {
			sSize += buffer;
		}
	}
//	TODO: not 2 means error
	if (count == 2) {
		try {
			type = static_cast<MessageType>(std::stoi(sType.c_str(), NULL, 10));
			size = std::stoul(sSize.c_str(), NULL, 10);
		} catch (std::invalid_argument e) {

		}

		char *tmp = (char *) malloc(size * sizeof(char));
		ssize_t read;
		read = recv(fd, tmp, size, 0);
		if (read != size) {
//			TODO: error
		}
		data.assign(tmp, size);
		free(tmp);
		read = recv(fd, &buffer, 1, 0);
		if (read == 1 && buffer == '\n') {
			printf("Client %d sent %d;%lu;%s\n", fd, type, size, data.c_str());
		} else {
			printf("Error in receiveMessage");
//			TODO: error
		}
	}
}

string Message::getMessageToSend() {
	return std::to_string(type) + ";" + std::to_string(size) + ";" + data + "\n";
}

void Message::sendMessage(int fd) {
	string data = getMessageToSend();
	send(fd, data.c_str(), data.length(), 0);
}
