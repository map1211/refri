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
			final String configLocation = System.getenv(args[0]);
			System.out.println("Properties Location : " + configLocation);
			
			System.setProperty("LOGPATH", configLocation);
			
			configurableApplicationContext = new SpringApplicationBuilder(App.class)//
					.properties(//
							"spring.config.location=file:" + configLocation + File.separator + "application.properties", //
							"config.location=file:" + configLocation, //
							"logging.config=file:" + configLocation + File.separator + "log4j.xml")//
					.run(args);
		} catch (Exception e) {
			logger.error("ERROR", e);
		} finally {
			configurableApplicationContext.close();
		}
	}
}