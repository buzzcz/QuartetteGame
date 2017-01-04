package cz.zcu.kiv.ups.network;

import cz.zcu.kiv.ups.dto.Message;
import cz.zcu.kiv.ups.dto.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Represents connection to server and implements its opening, closing, sending and receiving messages.
 *
 * @author Jaroslav Klaus
 */
@Service
@Slf4j
public class Connection implements NetworkInterface {

	/**
	 * Socket used for communication.
	 */
	private Socket s;

	/**
	 * Buffered Reader for receiving messages.
	 */
	private BufferedReader reader;

	/**
	 * Print Writer for sending messages.
	 */
	private PrintWriter writer;

	@Override
	public String open(String host, int port) {
		try {
			s = new Socket(host, port);
		} catch (IOException e) {
			String error = String.format("Connection to %s:%d refused.", host, port);
			log.error(error, e);
			return error;
		} catch (IllegalArgumentException e) {
			String error = "Illegal port - not in allowed range 0 - 65535.";
			log.error(error, e);
			return error;
		} catch (NullPointerException e) {
			String error = "Hostname not supplied.";
			log.error(error, e);
			return error;
		}

		try {
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			log.info(String.format("Connected to %s:%d.", s.getInetAddress(), s.getPort()));
		} catch (Exception e) {
			String error = String.format("Could not connect to %s:%d.", s.getInetAddress(), s.getPort());
			log.error(error, e);
			return error;
		}
		return null;
	}

	@Override
	public void close() {
		try {
			reader.close();
			writer.close();
		} catch (Exception e) {
			log.error("Could not close streams.", e);
		} finally {
			try {
				if (s != null) {
					s.close();
				}
			} catch (IOException e) {
				log.error("Could not close socket.", e);
			}
		}
	}

	@Override
	public void sendMessage(Message msg) {
		try {
			writer.println(msg.getMessageToSend());
			writer.flush();
			log.info(String.format("Sending %s.", msg.getMessageToSend()));
		} catch (Exception e) {
			log.error("Write error.", e);
		}
	}

	@Override
	public Message receiveMessage() {
		String line;
		try {
			if (reader.ready()) {
				line = reader.readLine();
				log.info(String.format("Received: %s.", line));
				String[] parts = line.split(";");
				int size = Integer.parseInt(parts[1]);
				if ((size == 0 && parts.length == 2) || size == parts[2].length()) {
					return new Message(MessageType.values()[Integer.parseInt(parts[0])], size == 0 ? "" : parts[2]);
				} else {
					log.error("Received data size mismatch.");
				}
			}
		} catch (IOException e) {
			log.error("Read error.", e);
		}
		return null;
	}
}
