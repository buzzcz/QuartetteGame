#include "server.h"

void Server::add_game(int number_of_players, Player p) {
	Game new_game(number_of_games++, number_of_players, p);
	games.push_back(new_game);
}

int Server::create(string address, uint16_t port) {
	unsigned long i;
	int return_value;

	for (i = 0; i < address.length(); i++) {
		address.at(i) = (char) toupper(address.at(i));
	}

	server_socket = socket(AF_INET, SOCK_STREAM, 0);
	memset(&server_addr, 0, sizeof(struct sockaddr_in));
	server_addr.sin_family = AF_INET;
	if (address.compare("INADDR_ANY") == 0) {
		server_addr.sin_addr.s_addr = htons(INADDR_ANY);
	} else if (address.compare("LOCALHOST") == 0) {
		server_addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
	} else {
		return_value = inet_pton(server_addr.sin_family, address.c_str(), &server_addr.sin_addr);
		if (i != 1) {
			printf("IP address is not valid\n");
			return (return_value == 0 ? -1 : return_value);
		}
	}
	if (port > 1 && port < 65535) {
		server_addr.sin_port = htons(port);
	} else {
		printf("Port must be bigger then 1 and smaller then 65636\n");
		return (port == 0 ? -1 : port);
	}

	return_value = bind(server_socket, (struct sockaddr *) &server_addr, sizeof(struct sockaddr_in));
	if (return_value == 0) {
		printf("Bind OK.\n");
	} else {
		printf("Bind error.\n");
		return return_value;
	}

	return_value = listen(server_socket, 5);
	if (return_value == 0) {
		printf("Listen OK.\n");
	} else {
		printf("Listen error.\n");
		return return_value;
	}

	FD_ZERO(&client_socks);
	FD_SET(server_socket, &client_socks);
	return 0;
}

int Server::start(string address, uint16_t port) {
	int return_value = create(address, port);

	if (return_value != 0) {
		return return_value;
	}

	number_of_games = 0;

	while (run) {
		tests = client_socks;
		return_value = select(FD_SETSIZE, &tests, (fd_set *) 0, (fd_set *) 0, (struct timeval *) 0);
		if (return_value < 0) {
			printf("Select error.\n");
			return return_value;
		}
		// exclude stdin, stdout, stderr
		for (fd = 3; fd < FD_SETSIZE; fd++) {
			if (FD_ISSET(fd, &tests)) {
				if (fd == server_socket) {
					client_socket = accept(server_socket, (struct sockaddr *) &client_addr, &addr_len);
					FD_SET(client_socket, &client_socks);
					printf("New connection accepted.\n");
				} else {
					to_read = 0;
					ioctl(fd, FIONREAD, &to_read);
					if (to_read > 0) {
						Message m;
						m.receive_message(fd, to_read);
					} else {
						printf("Client %d disconnected.\n", fd);
						close(fd);
						FD_CLR(fd, &client_socks);
					}
				}
			}
		}

	}

	return 0;
}
