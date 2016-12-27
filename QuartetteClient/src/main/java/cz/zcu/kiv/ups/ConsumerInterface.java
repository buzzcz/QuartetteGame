package cz.zcu.kiv.ups;

import cz.zcu.kiv.ups.network.Message;

/**
 * @author Jaroslav Klaus
 */
@FunctionalInterface
public interface ConsumerInterface {

	void consumeMessage();

}
