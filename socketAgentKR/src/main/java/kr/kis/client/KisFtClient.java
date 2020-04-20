package kr.kis.client;
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import kr.kis.utils.KisFTUtils;
import kr.kis.utils.KisFtConstant;
import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;
import lombok.Data;

@Data
public class KisFtClient {
	private Socket socket;
	
	private String socketIp;
	private int socketPort ;
	private String socketEncode;
	
	private LogUtil log;
	private SocketAddress socketAddress ;

	
	private static InputStream dis = null;
	private static OutputStream dos = null;   
	
	static String recvFileSize;
	static String envPath;
	
	public KisFtClient(){

		ServerInfoUtil util = new ServerInfoUtil();
		this.log = new LogUtil(this.getClass().getName());
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		String serverType 	= map.get("serverType").toString();
		String recvFilePath =  map.get("serverRecvPath").toString();
		String serverRecvCode =  map.get("serverRecvCode").toString();
		String serverOrgCode =  map.get("serverOrgCode").toString();
				
		this.socketIp 	= map.get("serverIp").toString();
		this.socketEncode = map.get("serverEncodeType").toString();
		this.socketPort 	= Integer.parseInt(map.get("serverPort").toString());
		
	}
	
	public KisFtClient(String envPath){
		this.envPath = envPath;
		ServerInfoUtil util = new ServerInfoUtil(envPath);
		this.log = new LogUtil(this.getClass().getName(), envPath);
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		String serverType 	= map.get("serverType").toString();
		String recvFilePath =  map.get("serverRecvPath").toString();
		String serverRecvCode =  map.get("serverRecvCode").toString();
		String serverOrgCode =  map.get("serverOrgCode").toString();
		
		this.socketIp 	= map.get("serverIp").toString();
		this.socketEncode = map.get("serverEncodeType").toString();
		this.socketPort 	= Integer.parseInt(map.get("serverPort").toString());
		
	}
	
	public KisFtClient(String socketIp, int socketPort, String encodeType){
		
		this.log = new LogUtil(this.getClass().getName());
		
		this.socketIp 	= socketIp;
		this.socketPort 	= socketPort;
		this.socketEncode = encodeType;
		
	}
	
	public KisFtClient(String socketIp, int socketPort, String encodeType, String envPath){
		
		this.log = new LogUtil(this.getClass().getName(), envPath);
		
		this.socketIp 	= socketIp;
		this.socketPort 	= socketPort;
		this.socketEncode = encodeType;
		
	}
	
	public void connect(String ip, int port, int time) throws  UnknownHostException, IOException{
		socketAddress = new InetSocketAddress(ip, port);
		socket = new Socket();
		socket.setSoTimeout(time*1000);	
		// 소켓이 열려 있으면 먼저 닫기 
		if(socket.isConnected()) socket.close();
		// 소켓 연결
		socket.connect( socketAddress, time*1000);
		
	}
	
	
	public boolean connect1(String ip, int port, int time) throws  UnknownHostException, IOException{
		socketAddress = new InetSocketAddress(ip, port);
		socket = new Socket();
		socket.setSoTimeout(time*1000);	
		socket = new Socket( ip, port );
		// 소켓이 열려 있으면 먼저 닫기 
		if(socket.isConnected()) socket.close();
		// 소켓 연결		
		socket.connect( socketAddress, time*1000);
		
		return socket.isConnected();
	}
	
