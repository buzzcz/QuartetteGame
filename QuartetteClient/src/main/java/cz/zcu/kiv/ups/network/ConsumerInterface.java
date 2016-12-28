package cz.zcu.kiv.ups.network;

/**
 * Implement this interface to enable message receiving and consuming.
 *
 * @author Jaroslav Klaus
 */
@FunctionalInterface
public interface ConsumerInterface {

	/**
	 * Receives and consumes message.
	 */
	void consumeMessage();

}
