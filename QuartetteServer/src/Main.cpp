#include <iostream>
#include "Server.h"

int main(int argc, char *argv[]) {
	//TODO: Check for parameters.
	Server s;
	int i = s.start(argv[1], (uint16_t) std::stoi(argv[2], NULL, 10));

	return i;
}
