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
	public void open(String host, int port) {
		// create a socket to communicate to the specified host and port
		try {
			s = new Socket(host, port);
		} catch (IOException e) {
			log.error("Connection to " + host + ":" + port + " refused.", e);
		} catch (IllegalArgumentException e) {
			log.error("Illegal port - not in allowed range 0 - 65535.", e);
		} catch (NullPointerException e) {
			log.error("Hostname not supplied.", e);
		}

		try {
			// create streams for reading and writing
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			// tell the user that we've connected
			log.info("Connected to " + s.getInetAddress() + ":" + s.getPort());
		} catch (Exception e) {
			log.error("Could not connect to " + s.getInetAddress() + ":" + s.getPort(), e);
		}
	}

	@Override
	public void close() {
		try {
			reader.close();
			writer.close();
		} catch (Exception e) {
			log.error("Could not close streams.", e);
		}
		//always be sure to close the socket
		finally {
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
			writer.println(msg.getData());
			writer.flush();
		} catch (Exception e) {
			log.error("Write error.", e);
		}
	}

	@Override
	public Message getMessage() {
		String line;
		// read the response (a line) from the server
		try {
			line = reader.readLine();
			// write the line to console
			return new Message(line);
		} catch (IOException e) {
			log.error("Read error.", e);
		}
		return null;
	}
}
