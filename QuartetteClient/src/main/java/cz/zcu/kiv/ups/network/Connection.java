package cz.zcu.kiv.ups.network;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

@Service
@Slf4j
public class Connection implements NetworkInterface {

	private Socket s;
	private BufferedReader reader;
	private PrintWriter writer;

	@Override
	public boolean open(String host, int port) {
		try {
			s = new Socket(host, port);
		} catch (IOException e) {
			log.error("Connection to " + host + ":" + port + " refused.", e);
			return false;
		} catch (IllegalArgumentException e) {
			log.error("Illegal port - not in allowed range 0 - 65535.", e);
			return false;
		} catch (NullPointerException e) {
			log.error("Hostname not supplied.", e);
			return false;
		}

		try {
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			log.info("Connected to " + s.getInetAddress() + ":" + s.getPort());
		} catch (Exception e) {
			log.error("Could not connect to " + s.getInetAddress() + ":" + s.getPort(), e);
			return false;
		}
		return true;
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
	public void putMessage(Message msg) {
		try {
			writer.println(msg.getMessageToSend());
			writer.flush();
		} catch (Exception e) {
			log.error("Write error.", e);
		}
	}

	@Override
	public Message getMessage() {
		String line;
		try {
			line = reader.readLine();
//			TODO: Parse message correctly
			return new Message(0, line);
		} catch (IOException e) {
			log.error("Read error.", e);
		}
		return null;
	}
}
