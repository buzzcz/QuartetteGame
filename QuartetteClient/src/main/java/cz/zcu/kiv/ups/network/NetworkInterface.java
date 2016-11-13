package cz.zcu.kiv.ups.network;

public interface NetworkInterface {

	void open(String ihost, int iport);

	void close();

	void putMessage(TcpMessage msg);

	TcpMessage getMessage();
}
