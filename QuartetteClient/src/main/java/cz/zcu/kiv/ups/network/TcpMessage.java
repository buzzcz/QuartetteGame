package cz.zcu.kiv.ups.network;

public class TcpMessage {

	private String message;
	private int bytes;

	public TcpMessage(String msg) {
		message = msg;
		bytes = msg.length();
	}

	public String getMessage() {
		return message;
	}
}
