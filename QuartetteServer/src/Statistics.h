#ifndef QUARTETTESERVER_STATISTICS_H
#define QUARTETTESERVER_STATISTICS_H

#include <atomic>
#include <time.h>

class Statistics {

public:
	static std::atomic<unsigned long> receivedBytes;
	static std::atomic<unsigned long> receivedMessages;
	static std::atomic<unsigned long> sentBytes;
	static std::atomic<unsigned long> sentMessages;
	static std::atomic<unsigned long> acceptedConnections;
	static std::atomic<unsigned long> closedClients;
	static time_t start;
	static time_t end;

};

#endif //QUARTETTESERVER_STATISTICS_H
