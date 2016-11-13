package cz.zcu.kiv.ups;

public class ParseCmdLine {

	private String hostname = "localhost";
	private int port = 10000;
	private String nickname = "";

	public ParseCmdLine(String[] args) {
		int i = 0;
		String arg;

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];
			if (arg.equals("-host")) {
				if (i < args.length) {
					hostname = args[i++];
				} else {
					System.err.println("-host requires hostname");
				}
			} else if (arg.equals("-port")) {
				if (i < args.length) {
					try {
						port = Integer.parseInt(args[i++]);
					} catch (NumberFormatException e) {
						System.err.println("-port not a number, setting to 10000");
						port = 10000;
					} finally {
						if (port <= 0 || port >= 65535) {
							System.err.println("-port not in range 1-65534");
						}
					}
				} else {
					System.err.println("-port requires port (1-65534)");
				}
			} else if (arg.equals("-nick")) {
				if (i < args.length) {
					nickname = args[i++];
				} else {
					System.err.println("-nick requires nickname");
				}
			} else {
				System.err.println("QuartetteClient: illegal option " + arg);

			}
		}
	}

	public void usage() {
		System.err.println("Usage: QuartetteClient [-host hostname] [-port port] [-nick nickname]");
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getNickname() {
		return nickname;
	}
}
