package cz.zcu.kiv.ups.network;

import lombok.Getter;

public class Message {

	@Getter
	private String data;
	private int size;

	public Message(String data) {
		this.data = data;
		size = data.length();
	}
}
