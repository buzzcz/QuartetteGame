package cz.zcu.kiv.ups.network;

import cz.zcu.kiv.ups.dto.Message;
import cz.zcu.kiv.ups.dto.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for sending keep-alive.
 *
 * @author Jaroslav Klaus
 */
@Service
public class KeepAliveSender implements KeepAliveSenderInterface {

	@Autowired
	private NetworkInterface connection;

	@Override
	public void sendKeepAlive() {
		Message m = new Message(MessageType.KEEP_ALIVE, "");
		connection.sendMessage(m);
	}

}
