package cz.zcu.kiv.ups.network;

/**
 * Implement this to enable checking time of last received keep-alive.
 *
 * @author Jaroslav Klaus
 */
@FunctionalInterface
public interface KeepAliveCheckerInterface {

	/**
	 * Checks if keep-alive was received in time.
	 */
	void checkKeepAlive();

}
