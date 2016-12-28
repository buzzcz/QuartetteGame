package cz.zcu.kiv.ups.network;

public interface NetworkInterface {

	String open(String ihost, int iport);

	void close();

	void sendMessage(Message msg);

	Message receiveMessage();
}
