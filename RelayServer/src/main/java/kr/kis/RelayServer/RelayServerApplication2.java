package kr.kis.RelayServer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RelayServerApplication2 {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("args null");
			return;
		}

		ConfigurableApplicationContext configurableApplicationContext = null;

		try {
			// Properties
			String properties = "spring.config.location=file:" + System.getenv(args[0]);
			configurableApplicationContext = new SpringApplicationBuilder(RelayServerApplication2.class).properties(properties).run(args);

			NettyServer nettyServer = configurableApplicationContext.getBean(NettyServer.class, configurableApplicationContext.getBean(NettyClient.class));
			nettyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configurableApplicationContext.close();
		}
	}
}