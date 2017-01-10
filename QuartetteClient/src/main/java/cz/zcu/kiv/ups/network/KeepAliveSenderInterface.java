package cz.zcu.kiv.ups.network;

/**
 * Implement this to send keep alive message and ensure connection to server.
 *
 * @author Jaroslav Klaus
 */
@FunctionalInterface
public interface KeepAliveSenderInterface {

	/**
	 * Sends keep-alive message.
	 */
	void sendKeepAlive();

}
