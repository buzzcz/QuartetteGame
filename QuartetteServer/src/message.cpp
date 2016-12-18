#include "message.h"

Message::Message() {}

Message::Message(int type, string data) : type(type), data(data) {
	size = data.length();
}

int Message::getType() {
	return type;
}

string Message::getData() {
	return data;
}

void Message::receiveMessage(int fd, size_t toRead) {
//	TODO: what if data incomplete or beginning is missing??
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
		type = std::stoi(sType.c_str(), NULL, 10);
		size = std::stoul(sSize.c_str(), NULL, 10);
//		TODO: 0 means no number was found

		char *tmp = (char *) malloc(size * sizeof(char));
		recv(fd, tmp, size, 0);
		data.assign(tmp, size);
		free(tmp);
		recv(fd, &buffer, 1, 0);
		if (buffer == '\n') {
			printf("Client %d sent %d;%lu;%s\n", fd, type, size, data.c_str());
		} else {
			printf("Error in receiveMessage");
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
