package cz.zcu.kiv.ups;

public class TcpApplication {

	public static void main(String[] args) throws InterruptedException {
		ParseCmdLine params = new ParseCmdLine(args);

		TcpClient client = new TcpClient(params.hostname, params.port);
		while (true) {
			TcpMessage msgSend = new TcpMessage("Hello server");
			client.putMessage(msgSend);
			TcpMessage msgRecv = client.getMessage();
			if (msgRecv == null) {
				break;
			}
			System.out.println(msgRecv.getMessage());
			Thread.sleep(300);
		}
		client.close();
	}
}
