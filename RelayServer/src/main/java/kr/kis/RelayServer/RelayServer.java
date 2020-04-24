package kr.kis.RelayServer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.util.TestingUtilities;
import org.springframework.stereotype.Component;

@Component
public class RelayServer {
	/**
	 * The Tcp port.
	 */
	@Value("${socket.server.port}")
	private int tcpPort;

	/**
	 * The Boss count.
	 */
	@Value("${thread.maxNum}")
	private int bossCount;

	/**
	 * The Worker count.
	 */
	@Value("${thread.maxNum}")
	private int workerCount;

	@Value("${config.location}")
	private String configLocation;

	public void start() {
		System.out.println("runRelayServer : " + configLocation);
		final GenericXmlApplicationContext context = setupReplayServerContext();
		final AbstractServerConnectionFactory crLfServer = context.getBean(AbstractServerConnectionFactory.class);

		System.out.print("Waiting for server to accept connections...");
		TestingUtilities.waitListening(crLfServer, 10000L);
		System.out.println("running.\n\n");

		System.out.println("Please enter some text and press <enter>: ");
		System.out.println("\tNote:");
		System.out.println("\t- Entering FAIL will create an exception");
		System.out.println("\t- Entering q will quit the application");
		System.out.print("\n");
		System.out.println("\t--> Please also check out the other samples, " + "that are provided as JUnit tests.");
		System.out.println("\t--> You can also connect to the server on port '" + crLfServer.getPort() + "' using Telnet.\n\n");
	}

	private GenericXmlApplicationContext setupReplayServerContext() {
		final GenericXmlApplicationContext context = new GenericXmlApplicationContext();

		context.load(configLocation + "/applicationContext.xml");
		context.registerShutdownHook();
		context.refresh();

		return context;
	}
}
