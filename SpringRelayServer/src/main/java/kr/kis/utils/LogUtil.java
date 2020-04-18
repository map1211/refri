package kr.kis.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LogUtil {

	private String clsName="";
	private String envPath="";
	
	private FileReader resources;
	private Properties properties;
	private Properties sysProp;
	
	public LogUtil() {
		
	}
	public LogUtil(String clsName) {
		this.clsName = clsName;
	}
	
	public LogUtil(String clsName, String envPath) {
		this.clsName = clsName;
		this.envPath = envPath;
		
	}
	
	
	
	//로그 출력 함수(msg)
	public void printMessage(String msg){
		if(!"".equals(envPath)) {
			ServerLog.getInstance(envPath).info(clsName, msg);
		} else {
			ServerLog.getInstance().info(clsName, msg);
		}
	}
	//로그 출력 함수(msg, throwable)
	public void printMessage(String msg, Throwable e){
		if(!"".equals(envPath)) {
			
			ServerLog.getInstance(envPath).info(clsName, msg, e);
		} else {
			
			ServerLog.getInstance().info(clsName, msg, e);
		}
	}	
	
	//로그 출력 함수(msg)
	public void info(String msg){
		if(!"".equals(envPath)) {
//			System.out.println("LogUtil.info():: envPath::" + envPath);
			ServerLog.getInstance(envPath).info(clsName, msg);
			
		} else {
			
			ServerLog.getInstance().info(clsName, msg);
		}
	}
	//로그 출력 함수(msg, throwable)
	public void info(String msg, Throwable e){
		if(!"".equals(envPath)) {
			
			ServerLog.getInstance(envPath).info(clsName, msg, e);
		} else {
			
			ServerLog.getInstance().info(clsName, msg, e);
		}
	}	
	
	//로그 출력 함수(msg)
	public void error(String msg){
		if(!"".equals(envPath)) {
			
			ServerLog.getInstance(envPath).info(clsName, msg);
		} else {
			
			ServerLog.getInstance().info(clsName, msg);
		}
	}
	//로그 출력 함수(msg, throwable)
	public void error(String msg, Throwable e){
		if(!"".equals(envPath)) {
			ServerLog.getInstance(envPath).info(clsName, msg, e);
			
		} else {
			ServerLog.getInstance().info(clsName, msg, e);
			
		}
	}	
	//로그 출력 함수(msg)
	public void debug(String msg){
		if(!"".equals(envPath)) {
			ServerLog.getInstance(envPath).info(clsName, msg);
			
		} else {
			
			ServerLog.getInstance().info(clsName, msg);
		}
	}
	//로그 출력 함수(msg, throwable)
	public void debug(String msg, Throwable e){
		if(!"".equals(envPath)) {
			ServerLog.getInstance(envPath).info(clsName, msg, e);
			
		} else {
			
			ServerLog.getInstance().info(clsName, msg, e);
		}
	}	
	
	
}
