package cz.zcu.kiv.ups.network;

/**
 * Implement this interface to enable connecting to server and communication.
 *
 * @author Jaroslav Klaus
 */
public interface NetworkInterface {

	/**
	 * Opens connection to specified server.
	 * @param host hostname.
	 * @param port port.
	 * @return error if any occurred, null otherwise.
	 */
	String open(String host, int port);

	/**
	 * Closes connection.
	 */
	void close();

	/**
	 * Sends message.
	 * @param msg message to send.
	 */
	void sendMessage(Message msg);

	/**
	 * Checks if any message is available and receives it.
	 * @return received message or null if no message is available.
	 */
	Message receiveMessage();

}
