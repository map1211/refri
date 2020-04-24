package kr.kis.RelayServer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RelayServerApplication {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("args null");
			return;
		}

		ConfigurableApplicationContext configurableApplicationContext = null;
		try {
			String getenv = System.getenv(args[0]);
			System.out.println(getenv);
			configurableApplicationContext = new SpringApplicationBuilder(RelayServerApplication2.class).properties("spring.config.location=file:" + getenv)
					.properties("config.location=file:" + getenv).run(args);

			RelayServer relayServer = configurableApplicationContext.getBean(RelayServer.class);
			relayServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configurableApplicationContext.close();
		}
	}
}