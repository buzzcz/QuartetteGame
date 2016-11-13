package cz.zcu.kiv.ups.network;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

@Service
public class TcpConnection implements NetworkInterface {

	private Socket s;
	private BufferedReader reader;
	private PrintWriter writer;
	private String host;
	private int port;

	@Override
	public void open(String host, int port) {
		this.host = host;
		this.port = port;
		// create a socket to communicate to the specified host and port
		try {
			s = new Socket(this.host, this.port);
		} catch (IOException e) {
			System.err.println("Connection to " + this.host + ":" + this.port + " refused");
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal port - not in allowed range 0 - 65535");
		} catch (NullPointerException e) {
			System.err.println("Hostname not supplied");
		}

		try {
			// create streams for reading and writing
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			// tell the user that we've connected
			System.out.println("Connected to " + s.getInetAddress() + ":" + s.getPort());
		} catch (Exception e) {
			System.err.println("Could not connect to " + s.getInetAddress() + ":" + s.getPort());
		}
	}

	@Override
	public void close() {
		try {
			reader.close();
			writer.close();
		} catch (Exception e) {
			System.err.println("Close error");
		}
		//always be sure to close the socket
		finally {
			try {
				if (s != null) s.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void putMessage(TcpMessage msg) {
		try {
			writer.println(msg.getMessage());
			writer.flush();
		} catch (Exception e) {
			System.err.println("Write error");
		}
	}

	@Override
	public TcpMessage getMessage() {
		String line;
		// read the response (a line) from the server
		try {
			line = reader.readLine();
			// write the line to console
			return new TcpMessage(line);
		} catch (IOException e) {
			System.err.println("Read error");
		}
		return null;
	}
}
