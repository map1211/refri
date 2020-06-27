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

//import kr.kis.utils.KisFTUtils;
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
		// ������ ���� ������ ���� �ݱ� 
		if(socket.isConnected()) socket.close();
		// ���� ����
		socket.connect( socketAddress, time*1000);
		
	}
	
	
	public boolean connect1(String ip, int port, int time) throws  UnknownHostException, IOException{
		socketAddress = new InetSocketAddress(ip, port);
		socket = new Socket();
		socket.setSoTimeout(time*1000);	
		socket = new Socket( ip, port );
		// ������ ���� ������ ���� �ݱ� 
		if(socket.isConnected()) socket.close();
		// ���� ����		
		socket.connect( socketAddress, time*1000);
		
		return socket.isConnected();
	}
	
	/**
	 * FRO1 ���� �۽�
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public String sendNrcvString( String msg ) throws Exception{
		
		String retMsg = null;
		
		try{
//			log.info("outputstream ���� " );
			OutputStream output = socket.getOutputStream();
//			log.info("(1) ���� �ڷḦ byte[]�� ��ȯ " );
			byte[] sendWhat = msg.getBytes(socketEncode);
//			log.info("(2) ���� �ڷ��� ���̸� ���� : length : "  + sendWhat.length);
			int writeLen= sendWhat.length;
//			log.info("(4) ���� �ڷḦ ���� : " + new String(sendWhat));
			output.write(sendWhat);
			log.info("send statement to server " );
//			log.info("��������  Byte[]��Ʈ���� ���۹��� " );
			InputStream input = socket.getInputStream();
//			log.info(" (1) ���۵� �ڷ��� ���̸� ���� " );
			int size = 1024; //dis.readInt();
//			log.info(" (2) ���۵�  �ڷ��� ���̿� �´� byte[] ����" );
			byte[] receiveWhat = new byte[size];
//			log.info(" (3) ���۵� �ڷḦ byte[]������ ������ " );
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line;
			
			while((line = br.readLine()) != null) {
				log.info(" readline :  " + line );
			}
			// (4) ���۵� �ڷḦ String������ ��ȯ 
//			log.info(" (4) ���۵� �ڷḦ String������ ��ȯ  " );
			log.info(" send message  :   " + line );
			return retMsg;
			
		}catch(Exception e){
			log.info("Exception : Client sendString  ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * FRO1 ���� �۽�
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public void sendFR01String( String msg ) throws Exception{
		
		try{
//			log.info("outputstream ���� " );
			OutputStream output = socket.getOutputStream();
//			log.info("(1) ���� �ڷḦ byte[]�� ��ȯ " );
			byte[] sendWhat = msg.getBytes(socketEncode);
//			log.info("(2) ���� �ڷ��� ���̸� ���� : length : "  + sendWhat.length);
			int writeLen= sendWhat.length;
//			log.info("(4) ���� �ڷḦ ���� : " + new String(sendWhat));
			output.write(sendWhat);
			log.info("Send statement complete " );

		}catch(Exception e){
			log.info("Exception : Client sendString ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * FR13 ���� ���� ���� ���� ����
	 * 
	 * @return
	 * @throws Exception
	 */
	public String recvString() throws Exception{
		
		try{
			InputStream input = socket.getInputStream();
			String line="";
			byte[] receiveWhat = new byte[100]; // ��������
			int bytesRead = 0;
			
			while ((bytesRead = input.read(receiveWhat)) > 0) {
				line = new String(receiveWhat, 0, bytesRead);
			}
			log.info("receive msg : [" +line + "]");
			
			return line;
			
		}catch(Exception e){
			log.info("Exception : Client recvString ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * FR12 ���� ���� ���� ���� ���������� ����, �۽� ���� ������ �´�. 
	 * ���� FR12 �� ���� ���� Ȯ�� �� ���� �����۾��� �ٷ� �����ؾ� �ȴ�. 
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
		
			//��������  Byte[]��Ʈ���� ���۹���
//			log.info("��������  Byte[]��Ʈ���� ���۹��� " );
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			// (1) ���۵� �ڷ��� ���̸� ����
//			log.info(" (1) ���۵� �ڷ��� ���̸� ���� " );
			int size = 100;
			// (2) ���۵�  �ڷ��� ���̿� �´� byte[] ����
//			log.info(" (2) ���۵�  �ڷ��� ���̿� �´� byte[] ����" );
			byte[] receiveWhat = new byte[size];
			// (3) ���۵� �ڷḦ byte[]������ ������
//			log.info(" (3) ���۵� �ڷḦ byte[]������ ������ " );
			dis.read(receiveWhat, 0, size);
			// (4) ���۵� �ڷḦ String������ ��ȯ 
//			log.info(" (4) ���۵� �ڷḦ String������ ��ȯ  " );
			retMsg = new String(receiveWhat, socketEncode);
			
			log.info("receive msg : [" +retMsg + "]");
			if("FR12".equals(retMsg.substring(0, 4)) && "1".equals(retMsg.substring(14, 15))) {
				String rcvFileSize = retMsg.substring(15, 25);
				log.info("file receiving mode :: filesize long : " + Long.parseLong(rcvFileSize) );
				
				// FR02 : ���� ���� ����. 
				// ���� ������ ���������� �Ϸ� �� ��� 
				// ���� �������� ��û ���� (FR13) : KIS -> ������
				log.info("File Receiving : [" + fileName + "]");
				
	            File file = new File(filePath  + File.separator + fileName) ;
				
				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				log.info("## Client: File create : [" + fileName + "]");
				
				// ����Ʈ ������ ���� �����鼭 ��� 
				int len;
				int bufSize = 1024;
				
				byte[] data = new byte[bufSize];
				long writeSize = 0;
				long longRcvFileSize = Long.parseLong(rcvFileSize); 
				
				if( longRcvFileSize > 1024) {
					while((len = is.read(data)) != -1) {
						bos.write(data, 0, len);
						writeSize += len;
						if((writeSize + 1024) > longRcvFileSize) {
							break;
						}
					}
				} else {
					data = new byte[(int)longRcvFileSize];
					while((len = is.read(data)) != -1) {
						bos.write(data, 0, len);
						writeSize += len;
						if((writeSize + 1024) > longRcvFileSize) {
							break;
						}
					}
				}
				
				// ��ȭ�� ���� ���� ����. 
				// ��Ŷ�� ���� �ȵ�� ���� ��� 
				// ����ؼ� loop ���鼭 Ȯ��. 
				//
				
				long writeSize2 = longRcvFileSize;
				while((len = is.read(data)) != -1) {
					bos.write(data, 0, len);
					writeSize += len; // ��ü ���� ������ �񱳿�.
					writeSize2 -= len;
					if(writeSize2 < 1024) {
						data = new byte[(int)writeSize2];
					}
				}
			
				
				// Lim hh TJ logic apply end 
				log.info("## Client: writen fileSize : " + writeSize);
				
				
				try {bos.close();} catch (IOException e) {e.printStackTrace();}
				try {fos.close();} catch (IOException e) {e.printStackTrace();}
				
				recvFileSize = Long.toString(writeSize);	  
				log.info("## Receive file data size : " + recvFileSize);
				
				if(longRcvFileSize != writeSize)  {
					log.info("Exception : file receive. ");
					retResult = "FAIL|"+"file receive size diff."; 
				} else {
					byte[] receiveFr13 = new byte[size]; // 100 byte read
					// (3) ���۵� �ڷḦ byte[]������ ������
//					log.info("FR13 ���� ����" );
					dis.read(receiveFr13, 0, size);
					// (4) ���۵� �ڷḦ String������ ��ȯ 
//					log.info(" ���� ���� ��ȯ  " );
					String retMsg1 = new String(receiveFr13, socketEncode);
					log.info("Receive FR13 statement : [" + retMsg1 + "]" );
					
					if(KisFtConstant.CODE_FR13.equals(retMsg1.substring(0, 4))) {
						retResult = "SUCCESS|"+recvFileSize; 
						log.info("file receive success. ");
					}
				}
				return retResult;
			} else {
//				log.info("## Agree/Reject  :  "+ retMsg.substring(14, 15));	
				log.info("## reject code  :  "+ retMsg.substring(35, 39));	
				if("FR12".equals(retMsg.substring(0, 4)) && "2".equals(retMsg.substring(14, 15))) {
					if("0001".equals(retMsg.substring(35, 39))) {
						log.error("## Receiving file is not exist" );		
					} else if("0002".equals(retMsg.substring(35, 39))) {
						log.error("## Receive other fulltext(error fulltext) " );		
						
					} else if("0003".equals(retMsg.substring(35, 39))) {
						log.error("## Raise other exception." );		
					}
				}
				return "FAIL";
			}
			
			
			
		}catch(Exception e){
			log.info("Exception : Client recvString ", e);
			return "FAIL";
		}
	}

	/**
	 * FR03 
	 * ���� �������� ���� ���� (FR03) : ������ -> KIS
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public String sendOnlyString( String msg ) throws Exception{
		
		String retMsg = "";
		
		try{
			log.info("FR03 :: Byte [] stream transmission to server.  msg : [" + msg + "]" );
			//������ Byte[]��Ʈ�� ����
//			log.info("������ Byte[]��Ʈ�� ���� " );
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			//(1) ���� �ڷḦ byte[]�� ��ȯ
			byte[] sendWhat = msg.getBytes(socketEncode);
			//(2) ���� �ڷ��� ���̸� ���� 
			int writeLen= sendWhat.length;
			dos.write(sendWhat, 0, writeLen);
			dos.flush();

			
			return retMsg;

		}catch(Exception e){
			log.info("Exceptin : Client sendString  ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	public void sendFR03String( String msg ) throws Exception{
		
		try{
			log.info("FR03 :: Byte [] stream transmission to server. msg : [" + msg + "]" );
			OutputStream output = socket.getOutputStream();
			//(1) ���� �ڷḦ byte[]�� ��ȯ
			byte[] sendWhat = msg.getBytes(socketEncode);
			//(2) ���� �ڷ��� ���̸� ���� 
			int writeLen= sendWhat.length;
			//(4) ���� �ڷḦ ����
			output.write(sendWhat);			
			
			
		}catch(Exception e){
			log.info("Exception : Client sendString ", e);
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
		
		// �ð� ��������
		long lStartTime = System.currentTimeMillis();
		
		// ������ ������ ���� �����ϱ�
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
			
			// relay ������ ����
			log.info("############### relay server ip: " + relaysocketIp);
			log.info("############### relay server port: " + relaysocketPort);
			log.info("############### relay server connect " );
			relayClient.connect(relaysocketIp, relaysocketPort, relayServerSocketTimeout);
			
			log.info("############### relay server file send" );
			DataOutputStream dos = new DataOutputStream(relayClient.getSocket().getOutputStream());
			
			// relay ������ ���� ����. 
			String sendRslt = fileSend(dos, sendFilePath, sendFileName.trim());
			
			if(KisFtConstant.RCV_SUCC.equals(sendRslt)) {
				log.info("file send success. ");
			}
			// realay ���� ���� ����
			relayClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}	

	
	/**
	 * DataOutputStream�� ���� ������ ������ ����
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
			// ���ϸ� ����.
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
			
			logutil.printMessage("## Client: File send.  ");
			logutil.printMessage("## Client: send file size : [" + totalReadBytes + "]");
			
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
			// ���ϸ��� ���� �ް� ���ϸ� ���� 
			logutil.info("## Client: File Name : [" + fileName + "]");
			
			// file ���� �� ���Ͽ� ���� ��� ��Ʈ�� ���� 
			File file = new File(filePath  + File.separator + fileName) ;
			
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			logutil.info("## Client: File create : [" + fileName + "]");
			
			// ����Ʈ ������ ���� �����鼭 ��� 
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
			
			logutil.info("## Client: File receive ");
			logutil.info("## Client: receive file size : [" + recvFileSize + "]");
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
	 * FTO1 ���� �۽�
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public void sendFT01String( String msg ) throws Exception{
		
		log.info("msg:[" + msg + "]");
		
		try{
			//������ Byte[]��Ʈ�� ����
			OutputStream output = socket.getOutputStream();
			//(1) ���� �ڷḦ byte[]�� ��ȯ
			byte[] sendWhat = msg.getBytes(socketEncode);
			//(2) ���� �ڷ��� ���̸� ���� 
			int writeLen= sendWhat.length;
			//(4) ���� �ڷḦ ����
			output.write(sendWhat);

		}catch(Exception e){
			log.info("Excepton : Client sendFT01String ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	
	/**
	 * FTO3 ���� �۽�
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public void sendFT03String( String msg ) throws Exception{
		
//		log.info("msg:[" + msg + "]");
		String retMsg = null;
		
		try{
			//������ Byte[]��Ʈ�� ����
//			log.info("outputstream ���� " );
			OutputStream output = socket.getOutputStream();
			//(1) ���� �ڷḦ byte[]�� ��ȯ
			byte[] sendWhat = msg.getBytes(socketEncode);
			//(2) ���� �ڷ��� ���̸� ���� 
			int writeLen= sendWhat.length;
			//(4) ���� �ڷḦ ����
			output.write(sendWhat);

		}catch(Exception e){
			log.info("Exception : Client sendFT01String  ", e);
			e.printStackTrace();
			throw e;
		}
	}
	
	
	/**
	 * FT12 ���� ���� ���� 
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String recvFT12String() throws Exception{
		
		String retMsg = null;
		
		try{
		
			//��������  Byte[]��Ʈ���� ���۹���
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			// (1) ���۵� �ڷ��� ���̸� ����
			int size = 100;
			// (2) ���۵�  �ڷ��� ���̿� �´� byte[] ����
			byte[] receiveWhat = new byte[size];
			// (3) ���۵� �ڷḦ byte[]������ ������
			dis.read(receiveWhat, 0, size);
			// (4) ���۵� �ڷḦ String������ ��ȯ 
			retMsg = new String(receiveWhat, socketEncode);
			
			log.info("receive msg : [" +retMsg + "]");
			
		}catch(Exception e){
			log.info("Exception : Client recvFT12String ", e);
			return "FAIL";
		}
		
		return retMsg;
	}
	
	/**
	 * FT13 ���� ���� ���� 
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String recvFT13String() throws Exception{
		
		String retMsg = null;
		
		try{
			
			//��������  Byte[]��Ʈ���� ���۹���
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			// (1) ���۵� �ڷ��� ���̸� ����
			int size = 100;
			// (2) ���۵�  �ڷ��� ���̿� �´� byte[] ����
			byte[] receiveWhat = new byte[size];
			dis.read(receiveWhat, 0, size);
			// (4) ���۵� �ڷḦ String������ ��ȯ 
			retMsg = new String(receiveWhat, socketEncode);
			
			log.info("receive msg : [" +retMsg + "]");
			
		}catch(Exception e){
			log.info("Exception : Client recvFT13String  ", e);
			return "FAIL";
		}
		
		return retMsg;
	}
	
	
	public String recvFT13String(String envPath) throws Exception{
		
		String retMsg = null;
		
		try{
			
			//��������  Byte[]��Ʈ���� ���۹���
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			// (1) ���۵� �ڷ��� ���̸� ����
			int size = 100;
			// (2) ���۵�  �ڷ��� ���̿� �´� byte[] ����
			byte[] receiveWhat = new byte[size];
			dis.read(receiveWhat, 0, size);
			// (4) ���۵� �ڷḦ String������ ��ȯ 
			retMsg = new String(receiveWhat, socketEncode);
			
			log.info("receive msg : [" +retMsg + "]");
			
		}catch(Exception e){
			log.info("Exception : Client recvFT13String  ", e);
			return "FAIL";
		}
		
		return retMsg;
	}
	
	
}
