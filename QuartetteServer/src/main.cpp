#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/ioctl.h>
#include <stdlib.h>
#include "game.h"

int main(void) {
	int server_socket;
	int client_socket, fd;
	int return_value;
	socklen_t addr_len;
	size_t to_read;
	struct sockaddr_in server_addr, client_addr;
	fd_set client_socks, tests;
	struct game *game_list;

	server_socket = socket(AF_INET, SOCK_STREAM, 0);
	memset(&server_addr, 0, sizeof(struct sockaddr_in));
	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(10000);
	server_addr.sin_addr.s_addr = INADDR_ANY;

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

	game_list = NULL;

	FD_ZERO(&client_socks);
	FD_SET(server_socket, &client_socks);

	while (1) {
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
					if (game_list == NULL) {
						game_list = (game *) malloc(sizeof(game));
					}
					printf("New connection accepted.\n");
				} else {
					ioctl(fd, FIONREAD, &to_read);
					if (to_read > 0) {
						char *buffer = (char *) malloc(to_read * sizeof(char));
						recv(fd, buffer, to_read, 0);
						printf("Client %d sent something\n", fd);
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