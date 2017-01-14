#include "Server.h"

void help(char *argv[]) {
	printf("You need to specify 2 parameters. Usage:\n");
	printf("%s address port\n", argv[0]);
	printf("%s localhost 10000\n", argv[0]);
	printf("%s inaddr_any 10000\n", argv[0]);
}

int main(int argc, char *argv[]) {
	//TODO: Check for parameters.
	if (argc != 3) {
		help(argv);
		return -1;
	}
	uint16_t port;
	try {
		port = (uint16_t) std::stoi(argv[2], NULL, 10);
	} catch (std::invalid_argument e) {
	}
	Server s;
	int i = s.start(argv[1], port);

	return i;
}
