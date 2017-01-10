#include "Statistics.h"

std::atomic<unsigned long> Statistics::receivedBytes(0);
std::atomic<unsigned long> Statistics::receivedMessages(0);
std::atomic<unsigned long> Statistics::sentBytes(0);
std::atomic<unsigned long> Statistics::sentMessages(0);
std::atomic<unsigned long> Statistics::acceptedConnections(0);
std::atomic<unsigned long> Statistics::closedClients(0);
time_t Statistics::start;
time_t Statistics::end;
