package kr.kis.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class SendFileInfoRead {

	private FileReader resources;
	private Properties properties;

	private static LogUtil log;
	private static ServerInfoUtil util;
	private static String serverSendPath = "";
	private static String serverSendConfigPath = "";
	private static String serverSendConfigName = "";
	private static String serverSendConfigDateType = "";
	
	public SendFileInfoRead() {
		log = new LogUtil(this.getClass().getName());
		this.util = new ServerInfoUtil();
		
		HashMap<String, Object> map;
		
		try {
			map = util.getSocketServerInfo();
			this.serverSendPath 	= map.get("serverSendPath").toString();
			this.serverSendConfigPath 	= map.get("serverSendConfigPath").toString();
			this.serverSendConfigName 	= map.get("serverSendConfigName").toString();
			this.serverSendConfigDateType 	= map.get("serverSendConfigDateType").toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public SendFileInfoRead(String envPath) {
		log = new LogUtil(this.getClass().getName(), envPath);
		this.util = new ServerInfoUtil(envPath);
		
		HashMap<String, Object> map;
		
		try {
			map = util.getSocketServerInfo();
			this.serverSendPath 	= map.get("serverSendPath").toString();
			this.serverSendConfigPath 	= map.get("serverSendConfigPath").toString();
			this.serverSendConfigName 	= map.get("serverSendConfigName").toString();
			this.serverSendConfigDateType 	= map.get("serverSendConfigDateType").toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * application.properties 에 정의된 filepath, filename 
	 * 해당 파일의 내용을 읽어 arraylist 로 반환. 
	 * @return
	 */
	public ArrayList<String> readConfig() {
		File file;
		ArrayList<String> arr = new ArrayList();
		try {
			// 파일객체 생성
			file = new File(serverSendConfigPath + File.separator + serverSendConfigName);
			
			// 입력 스트림 생성 
			FileReader fileReader = new FileReader(file);
			
			// 입력 버퍼 생성 
			BufferedReader bReader = new BufferedReader(fileReader);
			
			String line = "";
			while ((line = bReader.readLine()) != null) {
				log.info(line);
				arr.add(line);
				
			}
			
			// .readLine() 은 끝에 개행문자를 읽지 않는다. 
			bReader.close();
			
			for(int i=0; i < arr.size(); i++) {
				log.info(arr.get(i));
			}
		} catch (FileNotFoundException e) {
			log.error("Exception : FileNotFoundException : " + e.getMessage(), e);
		} catch (IOException e) {
			log.error("Exception : IOException : " + e.getMessage(), e);
			
		}
		
		
		return arr;
	}
	
	/**
	 * application.properties 에 정의된 filepath, filename
	 * 인자로 받은 yymdd 에 해당하는 문자열이 존재하는 것만 arraylist 에 담는다.  
	 * 해당 파일의 내용을 읽어 arraylist 로 반환. 
	 * @return
	 */
	public ArrayList<String> readConfig(String yymmdd) {
		File file;
		ArrayList<String> arr = new ArrayList();
		try {
			// 파일객체 생성
			file = new File(serverSendConfigPath + File.separator + serverSendConfigName);
			
			// 입력 스트림 생성 
			FileReader fileReader = new FileReader(file);
			
			// 입력 버퍼 생성 
			BufferedReader bReader = new BufferedReader(fileReader);
			
			String line = "";
			while ((line = bReader.readLine()) != null) {
				log.info(line);
				if(line.contains(yymmdd)) {
					arr.add(line);
				}
				
			}
			
			// .readLine() 은 끝에 개행문자를 읽지 않는다. 
			bReader.close();
			
			for(int i=0; i < arr.size(); i++) {
				log.info(arr.get(i));
			}
		} catch (FileNotFoundException e) {
			log.error("Exception : FileNotFoundException : " + e.getMessage(), e);
		} catch (IOException e) {
			log.error("Exception : IOException : " + e.getMessage(), e);
			
		}
		
		
		return arr;
	}
	
	
}
