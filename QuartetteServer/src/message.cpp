#include "message.h"

Message::Message() {}

Message::Message(int type, string data) : type(type), data(data) {
	size = data.length();
}

void Message::receive_message(int fd, size_t to_read) {
	char buffer;
	string s_type = "", s_size = "";
	int count = 0;
	int i;

	for (i = 0; i < to_read; i++) {
		if (count == 2) {
			break;
		}
		recv(fd, &buffer, 1, 0);
		if (buffer == ';') {
			count++;
			continue;
		}
		if (count == 0) {
			s_type += buffer;
		} else if (count == 1) {
			s_size += buffer;
		}
	}
//	TODO: not 2 means error
	if (count == 2) {
		type = strtol(s_type.c_str(), NULL, 10);
		size = strtol(s_size.c_str(), NULL, 10);
//		TODO: 0 means no number was found

		char *tmp = (char *) malloc(size * sizeof(char));
		recv(fd, tmp, size, 0);
		data.assign(tmp, size);
		free(tmp);
		printf("Client %d sent %d;%lu;%s\n", fd, type, size, data.c_str());
	}
}
