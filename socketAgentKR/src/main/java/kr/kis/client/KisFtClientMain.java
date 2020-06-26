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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import kr.kis.utils.CustStringUtils;
import kr.kis.utils.KisFTUtils;
import kr.kis.utils.KisFtConstant;
import kr.kis.utils.LogUtil;
import kr.kis.utils.SendFileInfoRead;
import kr.kis.utils.ServerInfoUtil;

//@Slf4j
public class KisFtClientMain {
	
	
	private static DataInputStream dis = null;
	private static DataOutputStream dos = null;
	private static FileOutputStream fos = null;
	private static BufferedOutputStream bos = null;
	
	static String recvFileSize;
	static String socketIp 	;
	static int    socketPort;
	static String socketEncode ; 
	static String relayServerIp;
	static int    relayServerPort;
	
	static int    socketTimeout;
	static int    socketPacketCount;
	static ArrayList<String>    recvFileList = new ArrayList();
	
	
	static String sendPath;
	static String sendTypeCode;
	static String sendTestYn;
	static String sendConfigDateType;
	static String execCmd;
	
	
	static int DEFAULT_BUFFER_SIZE = 1024;
	
	static boolean bConnectRslt = false;
	
	static String relayServerUseYn = "N";

	protected static LogUtil logutil ; 
	
	static String envPath;
	
