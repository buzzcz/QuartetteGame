package cz.zcu.kiv.ups.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility to parse and check input values.
 *
 * @author Jaroslav Klaus
 */
@Slf4j
public class ParseCmdLine {

	/**
	 * Hostname.
	 */
	@Getter
	private String hostname = "localhost";

	/**
	 * Port.
	 */
	@Getter
	private int port = 10000;

	/**
	 * Nickname.
	 */
	@Getter
	private String nickname;

	public ParseCmdLine(String[] args) {
		int i = 0;
		String arg;

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];
			switch (arg) {
				case "-host":
					if (i < args.length) {
						hostname = args[i++];
					} else {
						log.error("-host requires hostname.");
					}
					break;
				case "-port":
					if (i < args.length) {
						try {
							port = Integer.parseInt(args[i++]);
						} catch (NumberFormatException e) {
							log.error("-port not a number, setting to 10000.", e);
							port = 10000;
						} finally {
							if (port <= 0 || port >= 65535) {
								log.error("-port not in range 1-65534.");
							}
						}
					} else {
						log.error("-port requires port (1-65534).");
					}
					break;
				case "-nick":
					if (i < args.length) {
						nickname = args[i++];
					} else {
						log.error("-nick requires nickname.");
					}
					break;
				default:
					log.error(String.format("QuartetteClient: illegal option %s.", arg));
					break;
			}
		}
	}

}
