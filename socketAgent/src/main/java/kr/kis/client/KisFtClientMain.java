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
	
		// 시간 측정위해
		long lStartTime = System.currentTimeMillis();

		KisFtClientMain main = new KisFtClientMain();
		CmdLineParser parser = new CmdLineParser();

		// Define Option Help
		CmdLineParser.Option prop = main.addHelp(parser.addStringOption('p', "env property"), "This option Environment variable read.");
		CmdLineParser.Option upload = main.addHelp(parser.addStringOption('u', "send start"), " KIS 서버로 파일 전송하는 기능 제공.");
		CmdLineParser.Option download = main.addHelp(parser.addStringOption('d', "receive start"), "KIS 서버에서 파일 수신하는 기능제공.");

		
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
			
			//환경변수 확인.
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
				// 업로드 확인
				if(uploadOptValue != null ) {
					strType = "upload";
					if(!"".equals(uploadOptValue) ) {
						localFileName = uploadOptValue;
					} 			
				}

			} catch (Exception ex1) {
			}
			
			// receive 확인
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
//			logutil.info("######### Main :: LogUtil::envPath : " + envPath);
//			
//			logutil.info("######### Main :: KisFTUtils::envPath : " + envPath);
			KisFTUtils ftUtils = new KisFTUtils(envPath);
			
			try {
//				logutil.info("######### Main :: util.getSocketServerInfo(): ");
				map = util.getSocketServerInfo();
				serverType 	= map.get("serverType").toString();
				socketIp 	= map.get("serverIp").toString();
				socketPort 	= Integer.parseInt(map.get("serverPort").toString());
				recvFilePath =  map.get("serverRecvPath").toString();
				serverRecvCode =  map.get("serverRecvCode").toString();
				serverOrgCode =  map.get("serverOrgCode").toString();
				socketEncode = map.get("serverEncodeType").toString();
				socketTimeout = Integer.parseInt(map.get("serverSocketTimeout").toString());
				
				recvFileList = (ArrayList<String>)map.get("serverRecvFiles");
				
				relayServerUseYn = map.get("serverRelayUseYn").toString();
				
				sendPath 		=  map.get("serverSendPath").toString();
				sendTypeCode 	=  map.get("serverSendCode").toString();
				sendTestYn 		=  map.get("serverSendTestYn").toString();
				
				sendConfigDateType 	=  map.get("serverSendConfigDateType").toString();
				execCmd 		= map.get("execCommand").toString();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
						
			
			if("".equals(strType)) {
				logutil.info(" option is required ::::::: -u \"\" -p \"KIS_FTAgent\"" );
				logutil.info(" option is required ::::::: -d  \"\" -p \"KIS_FTAgent\"" );
			}
			
			
			logutil.info("######### Socket Agent Start ");
			/**
			 * 송신
			 */
			if("upload".equals(strType)) {
				
				if("".equals(localFileName)) {
					/**
					 * 전송을 위한 파일리스트를 cfg파일에서 읽기 
					 * 
					 * 
					 */
					SendFileInfoRead sfir; //
					if(envPath == null ) {
						sfir = new SendFileInfoRead();
					} else {
						System.out.println("####### sfir :: envPath ::" + envPath);
						sfir = new SendFileInfoRead(envPath);
						
					}
					ArrayList<String> arrFileList = new ArrayList();
					try {
						logutil.info("yymmdd :: " + CustStringUtils.getToday(sendConfigDateType));
						arrFileList = sfir.readConfig(CustStringUtils.getToday(sendConfigDateType));
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					
					for (int i=0; i < arrFileList.size(); i++) {
						String sFileName = arrFileList.get(i);
						
						// 업로드(송신) 모듈 호출
						// 데이터 유형을 EDI 로 고정해서 테스트  
						uploadExec(sFileName, KisFtConstant.SEND_FILE_TYPE_EDI); 
					}
				} else {
					// 업로드(송신) 모듈 호출
					// 데이터 유형을 EDI 로 고정해서 테스트
					logutil.info("localFileName : " + localFileName + " 송신모드 ");
					uploadExec(localFileName, KisFtConstant.SEND_FILE_TYPE_EDI); 
					
				}

				// 정상 종료시 시스템 명령어 수행. 
				// 송신시에는 필요 없어 주석 처리 함. 
				//logutil.info("###### execCmd::" + execCmd);
				//execCommand(execCmd);

			}
			
			/**
			 * 수신 
			 */
			if("download".equals(strType)) {
				
				
				// 다운로드(수신) 모듈 호출
				if(!"".equals(serverRecvCode)) {
					if("A".equals(serverRecvCode) && "".equals(localFileName)) {
						// 전체 문서 가져오기
						if(recvFileList.size() > 0) {
							for(int i=0; i < recvFileList.size(); i++) {
								downloadExec(localFileName, KisFtConstant.RCV_FILE_TYPE_EDI, recvFileList.get(i));
							}
						}
						
					} else {
						downloadExec(localFileName, "");
					}
				}
				
				// 정상 종료시 시스템 명령어 수행. 
				logutil.info("###### execCmd::" + execCmd);
				execCommand(execCmd);
			}
			
		} catch(Exception e){
			logutil.info("Client 오류 발생!", e);
			e.printStackTrace();
		} finally {
		}
	}
	

	
	/**
	 * 전문 규약에 따른 파일 수신 
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
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		// 시간 측정위해
		long lStartTime = System.currentTimeMillis();
		
		// 다운로드 모듈 호출
		KisFtClient sc1 = new KisFtClient(envPath);
		
		//[1] ==== 서버에 연결 ==================
		logutil.info("접속서버 ip   :" + socketIp);
		logutil.info("접속서버 port :" + socketPort);
		logutil.info("Encoding :" + socketEncode);
		
		logutil.info("socket timeout :" + socketTimeout);
		String rcvFileName = ""; 
				
		try {
			logutil.info("connect client");
			sc1.connect(socketIp, socketPort, socketTimeout);
			
			logutil.info("FR01 전문 발송 ");
			// 파일 수신 승인 전문(FR12) : KIS -> 가맹점
			// 전문을 보내면 서버에서 작업 후 
			// 파일 수신 전문(FR02) : KIS -> 가맹점 
			// FR02 전문 리턴 .
			String fullText  = "";
			
			fullText = ftUtils.makeFr01("", localFileName).toString();
			
			rcvFileName = localFileName; 
			String result = "";
			try {
				sc1.sendFR01String(fullText);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//1초 waiting
			try{    Thread.sleep(1000);  } catch (Exception e) {	}
			// 1초 waiting후 input stream 읽기 
			try {
				logutil.info("FR12 전문 수신,파일수신,수신완료  ");
				result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
			} catch (Exception e1) {
				// 1초 waiting 후 inputstream 이 없으면 다시 1초 waiting후 input stream 읽기 
				//1초 waiting
				try{    Thread.sleep(1000);  } catch (Exception e) {	}
				try {
					result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
				} catch (Exception e2) {
					try{    Thread.sleep(1000);  } catch (Exception e) {	}
					// 1초 waiting 후 inputstream 이 없으면 다시 1초 waiting후 input stream 읽기 
					try {
						result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
					} catch (Exception e3) {
						logutil.error("서버 응답시간 초과 / 서버 응답 없음 오류", e3);
						e3.printStackTrace();
					}
				}

					
			}

			logutil.info("클라이언트 받은 결과 [" + result + "]");
			
			if(result != null & result.contains("SUCCESS")) {
				String[] rslt = result.split("\\|");
				String rsltRecvFileSize = rslt[1];
				// FR03
				// 파일 수신종료 응답 전문 (FR03) : 가맹점 -> KIS
				try {
					logutil.info("FR03 전문 전송 시작 ");
					sc1.sendFR03String(ftUtils.makeFr03(rsltRecvFileSize).toString());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
			logutil.info("####### socket close ");
			sc1.close();	

			/**
			 * 릴레이서버 사용하여 전달 허용시 
			 */
			if("Y".equals(relayServerUseYn)) {
				
				/**
				 * 릴레이서버에 파일 전달 부분
				 * 
				 */
				File f = new File(recvFilePath+File.separator+rcvFileName.trim());
				
				if(f.exists()) 	{
					// 릴레이 서버에 파일 전달하기
					HashMap<String, Object> relaymap = new HashMap<String, Object>();
					try {
						relaymap = util.getRelayServerInfo("ST");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String relaysocketIp 	= relaymap.get("relayServerIp").toString();
					int relaysocketPort 	= Integer.parseInt(relaymap.get("relayServerPort").toString());
					
					KisFtClient relayClient = new KisFtClient();
					// relay 서버에 접속
					logutil.info("############### relay서버 ip: " + relaysocketIp);
					logutil.info("############### relay서버 port: " + relaysocketPort);
					logutil.info("############### relay서버 접속시도 " );
					try {
						relayClient.connect(relaysocketIp, relaysocketPort, socketTimeout);
						
						logutil.info("############### relay서버로 파일 송신 " );
						dos = new DataOutputStream(relayClient.getSocket().getOutputStream());
						
						// relay 서버에 파일 전송. 
						String sendRslt = fileSend(dos, recvFilePath, rcvFileName.trim());
						
						if(KisFtConstant.RCV_SUCC.equals(sendRslt)) {
							logutil.info("파일 송신 성공. ");
						}
						// realay 서버 접속 종료
						relayClient.close();	
						
					} catch (IOException e) {
						// realay 서버 접속 종료
						try {
							relayClient.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						e.printStackTrace();
					}
					
					
				} else {
					logutil.error(rcvFileName + " 파일 수신 오류로 인해 파일이 존재 하지 않습니다. ");
				}
				
			}
						
		} catch (IOException e2) {
			try {sc1.close();} catch (IOException e) {e.printStackTrace();}
			e2.printStackTrace();
		} finally {
		}
		
		 
		
		
	}
	
	/**
	 * 전문 규약에 따른 파일 수신 
	 * 
	 * @param localFileName
	 * @param recvType
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private static void downloadExec(String localFileName, String recvType, String recvFilePrefix)  {
		KisFTUtils ftUtils;
		ServerInfoUtil util;

//		System.out.println("downloadExec(String localFileName, String recvType, String recvFilePrefix)");
//		System.out.println("downloadExec() :: envPath :: " + envPath);
		if(envPath == null) {
			ftUtils = new KisFTUtils();
			util = new ServerInfoUtil();
		} else {
//			System.out.println("downloadExec() :: envPath :: " + envPath);
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
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		// 시간 측정위해
		long lStartTime = System.currentTimeMillis();
		
		// 다운로드 모듈 호출
		KisFtClient sc1 = new KisFtClient(envPath);
		
		//[1] ==== 서버에 연결 ==================
		logutil.info("접속서버 ip   :" + socketIp);
		logutil.info("접속서버 port :" + socketPort);
		logutil.info("Encoding :" + socketEncode);
		
		logutil.info("socket timeout :" + socketTimeout);
		String rcvFileName = ""; 
				
		try {
			logutil.info("Connect client");
			sc1.connect(socketIp, socketPort, socketTimeout);
			
//			logutil.info("1번을 통한 makeFr01 전문 발송 ");
			// 파일 수신 승인 전문(FR12) : KIS -> 가맹점
			// 전문을 보내면 서버에서 작업 후 
			// 파일 수신 전문(FR02) : KIS -> 가맹점 
			// FR02 전문 리턴 .
			String fullText  = "";
			if(!"".equals(localFileName )) { 
				// 입력받은 파일명이 존재하면 해당 파일명을 기준으로 전문 및 파일을 다운로드 
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
			// 1초 waiting후 input stream 읽기 
			try{   Thread.sleep(1000);  } catch (Exception e) {	}
			try {
				result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
			} catch (Exception e1) {
				// 1초 waiting후 input stream 읽기 
				try{   Thread.sleep(1000);  } catch (Exception e) {	}
				try {
					result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
				} catch (Exception e2) {
					// 1초 waiting후 input stream 읽기 
					try{   Thread.sleep(1000);  } catch (Exception e) {	}
					try {
						result = sc1.recvFR12_02_13String( recvFilePath, rcvFileName.trim());
					} catch (Exception e3) {
						logutil.error("서버 응답시간 초과 / 서버 응답 없음 오류", e3);
						e3.printStackTrace();
					}
				}
				
			}

			logutil.info("클라이언트 받은 결과 [" + result + "]");
			
			if(result != null & result.contains("SUCCESS")) {
				String[] rslt = result.split("\\|");
				String rsltRecvFileSize = rslt[1];
				// FR03
				// 파일 수신종료 응답 전문 (FR03) : 가맹점 -> KIS
				try {
					logutil.info("FR03 전문 전송 시작 ");
					sc1.sendFR03String(ftUtils.makeFr03(rsltRecvFileSize).toString());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			} else {
				logutil.info("####### 파일 수신 오류. ");
			}
			
			logutil.info("####### socket close ");
			sc1.close();			
			
			/**
			 * 릴레이서버 사용하여 전달 허용시 
			 */
			if("Y".equals(relayServerUseYn)) {
				
				/**
				 * 릴레이서버에 파일 전달 부분
				 * 
				 */
				File f = new File(recvFilePath+File.separator+rcvFileName.trim());
				
				if(f.exists()) 	{
					// 릴레이 서버에 파일 전달하기
					HashMap<String, Object> relaymap = new HashMap<String, Object>();
					try {
						relaymap = util.getRelayServerInfo("ST");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String relaysocketIp 	= relaymap.get("relayServerIp").toString();
					int relaysocketPort 	= Integer.parseInt(relaymap.get("relayServerPort").toString());
					
					KisFtClient relayClient = new KisFtClient();
					// relay 서버에 접속
					logutil.info("############### relay서버 ip: " + relaysocketIp);
					logutil.info("############### relay서버 port: " + relaysocketPort);
					logutil.info("############### relay서버 접속시도 " );
					try {
						relayClient.connect(relaysocketIp, relaysocketPort, socketTimeout);
						
						logutil.info("############### relay서버로 파일 송신 " );
						dos = new DataOutputStream(relayClient.getSocket().getOutputStream());
						
						// relay 서버에 파일 전송. 
						String sendRslt = fileSend(dos, recvFilePath, rcvFileName.trim());
						
						if(KisFtConstant.RCV_SUCC.equals(sendRslt)) {
							logutil.info("파일 송신 성공. ");
						}
						// realay 서버 접속 종료
						relayClient.close();	
						
					} catch (IOException e) {
						// realay 서버 접속 종료
						try {
							relayClient.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						e.printStackTrace();
					}
					
					
				} else {
					logutil.error(rcvFileName + " 파일 수신 오류로 인해 파일이 존재 하지 않습니다. ");
				}			
			}
		} catch (IOException e2) {
			try {sc1.close();} catch (IOException e) {e.printStackTrace();}
			e2.printStackTrace();
		} finally {
		}
		
		
		
	
		
		
	}
	
	/**
	 * 전문 규약에 따른 파일 송신 
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
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		logutil.info("Client :: file send start ");
		// 시간 측정위해
		long lStartTime = System.currentTimeMillis();
		
		// 전송 모듈 호출
		KisFtClient ftClient = new KisFtClient(envPath);
		
		//[1] ==== relay 서버에 연결 ==================
		logutil.info("접속서버 ip   :" + socketIp);
		logutil.info("접속서버 port :" + socketPort);
		
		logutil.info("socket timeout :" + socketTimeout);
		logutil.info("connect client");
		ftClient.connect(socketIp, socketPort, socketTimeout);
		
		logutil.info("client 을 통한 makeFT01 전문 발송 ");
		// 파일 수신 승인 전문(FT01) : 가맹점 -> KIS
		// 전문을 보내면 서버에서 작업 후 
		// 파일 송신 전문(FT12) : KIS -> 가맹점
		// FT12 전문 리턴 .
		String fullText  = "";

		fullText = ftUtils.makeFT01(sendType, fileName).toString();
		
		String result = "";
		logutil.info("클라이언트 FT01 전문 송신 [" + fullText + "]");
		try {
			ftClient.sendFT01String(fullText);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// 1초 waiting후 input stream 읽기 
		try{   Thread.sleep(1000);  } catch (Exception e) {	}
		try {
			result = ftClient.recvFT12String();
		} catch (Exception e1) {
			// 1초 waiting후 input stream 읽기 
			try{   Thread.sleep(1000);  } catch (Exception e) {	}
			try {
				result = ftClient.recvFT12String();
			} catch (Exception e2) {
				// 1초 waiting후 input stream 읽기 
				try{   Thread.sleep(1000);  } catch (Exception e) {	}
				try {
					result = ftClient.recvFT12String();
				} catch (Exception e3) {
					logutil.error("서버 응답시간 초과 / 서버 응답 없음 오류", e3);
					e3.printStackTrace();
				}
			}
			
		}		
		logutil.info("클라이언트 받은 결과 [" + result + "]");
		
		// 전문구분코드가 FR12 && 승인구분이 1인 경우 
		if(KisFtConstant.CODE_FT12.equals(result.substring(0, 4)) ) { 
			
			if(	KisFtConstant.ACCEPT_YES.equals(result.substring(14, 15))) {
				// 파일 전송 전문부분 실행. 
				logutil.info("파일 전송 전문부분 실행. 1024byte 씩 서버에서 outputstream 으로 전송");
				// 파일 전송 전문 : 가맹점 -> KIS
				// 파일 수신종료 요청 전문 (FR13) : KIS -> 가맹점
				logutil.info("############### 파일 송신 " );
				OutputStream output = ftClient.getSocket().getOutputStream();
				
				// relay 서버에 파일 전송. 
				//String sendRslt = fileSend(dos, recvFilePath, fileName.trim());
				File file = new File(sendPath + File.separator + fileName);
				try {
					FileInputStream fis = new FileInputStream(file);
					int len;
					byte[] buf = new byte[1024];
					long total = 0L;
					while((len = fis.read(buf)) != -1) {
						output.write(buf, 0, len);
						total += len;
					}
				
					logutil.info("파일 송신 성공. ");
					
					// 파일 전송 종료 알림 (FT03) : 가맹점 -> KIS
					ftClient.sendFT03String(ftUtils.makeFT03().toString());
					
					// 파일 전송종료 응답 전문( FT13) : KIS -> 가맹점
					// 1초 waiting후 input stream 읽기 
					try{   Thread.sleep(1000);  } catch (Exception e) {	}
					try {
						result = ftClient.recvFT13String();
					} catch (Exception e1) {
						// 1초 waiting후 input stream 읽기 
						try{   Thread.sleep(1000);  } catch (Exception e) {	}
						try {
							result = ftClient.recvFT13String();
						} catch (Exception e2) {
							// 1초 waiting후 input stream 읽기 
							try{   Thread.sleep(1000);  } catch (Exception e) {	}
							try {
								result = ftClient.recvFT13String();
							} catch (Exception e3) {
								logutil.error("서버 응답시간 초과 / 서버 응답 없음 오류", e3);
								e3.printStackTrace();
							}
						}
						
					}						
				} catch (Exception e) {
					logutil.error("Error : File not exist!! " + e.getMessage(), e);
				}
				
				
				
				
				//result = sc1.sendString(KisFTUtils.makeFr02().toString());
			} else {
				// 수신 요청한 파일이 존재하지 않음
				if(KisFtConstant.REJECT_CODE_1001.equals(result.substring(16, 19))) {
					logutil.info("Error : code : [" + KisFtConstant.ERROR_CODE_0001 +" :기 전송 파일 송신] " + result.substring(20, 69));
				} else if(KisFtConstant.REJECT_CODE_1002.equals(result.substring(16, 19))) {
					logutil.info("Error : code : [" + KisFtConstant.ERROR_CODE_0002 +" :청구시 작업일 세팅 에러] " + result.substring(20, 69));
				}
			}
			
		} else {
			// 전문구분코드가 FR12
			logutil.info("잘못된 전문 구분 코드 전송됨 ");
		}
		
		//[3] ==== 서버와 연결종료! ==================
		logutil.info("connect close ");
		ftClient.close();
		
		long lFinishTime = System.currentTimeMillis();
		long lEstTime = (lFinishTime - lStartTime);
		logutil.info("############### kis server에서 전문 통신 소요 시간: " + lEstTime / 1000.0 + " 초");
		
	}
	
	
	/**
	 * 전문 규약에 따른 파일 송신 
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
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		logutil.info("Client :: file send start ");
		// 시간 측정위해
		long lStartTime = System.currentTimeMillis();
		
		// 전송 모듈 호출
		KisFtClient ftClient = new KisFtClient(envPath);
		
		//[1] ==== relay 서버에 연결 ==================
		logutil.info("접속서버 ip   :" + socketIp);
		logutil.info("접속서버 port :" + socketPort);
		
		logutil.info("socket timeout :" + socketTimeout);
		logutil.info("connect client");
		ftClient.connect(socketIp, socketPort, socketTimeout);
		
		logutil.info("client 을 통한 makeFT01 전문 발송 ");
		// 파일 수신 승인 전문(FT01) : 가맹점 -> KIS
		// 전문을 보내면 서버에서 작업 후 
		// 파일 송신 전문(FT12) : KIS -> 가맹점
		// FT12 전문 리턴 .
		String fullText  = "";
		fullText = ftUtils.makeFT01(sendType, fileName).toString();
		
		String result = "";
		logutil.info("클라이언트 FT01 전문 송신 [" + fullText + "]");
		try {
			ftClient.sendFT01String(fullText);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// 1초 waiting후 input stream 읽기 
		try{   Thread.sleep(1000);  } catch (Exception e) {	}
		try {
			result = ftClient.recvFT12String();
		} catch (Exception e1) {
			// 1초 waiting후 input stream 읽기 
			try{   Thread.sleep(1000);  } catch (Exception e) {	}
			try {
				result = ftClient.recvFT12String();
			} catch (Exception e2) {
				// 1초 waiting후 input stream 읽기 
				try{   Thread.sleep(1000);  } catch (Exception e) {	}
				try {
					result = ftClient.recvFT12String();
				} catch (Exception e3) {
					logutil.error("서버 응답시간 초과 / 서버 응답 없음 오류", e3);
					e3.printStackTrace();
				}
			}
			
		}		
		logutil.info("클라이언트 받은 결과 [" + result + "]");
		
		// 전문구분코드가 FR12 && 승인구분이 1인 경우 
		if(KisFtConstant.CODE_FT12.equals(result.substring(0, 4)) ) { 
			
			if(	KisFtConstant.ACCEPT_YES.equals(result.substring(14, 15))) {
				// 파일 전송 전문부분 실행. 
				logutil.info("파일 전송 전문부분 실행. 1024byte 씩 서버에서 outputstream 으로 전송");
				// 파일 전송 전문 : 가맹점 -> KIS
				// 파일 수신종료 요청 전문 (FR13) : KIS -> 가맹점
				logutil.info("############### 파일 송신 " );
				OutputStream output = ftClient.getSocket().getOutputStream();
				
				// relay 서버에 파일 전송. 
				//String sendRslt = fileSend(dos, recvFilePath, fileName.trim());
				File file = new File(sendPath + File.separator + fileName);
				try {
					FileInputStream fis = new FileInputStream(file);
					int len;
					byte[] buf = new byte[1024];
					long total = 0L;
					while((len = fis.read(buf)) != -1) {
						output.write(buf, 0, len);
						total += len;
					}
					
					logutil.info("파일 송신 성공. ");
					
					// 파일 전송 종료 알림 (FT03) : 가맹점 -> KIS
					ftClient.sendFT03String(ftUtils.makeFT03().toString());
					
					// 파일 전송종료 응답 전문( FT13) : KIS -> 가맹점
					// 1초 waiting후 input stream 읽기 
					try{   Thread.sleep(1000);  } catch (Exception e) {	}
					try {
						result = ftClient.recvFT13String();
					} catch (Exception e1) {
						// 1초 waiting후 input stream 읽기 
						try{   Thread.sleep(1000);  } catch (Exception e) {	}
						try {
							result = ftClient.recvFT13String();
						} catch (Exception e2) {
							// 1초 waiting후 input stream 읽기 
							try{   Thread.sleep(1000);  } catch (Exception e) {	}
							try {
								result = ftClient.recvFT13String();
							} catch (Exception e3) {
								logutil.error("서버 응답시간 초과 / 서버 응답 없음 오류", e3);
								e3.printStackTrace();
							}
						}
						
					}						
				} catch (Exception e) {
					logutil.error("Error : File not exist!! " + e.getMessage(), e);
				}
				
				
			} else {
				// 수신 요청한 파일이 존재하지 않음
				if(KisFtConstant.REJECT_CODE_1001.equals(result.substring(16, 19))) {
					logutil.info("Error : code : [" + KisFtConstant.ERROR_CODE_0001 +" :기 전송 파일 송신] " + result.substring(20, 69));
				} else if(KisFtConstant.REJECT_CODE_1002.equals(result.substring(16, 19))) {
					logutil.info("Error : code : [" + KisFtConstant.ERROR_CODE_0002 +" :청구시 작업일 세팅 에러] " + result.substring(20, 69));
				}
			}
			
		} else {
			// 전문구분코드가 FR12
			logutil.info("잘못된 전문 구분 코드 전송됨 ");
		}
		
		//[3] ==== 서버와 연결종료! ==================
		logutil.info("connect close 1");
		ftClient.close();
		
		long lFinishTime = System.currentTimeMillis();
		long lEstTime = (lFinishTime - lStartTime);
		logutil.info("############### kis server에서 전문 통신 소요 시간: " + lEstTime / 1000.0 + " 초");
		
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
			logutil.info("File not Exist.");
		    System.exit(0);
		}

		long fileSize = file.length();
		long totalReadBytes = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		byte[] data = new byte[DEFAULT_BUFFER_SIZE];
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
            	totalReadBytes += len;
            }
            dos.flush();
            logutil.info("File transfer completed.");
            
			rslt = "SUCCESS";
			
			logutil.info("## Client: File 송신완료 : " + fileName);
			logutil.info("## Client: 송신 파일 사이즈 : [" + totalReadBytes + "]");
			
		} catch (IOException e) {
			e.printStackTrace();
			rslt = "ERROR";
		} finally {
			try {fis2.close();} catch (IOException e) {e.printStackTrace();}
			try {bis2.close();} catch (IOException e) {e.printStackTrace();}
		}
		
		return rslt;
	}
	
	/**
	 * DataInputStream 을 통해 파일을 수신하여 로컬에 저장
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
			logutil.info("## fileWrite :: Client: file receive starting... ");
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
	
	
	
	
	private List<String> optionHelpStrings = new ArrayList<String>();
	
	/**
	 * 옵션 도움말???
	 * @param option
	 * @param helpString
	 * @return
	 */
    private Option addHelp(Option option, String helpString) {
        optionHelpStrings.add(" -" + option.shortForm() + ", --" + option.longForm() + ": " + helpString);
        return option;

    }

    /**
     * 옵션 출력
     */
	private void printUsage() {

		System.err.println("usage: kisFtClient  [options]");

		for (Iterator<String> i = optionHelpStrings.iterator(); i.hasNext(); ) {
	        System.err.println(i.next());

		}
	}	
	
	
	public static void execCommand (String cmd) {
		
		try {
			Runtime rt = Runtime.getRuntime(); 
			Process proc = rt.exec(cmd); //시스템 명령어

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