	public KisFtClientMain() {
		
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		HashMap<String, Object> map;
		
		String serverType 	= "";
		String recvFilePath =  "";
		String serverRecvCode =  ""; 
		String serverOrgCode =  "";
	
		// �ð� ��������
		long lStartTime = System.currentTimeMillis();

		KisFtClientMain main = new KisFtClientMain();
		CmdLineParser parser = new CmdLineParser();

		// Define Option Help
		CmdLineParser.Option prop = main.addHelp(parser.addStringOption('p', "env property"), "This option Environment variable read.");
		CmdLineParser.Option upload = main.addHelp(parser.addStringOption('u', "send start"), " KIS ������ ���� �����ϴ� ��� ����.");
		CmdLineParser.Option download = main.addHelp(parser.addStringOption('d', "receive start"), "KIS �������� ���� �����ϴ� �������.");

		
		String uploadOptValue = "";
		String downloadOptValue = "";
		String propOptValue = "";
		String strType = "";
		String currentDate = "";
		String localFileName = ""; 
		String rcvFileName = "";
		envPath = ""; 
		
		
		
		try{
			
			parser.parse(args);
			
			//ȯ�溯�� Ȯ��.
			try {
				propOptValue = parser.getOptionValue(prop).toString();
				if(propOptValue != null ) {
					
					if(!"".equals(propOptValue) ) {
							envPath = System.getenv(propOptValue);
					} else {
						envPath = ".";
					}
						
					System.setProperty("LOGPATH", envPath);
				}
				
			} catch (Exception ex3) {
			}			
			
			
			// send
			try {
				uploadOptValue = parser.getOptionValue(upload).toString();
				// ���ε� Ȯ��
				if(uploadOptValue != null ) {
					strType = "upload";
					if(!"".equals(uploadOptValue) ) {
						localFileName = uploadOptValue;
					} 			
				}

			} catch (Exception ex1) {
			}
			
			// receive Ȯ��
			String result;
			try {
				downloadOptValue = parser.getOptionValue(download).toString();
				if(downloadOptValue != null ) {
					strType = "download";
					if(!"".equals(downloadOptValue) ) {
						localFileName = downloadOptValue;
					} 
				}

			} catch (Exception ex2) {
			}			
			

			logutil = new LogUtil(KisFtClientMain.class.getName(), envPath);
			
			ServerInfoUtil util = new ServerInfoUtil(envPath);
			KisFTUtils ftUtils = new KisFTUtils(envPath);
			
			try {
				map = util.getSocketServerInfo();
				serverType 	= map.get("serverType").toString();
				socketIp 	= map.get("serverIp").toString();
				socketPort 	= Integer.parseInt(map.get("serverPort").toString());
				recvFilePath =  map.get("serverRecvPath").toString();
				serverRecvCode =  map.get("serverRecvCode").toString();
				serverOrgCode =  map.get("serverOrgCode").toString();
				socketEncode = map.get("serverEncodeType").toString();
				socketTimeout = Integer.parseInt(map.get("serverSocketTimeout").toString());
				socketPacketCount = Integer.parseInt(map.get("serverSocketPacketCount").toString());
				
				recvFileList = (ArrayList<String>)map.get("serverRecvFiles");
				
				relayServerUseYn = map.get("serverRelayUseYn").toString();
				
				sendPath 		=  map.get("serverSendPath").toString();
				sendTypeCode 	=  map.get("serverSendCode").toString();
				sendTestYn 		=  map.get("serverSendTestYn").toString();
				
				sendConfigDateType 	=  map.get("serverSendConfigDateType").toString();
				if(map.get("execCommand") != null) {
					execCmd 		= map.get("execCommand").toString();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
						
			
			if("".equals(strType)) {
				logutil.info(" option is required ::::::: -u \"\" -p \"KIS_FTAgent\"" );
				logutil.info(" option is required ::::::: -d  \"\" -p \"KIS_FTAgent\"" );
			}
			
			
			logutil.info("######### Socket Agent Start ");
			/**
			 * �۽�
			 */
			if("upload".equals(strType)) {
				
				// delay Ÿ���� ȯ�溯������ �о� ���  
				//int sleepTm = 1000 / socketPacketCount;
				
				if("".equals(localFileName)) {
					/**
					 * ������ ���� ���ϸ���Ʈ�� cfg���Ͽ��� �б� 
					 * 
					 * 
					 */
					SendFileInfoRead sfir; //
					if(envPath == null ) {
						sfir = new SendFileInfoRead();
					} else {
						sfir = new SendFileInfoRead(envPath);
						
					}
					ArrayList<String> arrFileList = new ArrayList();
					try {
						System.out.println("CustStringUtils.getToday(sendConfigDateType) :: " + CustStringUtils.getToday(sendConfigDateType));
						arrFileList = sfir.readConfig(CustStringUtils.getToday(sendConfigDateType));
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					
					for (int i=0; i < arrFileList.size(); i++) {
						String sFileName = arrFileList.get(i);
						
						// ���ε�(�۽�) ��� ȣ��
						// ������ ������ EDI �� �����ؼ� �׽�Ʈ  
						uploadExec(sFileName, KisFtConstant.SEND_FILE_TYPE_EDI); 
					}
				} else {
					// ���ε�(�۽�) ��� ȣ��
					// ������ ������ EDI �� �����ؼ� �׽�Ʈ
					logutil.info("localFileName : " + localFileName + " �۽Ÿ�� ");
					uploadExec(localFileName, KisFtConstant.SEND_FILE_TYPE_EDI); 
					
				}

				// ���� ����� �ý��� ��ɾ� ����. 
				// �۽Žÿ��� �ʿ� ���� �ּ� ó�� ��. 
				//logutil.info("###### execCmd::" + execCmd);
				//execCommand(execCmd);

			}
			
			/**
			 * ���� 
			 */
			if("download".equals(strType)) {
				
				
				// �ٿ�ε�(����) ��� ȣ��
				if(!"".equals(serverRecvCode)) {
					if("A".equals(serverRecvCode) && "".equals(localFileName)) {
						// ��ü ���� ��������
						if(recvFileList.size() > 0) {
							for(int i=0; i < recvFileList.size(); i++) {
								downloadExec(localFileName, KisFtConstant.RCV_FILE_TYPE_EDI, recvFileList.get(i));
							}
						}
						
					} else {
						downloadExec(localFileName, "");
					}
				}
				
				// ���� ����� �ý��� ��ɾ� ����. 
				logutil.info("###### execCmd::" + execCmd);
				if(execCmd != null && !"".equals(execCmd)) {
					execCommand(execCmd);
				}
			}
			
		} catch(Exception e){
			logutil.info("Client ���� �߻�!", e);
			e.printStackTrace();
		} finally {
		}
	}
	

	
	/**
	 * ���� �Ծ࿡ ���� ���� ���� 
	 * 
	 * @param localFileName
	 * @param recvType
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private static void downloadExec(String localFileName, String recvType) throws UnknownHostException, IOException {

		KisFTUtils ftUtils;
		ServerInfoUtil util;

		if(envPath == null) {
			ftUtils = new KisFTUtils();
			util = new ServerInfoUtil();
		} else {
			ftUtils = new KisFTUtils(envPath);
			util = new ServerInfoUtil(envPath);
		}
		
		HashMap<String, Object> map;
		
		String serverType 	= "";
		String recvFilePath =  "";
		String serverRecvCode =  ""; 
		String serverOrgCode =  "";
		try {
			map = util.getSocketServerInfo();
			serverType 	= map.get("serverType").toString();
			socketIp 	= map.get("serverIp").toString();
			socketPort 	= Integer.parseInt(map.get("serverPort").toString());
			recvFilePath =  map.get("serverRecvPath").toString();
			serverRecvCode =  recvType; // map.get("serverRecvCode").toString();
			serverOrgCode =  map.get("serverOrgCode").toString();
			socketEncode = map.get("serverEncodeType").toString();
			socketTimeout = Integer.parseInt(map.get("serverSocketTimeout").toString());
//			socketPacketCount = Integer.parseInt(map.get("serverSocketPacketCount").toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		// �ð� ��������
		long lStartTime = System.currentTimeMillis();
		
		// �ٿ�ε� ��� ȣ��
		KisFtClient sc1 = new KisFtClient(envPath);
		
		//[1] ==== ������ ���� ==================
		logutil.info("���Ӽ��� ip   :" + socketIp);
		logutil.info("���Ӽ��� port :" + socketPort);
		logutil.info("Encoding :" + socketEncode);
		
		logutil.info("socket timeout :" + socketTimeout);
//		logutil.info("socket packet count :" + socketTimeout);
		String rcvFileName = ""; 
				
		try {
			logutil.info("connect client");
			sc1.connect(socketIp, socketPort, socketTimeout);
			
			logutil.info("FR01 ���� �߼� ");
			// ���� ���� ���� ����(FR12) : KIS -> ������
			// ������ ������ �������� �۾� �� 
			// ���� ���� ����(FR02) : KIS -> ������ 
			// FR02 ���� ���� .
			String fullText  = "";
			
			fullText = ftUtils.makeFr01("", localFileName).toString();
			
			rcvFileName = localFileName; 
			String result = "";
			try {
				sc1.sendFR01String(fullText);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//1�� waiting
			try{    Thread.sleep(1000);  } catch (Exception e) {	}
			// 1�� waiting�� input stream �б� 
			try {
				logutil.info("FR12 ���� ����,���ϼ���,���ſϷ�  ");
				result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
			} catch (Exception e1) {
				// 1�� waiting �� inputstream �� ������ �ٽ� 1�� waiting�� input stream �б� 
				//1�� waiting
				try{    Thread.sleep(1000);  } catch (Exception e) {	}
				try {
					result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
				} catch (Exception e2) {
					try{    Thread.sleep(1000);  } catch (Exception e) {	}
					// 1�� waiting �� inputstream �� ������ �ٽ� 1�� waiting�� input stream �б� 
					try {
						result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
					} catch (Exception e3) {
						logutil.error("���� ����ð� �ʰ� / ���� ���� ���� ����", e3);
						e3.printStackTrace();
					}
				}

					
			}

			logutil.info("Ŭ���̾�Ʈ ���� ��� [" + result + "]");
			
			if(result != null & result.contains("SUCCESS")) {
				String[] rslt = result.split("\\|");
				String rsltRecvFileSize = rslt[1];
				// FR03
				// ���� �������� ���� ���� (FR03) : ������ -> KIS
				try {
					logutil.info("FR03 ���� ���� ���� ");
					sc1.sendFR03String(ftUtils.makeFr03(rsltRecvFileSize).toString());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
			logutil.info("####### socket close ");
			sc1.close();	


						
		} catch (IOException e2) {
			try {sc1.close();} catch (IOException e) {e.printStackTrace();}
			e2.printStackTrace();
		} finally {
		}
		
		 
		
		
	}
	
	/**
	 * ���� �Ծ࿡ ���� ���� ���� 
	 * 
	 * @param localFileName
	 * @param recvType
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private static void downloadExec(String localFileName, String recvType, String recvFilePrefix)  {
		KisFTUtils ftUtils;
		ServerInfoUtil util;

		if(envPath == null) {
			ftUtils = new KisFTUtils();
			util = new ServerInfoUtil();
		} else {
			ftUtils = new KisFTUtils(envPath);
			util = new ServerInfoUtil(envPath);
		}
		
		HashMap<String, Object> map;
		
		String serverType 	= "";
		String recvFilePath =  "";
		String serverRecvCode =  ""; 
		String serverOrgCode =  "";
		try {
			map = util.getSocketServerInfo();
			serverType 	= map.get("serverType").toString();
			socketIp 	= map.get("serverIp").toString();
			socketPort 	= Integer.parseInt(map.get("serverPort").toString());
			recvFilePath =  map.get("serverRecvPath").toString();
			serverRecvCode =  recvType; // map.get("serverRecvCode").toString();
			serverOrgCode =  map.get("serverOrgCode").toString();
			socketEncode = map.get("serverEncodeType").toString();
			socketTimeout = Integer.parseInt(map.get("serverSocketTimeout").toString());
//			socketPacketCount = Integer.parseInt(map.get("serverSocketPacketCount").toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		// �ð� ��������
		long lStartTime = System.currentTimeMillis();
		
		// �ٿ�ε� ��� ȣ��
		KisFtClient sc1 = new KisFtClient(envPath);
		
		//[1] ==== ������ ���� ==================
		logutil.info("���Ӽ��� ip   :" + socketIp);
		logutil.info("���Ӽ��� port :" + socketPort);
		logutil.info("Encoding :" + socketEncode);
		
		logutil.info("socket timeout :" + socketTimeout);
		String rcvFileName = ""; 
				
		try {
			logutil.info("Connect client");
			sc1.connect(socketIp, socketPort, socketTimeout);
			
			// ���� ���� ���� ����(FR12) : KIS -> ������
			// ������ ������ �������� �۾� �� 
			// ���� ���� ����(FR02) : KIS -> ������ 
			// FR02 ���� ���� .
			String fullText  = "";
			if(!"".equals(localFileName )) { 
				// �Է¹��� ���ϸ��� �����ϸ� �ش� ���ϸ��� �������� ���� �� ������ �ٿ�ε� 
				fullText = ftUtils.makeFr01("", localFileName).toString();
			} else {
				fullText = ftUtils.makeFr01(recvType, localFileName, recvFilePrefix).toString();
			}
			rcvFileName = fullText.substring(15, 45).trim(); 
			//logutil.info("recv file name : [" + rcvFileName + "]");
			String result = "";
			try {
				sc1.sendFR01String(fullText);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// 1�� waiting�� input stream �б� 
			try{   Thread.sleep(1000);  } catch (Exception e) {	}
			try {
				result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
			} catch (Exception e1) {
				// 1�� waiting�� input stream �б� 
				try{   Thread.sleep(1000);  } catch (Exception e) {	}
				try {
					result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
				} catch (Exception e2) {
					// 1�� waiting�� input stream �б� 
					try{   Thread.sleep(1000);  } catch (Exception e) {	}
					try {
						result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
					} catch (Exception e3) {
						logutil.error("���� ����ð� �ʰ� / ���� ���� ���� ����", e3);
						e3.printStackTrace();
					}
				}
				
			}

			logutil.info("Ŭ���̾�Ʈ ���� ��� [" + result + "]");
			
			if(result != null & result.contains("SUCCESS")) {
				String[] rslt = result.split("\\|");
				String rsltRecvFileSize = rslt[1];
				// FR03
				// ���� �������� ���� ���� (FR03) : ������ -> KIS
				try {
					logutil.info("FR03 ���� ���� ���� ");
					sc1.sendFR03String(ftUtils.makeFr03(rsltRecvFileSize).toString());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			} else {
				logutil.info("####### ���� ���� ����. ");
			}
			
			logutil.info("####### socket close ");
			sc1.close();			
			

		} catch (IOException e2) {
			try {sc1.close();} catch (IOException e) {e.printStackTrace();}
			e2.printStackTrace();
		} finally {
		}
		
		
		
	
		
		
	}
	
	/**
	 * ���� �Ծ࿡ ���� ���� �۽� 
	 * 
	 * @param localFileName
	 * @param recvType
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private static void uploadExec(String fileName, String sendType) throws UnknownHostException, IOException {
		KisFTUtils ftUtils;
		ServerInfoUtil util;
		System.out.println("uploadExec(String fileName, String sendType)");
		System.out.println("uploadExec() :: envPath :: " + envPath);
		if(envPath == null) {
			ftUtils = new KisFTUtils();
			util = new ServerInfoUtil();
		} else {
			ftUtils = new KisFTUtils(envPath);
			util = new ServerInfoUtil(envPath);
		}

		
		HashMap<String, Object> map;
		
		String serverType 	= "";
		String recvFilePath =  "";
		String serverSendCode =  ""; 
		String serverOrgCode =  "";
		ArrayList<String> arrFileList = new ArrayList();
		try {
			map = util.getSocketServerInfo();
			serverType 	= map.get("serverType").toString();
			socketIp 	= map.get("serverIp").toString();
			socketPort 	= Integer.parseInt(map.get("serverPort").toString());
			recvFilePath =  map.get("serverRecvPath").toString();
			serverSendCode =  sendType; // map.get("serverRecvCode").toString();
			serverOrgCode =  map.get("serverOrgCode").toString();
			socketEncode = map.get("serverEncodeType").toString();
			socketTimeout = Integer.parseInt(map.get("serverSocketTimeout").toString());
			socketPacketCount = Integer.parseInt(map.get("serverSocketPacketCount").toString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		logutil.info("Client :: file send start ");
		// �ð� ��������
		long lStartTime = System.currentTimeMillis();
		
		// ���� ��� ȣ��
		KisFtClient ftClient = new KisFtClient(envPath);
		
		//[1] ==== relay ������ ���� ==================
		logutil.info("���Ӽ��� ip   :" + socketIp);
		logutil.info("���Ӽ��� port :" + socketPort);
		
		logutil.info("socket timeout :" + socketTimeout);
		logutil.info("socket packet count  :" + socketPacketCount);
		logutil.info("connect client");
		ftClient.connect(socketIp, socketPort, socketTimeout);
		
		logutil.info("client �� ���� makeFT01 ���� �߼� ");
		// ���� ���� ���� ����(FT01) : ������ -> KIS
		// ������ ������ �������� �۾� �� 
		// ���� �۽� ����(FT12) : KIS -> ������
		// FT12 ���� ���� .
		String fullText  = "";

		fullText = ftUtils.makeFT01(sendType, fileName).toString();
		
		String result = "";
		logutil.info("Ŭ���̾�Ʈ FT01 ���� �۽� [" + fullText + "]");
		try {
			ftClient.sendFT01String(fullText);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// 1�� waiting�� input stream �б� 
		try{   Thread.sleep(1000);  } catch (Exception e) {	}
		try {
			result = ftClient.recvFT12String();
		} catch (Exception e1) {
			// 1�� waiting�� input stream �б� 
			try{   Thread.sleep(1000);  } catch (Exception e) {	}
			try {
				result = ftClient.recvFT12String();
			} catch (Exception e2) {
				// 1�� waiting�� input stream �б� 
				try{   Thread.sleep(1000);  } catch (Exception e) {	}
				try {
					result = ftClient.recvFT12String();
				} catch (Exception e3) {
					logutil.error("���� ����ð� �ʰ� / ���� ���� ���� ����", e3);
					e3.printStackTrace();
				}
			}
			
		}		
		logutil.info("Ŭ���̾�Ʈ ���� ��� [" + result + "]");
		
		// ���������ڵ尡 FR12 && ���α����� 1�� ��� 
		if(KisFtConstant.CODE_FT12.equals(result.substring(0, 4)) ) { 
			
			if(	KisFtConstant.ACCEPT_YES.equals(result.substring(14, 15))) {
				// ���� ���� �����κ� ����. 
				logutil.info("���� ���� �����κ� ����. 1024byte �� �������� outputstream ���� ����");
				// ���� ���� ���� : ������ -> KIS
				// ���� �������� ��û ���� (FR13) : KIS -> ������
				logutil.info("############### ���� �۽� " );
				OutputStream output = ftClient.getSocket().getOutputStream();
				
				// relay ������ ���� ����. 
				//String sendRslt = fileSend(dos, recvFilePath, fileName.trim());
				File file = new File(sendPath + File.separator + fileName);
				try {
					// �۽��� ������ ũ�� ����. 
					long lSendFileSize = file.length(); 
					FileInputStream fis = new FileInputStream(file);
					int len;
					byte[] buf = new byte[1024];
					long total = 0L;
					int sendCnt = 0;
					
					// ���� ����. 
					// 1024 byte ���� �� ��� 
					if( lSendFileSize < Long.parseLong("1024")) {
						while((len = fis.read(new byte[ (int)lSendFileSize ])) != -1) {
							if(len == 0 ) {
								break;
							}
							
							output.write(buf, 0, len);
							total += len;
							
							// 64kbps, ��Ŷ �� �������� delay �߰���.
							// 2020.06.26
							try{    
								Thread.sleep(1000/socketPacketCount);  
							} catch (Exception e) {	
								
							}
						}
							
					} else {
						while((len = fis.read(buf)) != -1) {
							if(len == 0 ) {
								break;
							}
							
							output.write(buf, 0, len);
							total += len;
							
							// 64kbps, ��Ŷ �� �������� delay �߰���.
							// 2020.06.26
							try{    
								Thread.sleep(1000/socketPacketCount);  
							} catch (Exception e) {	
								
							}
							
						}
						
					}
				
					logutil.info("���� �۽� ����. ");
					// ���� ���� ���� �˸� ���� ���� ���� ������ �� �� �� �ֱ�. 
					// 64kbps �� �ʴ� 4-5��Ŷ �������� �������� ���Žÿ� ���� �߻��ؼ� �߰����� 
					// 0.5 �� delay 
					try{   Thread.sleep(500);  } catch (Exception e) {	}

					// ���� ���� ���� �˸� (FT03) : ������ -> KIS
					logutil.info(" ���� ���� ���� �˸� (FT03) : ������ -> KIS ");
					ftClient.sendFT03String(ftUtils.makeFT03().toString());
					
					// ���� �������� ���� ����( FT13) : KIS -> ������
					logutil.info(" ���� �������� ���� ����( FT13) : KIS -> ������ ");
					// 1�� waiting�� input stream �б� 
					try{   Thread.sleep(1000);  } catch (Exception e) {	}
					try {
						result = ftClient.recvFT13String();
					} catch (Exception e1) {
						// 1�� waiting�� input stream �б� 
						try{   Thread.sleep(1000);  } catch (Exception e) {	}
						try {
							result = ftClient.recvFT13String();
						} catch (Exception e2) {
							// 1�� waiting�� input stream �б� 
							try{   Thread.sleep(1000);  } catch (Exception e) {	}
							try {
								result = ftClient.recvFT13String();
							} catch (Exception e3) {
								logutil.error("���� ����ð� �ʰ� / ���� ���� ���� ����", e3);
								e3.printStackTrace();
							}
						}
						
					}						
				} catch (Exception e) {
					logutil.error("Error : File not exist!! " + e.getMessage(), e);
				}
				
				
				
				
				//result = sc1.sendString(KisFTUtils.makeFr02().toString());
			} else {
				// �����ڵ� ���� 
				if(KisFtConstant.REJECT_CODE_1001.equals(result.substring(16, 19))) {
					logutil.info("Error : code : [" + KisFtConstant.ERROR_CODE_0001 +" :�� ���� ���� �۽�] " + result.substring(20, 69));
				} else if(KisFtConstant.REJECT_CODE_1002.equals(result.substring(16, 19))) {
					logutil.info("Error : code : [" + KisFtConstant.ERROR_CODE_0002 +" :û���� �۾��� ���� ����] " + result.substring(20, 69));
				}
			}
			
		} else {
			// ���������ڵ尡 FR12
			logutil.info("�߸��� ���� ���� �ڵ� ���۵� ");
		}
		
		//[3] ==== ������ ��������! ==================
		logutil.info("connect close ");
		ftClient.close();
		
		long lFinishTime = System.currentTimeMillis();
		long lEstTime = (lFinishTime - lStartTime);
		logutil.info("############### kis server���� ���� ��� �ҿ� �ð�: " + lEstTime / 1000.0 + " ��");
		
	}
	
	
	/**
	 * ���� �Ծ࿡ ���� ���� �۽� 
	 * 
	 * @param localFileName
	 * @param recvType
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private static void uploadExecSpecific(String fileName, String sendType) throws UnknownHostException, IOException {
		KisFTUtils ftUtils;
		ServerInfoUtil util;
		System.out.println("uploadExecSpecific(String fileName, String sendType)");
		System.out.println("uploadExecSpecific() :: envPath :: " + envPath);
		if(envPath == null) {
			ftUtils = new KisFTUtils();
			util = new ServerInfoUtil();
		} else {
			ftUtils = new KisFTUtils(envPath);
			util = new ServerInfoUtil(envPath);
		}

		
		HashMap<String, Object> map;
		
		String serverType 	= "";
		String recvFilePath =  "";
		String serverSendCode =  ""; 
		String serverOrgCode =  "";
		ArrayList<String> arrFileList = new ArrayList();
		try {
			map = util.getSocketServerInfo();
			serverType 	= map.get("serverType").toString();
			socketIp 	= map.get("serverIp").toString();
			socketPort 	= Integer.parseInt(map.get("serverPort").toString());
			recvFilePath =  map.get("serverRecvPath").toString();
			serverSendCode =  sendType; // map.get("serverRecvCode").toString();
			serverOrgCode =  map.get("serverOrgCode").toString();
			socketEncode = map.get("serverEncodeType").toString();
			socketTimeout = Integer.parseInt(map.get("serverSocketTimeout").toString());
			socketPacketCount = Integer.parseInt(map.get("serverSocketPacketCount").toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		logutil.info("Client :: file send start ");
		// �ð� ��������
		long lStartTime = System.currentTimeMillis();
		
		// ���� ��� ȣ��
		KisFtClient ftClient = new KisFtClient(envPath);
		
		//[1] ==== relay ������ ���� ==================
		logutil.info("���Ӽ��� ip   :" + socketIp);
		logutil.info("���Ӽ��� port :" + socketPort);
		
		logutil.info("socket timeout :" + socketTimeout);
		logutil.info("socket packet count :" + socketPacketCount);
		logutil.info("connect client");
		ftClient.connect(socketIp, socketPort, socketTimeout);
		
		logutil.info("client �� ���� makeFT01 ���� �߼� ");
		// ���� ���� ���� ����(FT01) : ������ -> KIS
		// ������ ������ �������� �۾� �� 
		// ���� �۽� ����(FT12) : KIS -> ������
		// FT12 ���� ���� .
		String fullText  = "";
		fullText = ftUtils.makeFT01(sendType, fileName).toString();
		
		String result = "";
		logutil.info("Ŭ���̾�Ʈ FT01 ���� �۽� [" + fullText + "]");
		try {
			ftClient.sendFT01String(fullText);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// 1�� waiting�� input stream �б� 
		try{   Thread.sleep(1000);  } catch (Exception e) {	}
		try {
			result = ftClient.recvFT12String();
		} catch (Exception e1) {
			// 1�� waiting�� input stream �б� 
			try{   Thread.sleep(1000);  } catch (Exception e) {	}
			try {
				result = ftClient.recvFT12String();
			} catch (Exception e2) {
				// 1�� waiting�� input stream �б� 
				try{   Thread.sleep(1000);  } catch (Exception e) {	}
				try {
					result = ftClient.recvFT12String();
				} catch (Exception e3) {
					logutil.error("���� ����ð� �ʰ� / ���� ���� ���� ����", e3);
					e3.printStackTrace();
				}
			}
			
		}		
		logutil.info("Ŭ���̾�Ʈ ���� ��� [" + result + "]");
		
		// ���������ڵ尡 FR12 && ���α����� 1�� ��� 
		if(KisFtConstant.CODE_FT12.equals(result.substring(0, 4)) ) { 
			
			if(	KisFtConstant.ACCEPT_YES.equals(result.substring(14, 15))) {
				// ���� ���� �����κ� ����. 
				logutil.info("���� ���� �����κ� ����. 1024byte �� �������� outputstream ���� ����");
				// ���� ���� ���� : ������ -> KIS
				// ���� �������� ��û ���� (FR13) : KIS -> ������
				logutil.info("############### ���� �۽� " );
				OutputStream output = ftClient.getSocket().getOutputStream();
				
				// relay ������ ���� ����. 
				//String sendRslt = fileSend(dos, recvFilePath, fileName.trim());
				File file = new File(sendPath + File.separator + fileName);
				try {
					// �۽��� ������ ũ�� ����. 
					long lSendFileSize = file.length(); 
					
					FileInputStream fis = new FileInputStream(file);
					int len;
					byte[] buf = new byte[1024];
					long total = 0L;
					
					// 1024 byte ���� �� ��� 
					if( lSendFileSize < Long.parseLong("1024")) {
						buf = new byte[(int)lSendFileSize];
						while((len = fis.read( buf )) != -1) {
							if(len == 0 ) {
								break;
							}
							output.write(buf, 0, len);
							total += len;
							
							// 64kbps, ��Ŷ �� �������� delay �߰���.
							// 2020.06.26
							try{    
								Thread.sleep(1000/socketPacketCount);
							} catch (Exception e) {	
								
							}
						}
	
					} else {
						
						while((len = fis.read(buf)) != -1) {
							if(len == 0) {
								break;
							}
							output.write(buf, 0, len);
							total += len;
							
							// 64kbps, ��Ŷ �� �������� delay �߰���.
							// 2020.06.26
							try{    
								Thread.sleep(1000/socketPacketCount);
							} catch (Exception e) {	
								
							}
						}
					}
					
					logutil.info("���� �۽� ����. ");
					
					// ���� ���� ���� �˸� (FT03) : ������ -> KIS
					ftClient.sendFT03String(ftUtils.makeFT03().toString());
					
					// ���� �������� ���� ����( FT13) : KIS -> ������
					// 1�� waiting�� input stream �б� 
					try{   Thread.sleep(1000);  } catch (Exception e) {	}
					try {
						result = ftClient.recvFT13String();
					} catch (Exception e1) {
						// 1�� waiting�� input stream �б� 
						try{   Thread.sleep(1000);  } catch (Exception e) {	}
						try {
							result = ftClient.recvFT13String();
						} catch (Exception e2) {
							// 1�� waiting�� input stream �б� 
							try{   Thread.sleep(1000);  } catch (Exception e) {	}
							try {
								result = ftClient.recvFT13String();
							} catch (Exception e3) {
								logutil.error("���� ����ð� �ʰ� / ���� ���� ���� ����", e3);
								e3.printStackTrace();
							}
						}
						
					}						
				} catch (Exception e) {
					logutil.error("Error : File not exist!! " + e.getMessage(), e);
				}
				
				
			} else {
				// ���� ��û�� ������ �������� ����
				if(KisFtConstant.REJECT_CODE_1001.equals(result.substring(16, 19))) {
					logutil.info("Error : code : [" + KisFtConstant.ERROR_CODE_0001 +" :�� ���� ���� �۽�] " + result.substring(20, 69));
				} else if(KisFtConstant.REJECT_CODE_1002.equals(result.substring(16, 19))) {
					logutil.info("Error : code : [" + KisFtConstant.ERROR_CODE_0002 +" :û���� �۾��� ���� ����] " + result.substring(20, 69));
				}
			}
			
		} else {
			// ���������ڵ尡 FR12
			logutil.info("�߸��� ���� ���� �ڵ� ���۵� ");
		}
		
		//[3] ==== ������ ��������! ==================
		logutil.info("connect close 1");
		ftClient.close();
		
		long lFinishTime = System.currentTimeMillis();
		long lEstTime = (lFinishTime - lStartTime);
		logutil.info("############### kis server���� ���� ��� �ҿ� �ð�: " + lEstTime / 1000.0 + " ��");
		
	}
	
	

	/**
	 * DataInputStream �� ���� ������ �����Ͽ� ���ÿ� ����
	 * 
	 * @param dis
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static String fileWrite(DataInputStream dis, String filePath, String fileName) {
		String rslt = "";
		
		if("".equals(filePath)) {
			filePath = "C:/socket-server";
		}
		
		try {
			// ���ϸ��� ���� �ް� ���ϸ� ���� 
			logutil.info("## Client: File Name : [" + fileName + "]");
			
			// file ���� �� ���Ͽ� ���� ��� ��Ʈ�� ���� 
			File file = new File(filePath  + File.separator + fileName) ;
			
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			logutil.info("## Client: File �����Ϸ� : [" + fileName + "]");
			
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
			
			logutil.info("## Client: File ���ſϷ�  ");
			logutil.info("## Client: ���� ���� ������ : [" + recvFileSize + "]");
		} catch (IOException e) {
			e.printStackTrace();
			rslt = "ERROR";
		} finally {
			try {bos.close();} catch (IOException e) {e.printStackTrace();}
			try {fos.close();} catch (IOException e) {e.printStackTrace();}
		}
		
		return rslt;
	}
	
	
	
	
	private List<String> optionHelpStrings = new ArrayList<String>();
	
	/**
	 * �ɼ� ����???
	 * @param option
	 * @param helpString
	 * @return
	 */
    private Option addHelp(Option option, String helpString) {
        optionHelpStrings.add(" -" + option.shortForm() + ", --" + option.longForm() + ": " + helpString);
        return option;

    }

    /**
     * �ɼ� ���
     */
	private void printUsage() {

		System.err.println("usage: kisFtClient  [options]");

		for (Iterator<String> i = optionHelpStrings.iterator(); i.hasNext(); ) {
	        System.err.println(i.next());

		}
	}	
	
	/**
	 *  ���ڷ� ���� �� �� ��ŭ ��� 
	 * @param stime
	 */
	private static void sleepSendSocket(int stime) {
		try{    
			Thread.sleep(stime);  
		} catch (Exception e) {	
			
		}
	}
	
	public static void execCommand (String cmd) {
		
		try {
			Runtime rt = Runtime.getRuntime(); 
			Process proc = rt.exec(cmd); //�ý��� ��ɾ�

			InputStream is = proc.getInputStream(); 
			InputStreamReader isr = new InputStreamReader(is); 
			BufferedReader br = new BufferedReader(isr);

			String line;
			while((line=br.readLine())!= null){
				System.out.println(line);
				System.out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
