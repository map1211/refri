package kr.kis.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.util.ResourceUtils;

//@Slf4j
public class ServerLog {
	private static ServerLog serverLog = null;
	
	private Logger logger;
	
	private String envPath="";
	
	private FileReader resources;
	private Properties properties;
	private Properties sysProp;
	
	private ServerLog(){
		ServerInfoUtil utils = new ServerInfoUtil();
		Properties log4jProperty = this.utilLogInit();
	
		PropertyConfigurator.configure(log4jProperty);
		logger = (Logger)Logger.getInstance("ServerLog");
	}
	
	
	private ServerLog(String envPath){
//		PropertyConfigurator.configure("C:/egov_dev/workspace/JavaSample/bin/log4j.properties");
//		log = (Logger)Logger.getInstance("ServerLog");
		this.envPath = envPath;
//		System.out.println("ServerLog(envPath) :: envPath ::" + envPath);
		Properties log4jProperty = this.utilLogInit();
		PropertyConfigurator.configure(log4jProperty);
		logger = (Logger)Logger.getInstance("ServerLog");
		
	}
	
	public Properties utilLogInit() {
		
		properties = new Properties();
		
//		if("".equals(envPath)) {
			envPath = ".";
//		}
		System.setProperty("LOGPATH", envPath);
		
//		System.out.println("####### utilLogInit() :: SYSTEM.properties:: LOGPATH::" + System.getProperty("LOGPATH"));
		try {
//			resources = new FileReader( envPath + "/resources/log4j.properties" );
			resources = new FileReader( ResourceUtils.getFile("classpath:log4j.properties") );
			properties.load(resources);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		
		return properties;
	}	
		
	
	public static ServerLog getInstance(){
		if(serverLog == null){
			serverLog = initLog();
		}
		return serverLog; 
	}
	
	private static ServerLog initLog(){
		serverLog = new ServerLog();
		return serverLog;
	}
	
	public static ServerLog getInstance(String envPath){
//		System.out.println("###### ServerLog::getInstance::envPath::" + envPath);
		if(serverLog == null){
			serverLog = initLog(envPath);
		}
		return serverLog; 
	}
	
	private static ServerLog initLog(String envPath){
//		System.out.println("###### ServerLog::initLog::envPath::" + envPath);
		serverLog = new ServerLog(envPath);
		return serverLog;
	}
	
	public void error(String subTitle, String message){
		logger.error("[" + subTitle + "] : " + message );
	}
	
	public void error(String subTitle, String message, Throwable e){
		logger.error("[" + subTitle + "] : " + message, e );
	}	

	public void debug(String subTitle, String message){
		logger.debug("[" + subTitle + "] : " + message );
	}
	
	public void debug(String subTitle, String message, Throwable e){
		logger.debug("[" + subTitle + "] : " + message, e );
	}
	
	public void info(String subTitle, String message){
		logger.info("[" + subTitle + "] : " + message );
	}
	
	public void info(String subTitle, String message, Throwable e){
		logger.info("[" + subTitle + "] : " + message, e );
	}
}
