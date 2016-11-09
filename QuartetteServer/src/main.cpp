#include <iostream>
#include <stdlib.h>
#include "server.h"

int main(int argc, char *argv[]) {
	Server s;
	s.start(argv[1], strtol(argv[2], NULL, 10));

	return 0;
}