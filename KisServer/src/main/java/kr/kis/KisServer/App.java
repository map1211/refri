package kr.kis.KisServer;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("args null");
			return;
		}

		ConfigurableApplicationContext configurableApplicationContext = null;
		try {
			final String appLocation = System.getenv(args[0]) + File.separator;
			System.out.println("App Location : " + appLocation);

			System.setProperty("LOGPATH", appLocation + "logs");

			String configLocation = appLocation + File.separator + "resources" + File.separator;
			configurableApplicationContext = new SpringApplicationBuilder(App.class)//
					.properties(//
							"spring.config.location=file:" + configLocation + "application.properties", //
							"logging.config=file:" + configLocation + "log4j.xml", //
							"config.location=file:" + configLocation)//
					.run(args);
		} catch (Exception e) {
			logger.error("ERROR", e);
		} finally {
			configurableApplicationContext.close();
		}
	}
}