	/**
	 * FRO1 전문 송신
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public String sendNrcvString( String msg ) throws Exception{
		
		String retMsg = null;
		
		try{
			log.info("outputstream 생성 " );
			OutputStream output = socket.getOutputStream();
//			log.info("(1) 전송 자료를 byte[]로 변환 " );
			byte[] sendWhat = msg.getBytes(socketEncode);
//			log.info("(2) 전송 자료의 길이를 구함 : length : "  + sendWhat.length);
			int writeLen= sendWhat.length;
//			log.info("(4) 전송 자료를 보냄 : " + new String(sendWhat));
			output.write(sendWhat);
			log.info("서버로 전문 송신 성공 " );
//			log.info("서버에서  Byte[]스트림을 전송받음 " );
			InputStream input = socket.getInputStream();
//			log.info(" (1) 전송될 자료의 길이를 받음 " );
			int size = 1024; //dis.readInt();
//			log.info(" (2) 전송될  자료의 길이에 맞는 byte[] 생성" );
			byte[] receiveWhat = new byte[size];
//			log.info(" (3) 전송된 자료를 byte[]변수에 저장함 " );
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line;
			
			while((line = br.readLine()) != null) {
				log.info(" readline :  " + line );
			}
			// (4) 전송된 자료를 String변수로 변환 
//			log.info(" (4) 전송된 자료를 String변수로 변환  " );
			log.info(" 전송 메시지 :   " + line );
			return retMsg;
			
		}catch(Exception e){
			log.info("Client sendString 오류 ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * FRO1 전문 송신
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public void sendFR01String( String msg ) throws Exception{
		
		try{
			log.info("outputstream 생성 " );
			OutputStream output = socket.getOutputStream();
//			log.info("(1) 전송 자료를 byte[]로 변환 " );
			byte[] sendWhat = msg.getBytes(socketEncode);
//			log.info("(2) 전송 자료의 길이를 구함 : length : "  + sendWhat.length);
			int writeLen= sendWhat.length;
//			log.info("(4) 전송 자료를 보냄 : " + new String(sendWhat));
			output.write(sendWhat);
			log.info("전문송신 완료 " );

		}catch(Exception e){
			log.info("Client sendString 오류 ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * FR13 파일 수신 종료 전문 수신
	 * 
	 * @return
	 * @throws Exception
	 */
	public String recvString() throws Exception{
		
		String retMsg = null;
		
		KisFTUtils ftUtils = new KisFTUtils();
		
		try{
			InputStream input = socket.getInputStream();
			String line="";
			byte[] receiveWhat = new byte[100]; // 수신전문
			int bytesRead = 0;
			
			while ((bytesRead = input.read(receiveWhat)) > 0) {
				line = new String(receiveWhat, 0, bytesRead);
			}
			log.info("receive msg : [" +line + "]");
			
			return line;
			
		}catch(Exception e){
			log.info("Client recvString 오류 ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * FR12 응답 전문 수신 직후 파일전문이 오고, 송신 종료 전문이 온다. 
	 * 따라서 FR12 에 대한 내용 확인 후 파일 수신작업을 바로 수행해야 된다. 
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String recvFR12_02_13String(String filePath, String fileName) throws Exception{
		
		String retMsg = null;
		String retResult = null;
		
		
		try{
		
			//서버에서  Byte[]스트림을 전송받음
//			log.info("서버에서  Byte[]스트림을 전송받음 " );
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			// (1) 전송될 자료의 길이를 받음
//			log.info(" (1) 전송될 자료의 길이를 받음 " );
			int size = 100;
			// (2) 전송될  자료의 길이에 맞는 byte[] 생성
//			log.info(" (2) 전송될  자료의 길이에 맞는 byte[] 생성" );
			byte[] receiveWhat = new byte[size];
			// (3) 전송된 자료를 byte[]변수에 저장함
//			log.info(" (3) 전송된 자료를 byte[]변수에 저장함 " );
			dis.read(receiveWhat, 0, size);
			// (4) 전송된 자료를 String변수로 변환 
//			log.info(" (4) 전송된 자료를 String변수로 변환  " );
			retMsg = new String(receiveWhat, socketEncode);
			
			log.info("receive msg : [" +retMsg + "]");
			if("FR12".equals(retMsg.substring(0, 4)) && "1".equals(retMsg.substring(14, 15))) {
				String rcvFileSize = retMsg.substring(15, 25);
				log.info("파일 수신 모드 filesize long : " + Long.parseLong(rcvFileSize) );
				
				// FR02 : 파일 수신 실행. 
				// 파일 수신이 정상적으로 완료 된 경우 
				// 파일 수신종료 요청 전문 (FR13) : KIS -> 가맹점
				log.info("File Receiving : [" + fileName + "]");
				
				byte[] buffer = new byte[1024];
	            int bytesRead=0;
	            File file = new File(filePath  + File.separator + fileName) ;
				
				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				log.info("## Client: File 생성완료 : [" + fileName + "]");
				
				// 바이트 데이터 전송 받으면서 기록 
				int len;
				int bufSize = 1024;
				
				byte[] data = new byte[bufSize];
				long writeSize = 0;
				while((len = is.read(data)) != -1) {
					bos.write(data, 0, len);
					writeSize += len;
					if((writeSize + 1024) > Long.parseLong(rcvFileSize)) {
						break;
					}
				}
				
				byte[] residualData = new byte[(int) (Long.parseLong(rcvFileSize)-writeSize)];
				len = is.read(residualData);
				bos.write(residualData, 0, len);
				writeSize += len;
				
				log.info("## 최종 Client: writen fileSize : " + writeSize);
				
				try {bos.close();} catch (IOException e) {e.printStackTrace();}
				try {fos.close();} catch (IOException e) {e.printStackTrace();}
				
				recvFileSize = Long.toString(writeSize);	  
				log.info("## 수신파일 데이터 size : " + recvFileSize);
				
				if(Long.parseLong(rcvFileSize) != writeSize)  {
					log.info("파일 수신 오류. ");
				} else {
					byte[] receiveFr13 = new byte[size]; // 100 byte read
					// (3) 전송된 자료를 byte[]변수에 저장함
//					log.info("FR13 전문 수신" );
					dis.read(receiveFr13, 0, size);
					// (4) 전송된 자료를 String변수로 변환 
//					log.info(" 수신 전문 변환  " );
					String retMsg1 = new String(receiveFr13, socketEncode);
					log.info("FR13 전문 수신 : [" + retMsg1 + "]" );
					
					if(KisFtConstant.CODE_FR13.equals(retMsg1.substring(0, 4))) {
						retResult = "SUCCESS|"+recvFileSize; 
						log.info("파일 수신 성공. ");
					}
				}
				return retResult;
			} else {
				log.info("## 승인/거절여부 :  "+ retMsg.substring(14, 15));	
				log.info("## 거절 코드  :  "+ retMsg.substring(35, 39));	
				if("FR12".equals(retMsg.substring(0, 4)) && "2".equals(retMsg.substring(14, 15))) {
					if("0001".equals(retMsg.substring(35, 39))) {
						log.error("## 수신 요청한 파일이 존재하지 않음  " );		
					} else if("0002".equals(retMsg.substring(35, 39))) {
						log.error("## 기타 전문 수신함(오류 전문) " );		
						
					} else if("0003".equals(retMsg.substring(35, 39))) {
						log.error("## 기타 에러 발생" );		
					}
				}
				return "FAIL";
			}
			
			
			
		}catch(Exception e){
			log.info("Client recvString 오류 ", e);
			return "FAIL";
		}
	}

	/**
	 * FR03 
	 * 파일 수신종료 응답 전문 (FR03) : 가맹점 -> KIS
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public String sendOnlyString( String msg ) throws Exception{
		
		String retMsg = "";
		
		try{
			log.info("FR03 :: 서버에 Byte[]스트림 전송 msg : [" + msg + "]" );
			//서버에 Byte[]스트림 전송
//			log.info("서버에 Byte[]스트림 전송 " );
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			//(1) 전송 자료를 byte[]로 변환
			byte[] sendWhat = msg.getBytes(socketEncode);
			//(2) 전송 자료의 길이를 구함 
			int writeLen= sendWhat.length;
			dos.write(sendWhat, 0, writeLen);
			dos.flush();

			
			return retMsg;

		}catch(Exception e){
			log.info("Client sendString 오류 ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	public void sendFR03String( String msg ) throws Exception{
		
		String retMsg = "";
		
		try{
			log.info("FR03 :: 서버에 Byte[]스트림 전송 msg : [" + msg + "]" );
			OutputStream output = socket.getOutputStream();
			//(1) 전송 자료를 byte[]로 변환
			byte[] sendWhat = msg.getBytes(socketEncode);
			//(2) 전송 자료의 길이를 구함 
			int writeLen= sendWhat.length;
			//(4) 전송 자료를 보냄
			output.write(sendWhat);			
			
			
		}catch(Exception e){
			log.info("Client sendString 오류 ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	public void requestSendFile(String sendFilePath, String sendFileName) throws UnknownHostException, IOException{
		ServerInfoUtil util ;
		LogUtil log  ;

		if(envPath == null) {
			util = new ServerInfoUtil();
			log = new LogUtil(KisFtClient.class.getName());
		} else {
			util = new ServerInfoUtil(envPath);
			log = new LogUtil(KisFtClient.class.getName(), envPath);
			
		}
		HashMap<String, Object> map = util.getSocketServerInfo();
		String serverType 	= map.get("serverType").toString();
		socketIp 	= map.get("serverIp").toString();
		socketPort 	= Integer.parseInt(map.get("serverPort").toString());
		String recvFilePath =  map.get("serverRecvPath").toString();
		String serverRecvCode =  map.get("serverRecvCode").toString();
		String serverOrgCode =  map.get("serverOrgCode").toString();
		socketEncode = map.get("serverEncodeType").toString();
		
		// 시간 측정위해
		long lStartTime = System.currentTimeMillis();
		
		// 릴레이 서버에 파일 전달하기
		HashMap<String, Object> relaymap;
		try {
			relaymap = util.getRelayServerInfo();
			String relaysocketIp 	= relaymap.get("relayServerIp").toString();
			int relaysocketPort 	= Integer.parseInt(relaymap.get("relayServerPort").toString());
			int relayServerSocketTimeout 	= Integer.parseInt(relaymap.get("relayServerSocketTimeout").toString());
			
			KisFtClient relayClient;
			if(envPath == null) {
				relayClient = new KisFtClient();
			} else {
				relayClient = new KisFtClient(envPath);				
			}
			
			// relay 서버에 접속
			log.info("############### relay서버 ip: " + relaysocketIp);
			log.info("############### relay서버 port: " + relaysocketPort);
			log.info("############### relay서버 접속시도 " );
			relayClient.connect(relaysocketIp, relaysocketPort, relayServerSocketTimeout);
			
			log.info("############### relay서버로 파일 송신 " );
			DataOutputStream dos = new DataOutputStream(relayClient.getSocket().getOutputStream());
			
			// relay 서버에 파일 전송. 
			String sendRslt = fileSend(dos, sendFilePath, sendFileName.trim());
			
			if(KisFtConstant.RCV_SUCC.equals(sendRslt)) {
				log.info("파일 송신 성공. ");
			}
			// realay 서버 접속 종료
			relayClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}	

	
	/**
	 * DataOutputStream을 통한 파일을 서버에 전송
	 * 
	 * @param dos
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static String fileSend(DataOutputStream dos, String filePath, String fileName) {
		String rslt = "";
		LogUtil logutil = new LogUtil();
		FileInputStream fis2 = null ;
		BufferedInputStream bis2 = null;
		
		if("".equals(filePath)) {
			filePath = "C:/socket-server2";
		}
		
		
		File file = new File(filePath + File.separator + fileName);
		if (!file.exists()) {
			logutil.printMessage("File not Exist.");
		    System.exit(0);
		}

		long fileSize = file.length();
		long totalReadBytes = 0;
		byte[] buffer = new byte[1024];
		byte[] data = new byte[1024];
		int readBytes;
		double startTime = 0;


		int len;
		try {
			// 파일명 전송.
			dos.writeUTF(fileName);
			
			fis2 = new FileInputStream(file);
			bis2 = new BufferedInputStream(fis2);
			
            startTime = System.currentTimeMillis();
            
            while((len = bis2.read(data)) != -1 ) {
            	dos.write(data, 0, len);
            }
            dos.flush();
            logutil.printMessage("File transfer completed.");
            
			rslt = "SUCCESS";
			
			logutil.printMessage("## Client: File 송신완료  ");
			logutil.printMessage("## Client: 송신 파일 사이즈 : [" + totalReadBytes + "]");
			
		} catch (IOException e) {
			e.printStackTrace();
			rslt = "ERROR";
		} finally {
			try {fis2.close();} catch (IOException e) {e.printStackTrace();}
			try {bis2.close();} catch (IOException e) {e.printStackTrace();}
		}
		
		return rslt;
	}
		
	public void close() throws IOException{
		socket.close();
	}
	
	
	
	public static String fileWrite(DataInputStream dis, String filePath, String fileName, String rcvFileSize) {
		String rslt = "";
		LogUtil logutil = new LogUtil();
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		if("".equals(filePath)) {
			filePath = "C:/socket-server";
		}
		
		try {
			logutil.info("## fileWrite :: Client: file receive starting... ");
			// 파일명을 전송 받고 파일명 수정 
			logutil.info("## Client: File Name : [" + fileName + "]");
			
			// file 생성 후 파일에 대한 출력 스트림 생성 
			File file = new File(filePath  + File.separator + fileName) ;
			
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			logutil.info("## Client: File 생성완료 : [" + fileName + "]");
			
			// 바이트 데이터 전송 받으면서 기록 
			int len;
			int size = 1024;
			
			byte[] data = new byte[size];
			long writeSize = 0;
			while((len = dis.read(data)) != -1) {
				bos.write(data, 0, len);
				
				writeSize += len;
			}
			logutil.info("## Client: writen fileSize : " + writeSize);
			recvFileSize = Long.toString(writeSize);
			rslt = "SUCCESS";
			
			logutil.info("## Client: File 수신완료  ");
			logutil.info("## Client: 받은 파일 사이즈 : [" + recvFileSize + "]");
		} catch (IOException e) {
			e.printStackTrace();
			rslt = "ERROR";
		} finally {
			try {bos.close();} catch (IOException e) {e.printStackTrace();}
			try {fos.close();} catch (IOException e) {e.printStackTrace();}
		}
		
		return rslt;
	}
		
	
	
	/**
	 * FTO1 전문 송신
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public void sendFT01String( String msg ) throws Exception{
		
		log.info("msg:[" + msg + "]");
		String retMsg = null;
		
		try{
			//서버에 Byte[]스트림 전송
			OutputStream output = socket.getOutputStream();
			//(1) 전송 자료를 byte[]로 변환
			byte[] sendWhat = msg.getBytes(socketEncode);
			//(2) 전송 자료의 길이를 구함 
			int writeLen= sendWhat.length;
			//(4) 전송 자료를 보냄
			output.write(sendWhat);

		}catch(Exception e){
			log.info("Client sendFT01String 오류 ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	
	/**
	 * FTO3 전문 송신
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public void sendFT03String( String msg ) throws Exception{
		
		log.info("msg:[" + msg + "]");
		String retMsg = null;
		
		try{
			//서버에 Byte[]스트림 전송
			log.info("outputstream 생성 " );
			OutputStream output = socket.getOutputStream();
			//(1) 전송 자료를 byte[]로 변환
			byte[] sendWhat = msg.getBytes(socketEncode);
			//(2) 전송 자료의 길이를 구함 
			int writeLen= sendWhat.length;
			//(4) 전송 자료를 보냄
			output.write(sendWhat);

		}catch(Exception e){
			log.info("Client sendFT01String 오류 ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	
	/**
	 * FT12 응답 전문 수신 
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String recvFT12String() throws Exception{
		
		String retMsg = null;
		String retResult = null;
		
		
		try{
		
			//서버에서  Byte[]스트림을 전송받음
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			// (1) 전송될 자료의 길이를 받음
			int size = 100;
			// (2) 전송될  자료의 길이에 맞는 byte[] 생성
			byte[] receiveWhat = new byte[size];
			// (3) 전송된 자료를 byte[]변수에 저장함
			dis.read(receiveWhat, 0, size);
			// (4) 전송된 자료를 String변수로 변환 
			retMsg = new String(receiveWhat, socketEncode);
			
			log.info("receive msg : [" +retMsg + "]");
			
		}catch(Exception e){
			log.info("Client recvFT12String 오류 ", e);
			return "FAIL";
		}
		
		return retMsg;
	}
	
	/**
	 * FT13 응답 전문 수신 
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String recvFT13String() throws Exception{
		
		String retMsg = null;
		String retResult = null;
		
		KisFTUtils ftUtils = new KisFTUtils();
		
		try{
			
			//서버에서  Byte[]스트림을 전송받음
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			// (1) 전송될 자료의 길이를 받음
			int size = 100;
			// (2) 전송될  자료의 길이에 맞는 byte[] 생성
			byte[] receiveWhat = new byte[size];
			dis.read(receiveWhat, 0, size);
			// (4) 전송된 자료를 String변수로 변환 
			retMsg = new String(receiveWhat, socketEncode);
			
			log.info("receive msg : [" +retMsg + "]");
			
		}catch(Exception e){
			log.info("Client recvFT13String 오류 ", e);
			return "FAIL";
		}
		
		return retMsg;
	}
	
	
}
