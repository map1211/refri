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
		log.info("SendFileInfoRead(String envPath) :: " + envPath);
		HashMap<String, Object> map;
		
		try {
			map = util.getSocketServerInfo();
			this.serverSendPath 	= map.get("serverSendPath").toString();
			this.serverSendConfigPath 	= envPath + map.get("serverSendConfigPath").toString();
			this.serverSendConfigName 	= map.get("serverSendConfigName").toString();
			this.serverSendConfigDateType 	= map.get("serverSendConfigDateType").toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * application.properties �뿉 �젙�쓽�맂 filepath, filename 
	 * �빐�떦 �뙆�씪�쓽 �궡�슜�쓣 �씫�뼱 arraylist 濡� 諛섑솚. 
	 * @return
	 */
	public ArrayList<String> readConfig() {
		File file;
		ArrayList<String> arr = new ArrayList();
		try {
			log.info("#######  readConfig() serverSendConfigPath  :: " + serverSendConfigPath );
			// �뙆�씪媛앹껜 �깮�꽦
			file = new File(serverSendConfigPath + File.separator + serverSendConfigName);
			
			// �엯�젰 �뒪�듃由� �깮�꽦 
			FileReader fileReader = new FileReader(file);
			
			// �엯�젰 踰꾪띁 �깮�꽦 
			BufferedReader bReader = new BufferedReader(fileReader);
			
			String line = "";
			while ((line = bReader.readLine()) != null) {
				log.info(line);
				arr.add(line);
				
			}
			
			// .readLine() �� �걹�뿉 媛쒗뻾臾몄옄瑜� �씫吏� �븡�뒗�떎. 
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
	 * application.properties �뿉 �젙�쓽�맂 filepath, filename
	 * �씤�옄濡� 諛쏆� yymdd �뿉 �빐�떦�븯�뒗 臾몄옄�뿴�씠 議댁옱�븯�뒗 寃껊쭔 arraylist �뿉 �떞�뒗�떎.  
	 * �빐�떦 �뙆�씪�쓽 �궡�슜�쓣 �씫�뼱 arraylist 濡� 諛섑솚. 
	 * @return
	 */
	public ArrayList<String> readConfig(String yymmdd) {
		File file;
		ArrayList<String> arr = new ArrayList();
		try {
			log.info("#######  readConfig(String yymmdd) serverSendConfigPath  :: " + serverSendConfigPath );
			// �뙆�씪媛앹껜 �깮�꽦
			file = new File(serverSendConfigPath + File.separator + serverSendConfigName);
			
			// �엯�젰 �뒪�듃由� �깮�꽦 
			FileReader fileReader = new FileReader(file);
			
			// �엯�젰 踰꾪띁 �깮�꽦 
			BufferedReader bReader = new BufferedReader(fileReader);
			
			String line = "";
			while ((line = bReader.readLine()) != null) {
				log.info("line : " + line + "_"+yymmdd);
				//if(line.contains(yymmdd)) {
					//log.info("line :============ " );
					arr.add(line + "_"+yymmdd);
				//}
				
			}
			
			// .readLine() �� �걹�뿉 媛쒗뻾臾몄옄瑜� �씫吏� �븡�뒗�떎. 
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
