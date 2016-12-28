package cz.zcu.kiv.ups.network;

import lombok.Data;

@Data
public class Message {

	private int type;

	private String data;

	private int size;

	public Message(int type, String data) {
		this.type = type;
		this.data = data;
		size = data.length();
	}

	public String getMessageToSend() {
		return type + ";" + size + ";" + data;
	}
}
