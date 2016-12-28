package cz.zcu.kiv.ups.network;

import lombok.Data;

/**
 * Represents message to be sent or received.
 *
 * @author Jaroslav Klaus
 */
@Data
public class Message {

	/**
	 * Type of message.
	 */
	private int type;

	/**
	 * Message data.
	 */
	private String data;

	/**
	 * Data size.
	 */
	private int size;

	/**
	 * Constructor to create message.
	 * @param type type of message.
	 * @param data message data.
	 */
	public Message(int type, String data) {
		this.type = type;
		this.data = data;
		size = data.length();
	}

	/**
	 * Creates String to be sent.
	 * @return String to be sent.
	 */
	public String getMessageToSend() {
		return type + ";" + size + ";" + data;
	}
}
