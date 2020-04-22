package kr.kis.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class KisFTUtils {

	static String orgCode ;
	static String recvTypeCode;
	
	static String sendPath;
	static String sendTypeCode;
	static String sendTestYn;
	
	static String serverMode;
	static String sendConfigDateType;
	
	

	public KisFTUtils() {

		
		ServerInfoUtil util = new ServerInfoUtil();
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		String serverType 			= map.get("serverType").toString();
		String socketIp 			= map.get("serverIp").toString();
		int    socketPort 			= Integer.parseInt(map.get("serverPort").toString());
		String recvFilePath 		= map.get("serverRecvPath").toString();
		String serverRecvCode 		= map.get("serverRecvCode").toString();
		String serverOrgCode 		= map.get("serverOrgCode").toString();
		String socketEncode 		= map.get("serverEncodeType").toString();		

		
		orgCode 		= map.get("serverOrgCode").toString();
//		log.info("## KisFTUtils() : orgCode:" + orgCode);
		recvTypeCode 	= map.get("serverRecvCode").toString();
//		log.info("## KisFTUtils() : recvTypeCode:" + recvTypeCode);
		
		sendPath 		=  map.get("serverSendPath").toString();
		sendTypeCode 	=  map.get("serverSendCode").toString();
		sendTestYn 		=  map.get("serverSendTestYn").toString();
		
		
		serverMode 		=  map.get("serverMode").toString();
		// 날짜 타입 
		sendConfigDateType 			= map.get("sendConfigDateType").toString();		
	}
	
	public KisFTUtils(String envPath) {
		
		
		ServerInfoUtil util = new ServerInfoUtil(envPath);
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		String serverType 			= map.get("serverType").toString();
		String socketIp 			= map.get("serverIp").toString();
		int    socketPort 			= Integer.parseInt(map.get("serverPort").toString());
		String recvFilePath 		= map.get("serverRecvPath").toString();
		String serverRecvCode 		= map.get("serverRecvCode").toString();
		String serverOrgCode 		= map.get("serverOrgCode").toString();
		String socketEncode 		= map.get("serverEncodeType").toString();		
		
		
		orgCode 		= map.get("serverOrgCode").toString();
//		log.info("## KisFTUtils() : orgCode:" + orgCode);
		recvTypeCode 	= map.get("serverRecvCode").toString();
//		log.info("## KisFTUtils() : recvTypeCode:" + recvTypeCode);
		
		sendPath 		=  map.get("serverSendPath").toString();
		sendTypeCode 	=  map.get("serverSendCode").toString();
		sendTestYn 		=  map.get("serverSendTestYn").toString();
		
		serverMode 		=  map.get("serverMode").toString();
		// 날짜 타입 
		sendConfigDateType 			= map.get("sendConfigDateType").toString();		
		
	}
	
	/**
	 * 파일 수신 요청 전문(FR01) : 가맹점 -> KIS 
	 * 전문 생성 
	 * @return
	 */
	public StringBuilder makeFr01() {
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR01, 4, "L", " ")); //head 4
	
		log.info("## KisFTUtils().makeFr01() : orgCode:" + orgCode);
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));

		/**
		 * 수신 파일 구분 :         
		 * - E : EDI 입금/반송 내역 파일 (150Byte)
	     * - D : DDC 입금/반송 내역 파일 (150Byte)
	     * - T : 거래내역 파일 (150Byte)
		 */
		sb.append(CustStringUtils.fillSpaceString(recvTypeCode, 1, "L", " "));
		
		/**
		 * 수신 파일명 : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(30)으로 빈공간은 ' '로 채워야 됨.
		 * 	수신 파일명 – 입금/반송 내역 파일명(23 자리) 
         * - 기관코드 (10자리) : 영문(5) + 숫자(5) 
         * - 입금반송코드(5자리)   : 
		 *     REPLY(EDI 입금/반송내역)
         *     DDCRE(DDC 입금/반송내역)
         *     TRANS(거래내역 추출 DATA)
         * - 작업일자 (6자리)   : YYMMDD
         * - ex : MAGNET0001-REPLY.030902
		 */
//		sb.append(CustStringUtils.fillSpaceString("MAGNET0001-REPLY.200203", 30, "R", " "));
//		sb.append(CustStringUtils.fillSpaceString("socketServer.log", 30, "R", " "));
		String rcvFileName = "";
		try {
			rcvFileName = CustStringUtils.getRecvFileName(orgCode, recvTypeCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("### Build file name : [" + rcvFileName+ "]");
		sb.append(CustStringUtils.fillSpaceString(rcvFileName, 30, "R", " "));
		
		/**
		 * Space : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(55)으로 빈공간은 ' '로 채워야 됨.
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 55, "L", " "));
		
		
		return sb;
		
	}
	
	
	
	public StringBuilder makeFr01(String recvTypeCodeValue) throws IOException {
		if(!(("E".equals(recvTypeCodeValue) || "D".equals(recvTypeCodeValue) || "T".equals(recvTypeCodeValue)))) {
			throw new IOException("Incorrect receive file type! please check receive file type.");
		}
			
		recvTypeCode = recvTypeCodeValue;
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR01, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		
		/**
		 * 수신 파일 구분 :         
		 * - E : EDI 입금/반송 내역 파일 (150Byte)
		 * - D : DDC 입금/반송 내역 파일 (150Byte)
		 * - T : 거래내역 파일 (150Byte)
		 */
		sb.append(CustStringUtils.fillSpaceString(recvTypeCode, 1, "L", " ")); 
		
		/**
		 * 수신 파일명 : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(30)으로 빈공간은 ' '로 채워야 됨.
		 * 	수신 파일명 – 입금/반송 내역 파일명(23 자리) 
		 * - 기관코드 (10자리) : 영문(5) + 숫자(5) 
		 * - 입금반송코드(5자리)   : 
		 *     REPLY(EDI 입금/반송내역)
		 *     DDCRE(DDC 입금/반송내역)
		 *     TRANS(거래내역 추출 DATA)
		 * - 작업일자 (6자리)   : YYMMDD
		 * - ex : MAGNET0001-REPLY.030902
		 */
//		sb.append(CustStringUtils.fillSpaceString("MAGNET0001-REPLY.200203", 30, "R", " "));
//		sb.append(CustStringUtils.fillSpaceString("socketServer.log", 30, "R", " "));
		String rcvFileName = "";
		try {
			rcvFileName = CustStringUtils.getRecvFileName(orgCode, recvTypeCode );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("### Build file name : [" + rcvFileName+ "]");
		sb.append(CustStringUtils.fillSpaceString(rcvFileName, 30, "R", " "));
		
		/**
		 * Space : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(55)으로 빈공간은 ' '로 채워야 됨.
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 55, "L", " "));
		
		
		return sb;
		
	}
	
	public StringBuilder makeFr01(String recvTypeCodeValue, String fileName) throws IOException {
		
		if(!( ("".equals(recvTypeCodeValue) || "E".equals(recvTypeCodeValue) || 
				"D".equals(recvTypeCodeValue) || "T".equals(recvTypeCodeValue))) ) {
			throw new IOException("Incorrect receive file type! please check receive file type.");
		}
		
		if("".equals(fileName)) {
			throw new IOException("fileName is not null");
		}
		
//		String orgCode = properties.getProperty("socket.server.orgCode");
		String orgCode = fileName.substring(0, 10);
		log.info("### KisFTUtils().makrFr01(fileName) : orgCode : " + orgCode);
		
//		String recvTypeCode = properties.getProperty("socket.server.recvCode");
		String recvTypeCodeTemp = fileName.substring(11, 16);
		log.info("### KisFTUtils().makrFr01(fileName) : recvTypeCodeTemp : " + recvTypeCodeTemp);
		String recvTypeCode = "";
//		String recvDate = fileName.substring(17, 23);
//		log.info("### KisFTUtils().makrFr01(fileName) : recvDate : " + recvDate);
//		if(KisFtConstant.IN_RET_CODE_EDI.equals(recvTypeCodeTemp )) {
//			recvTypeCode = KisFtConstant.RCV_FILE_TYPE_EDI;;
//		} else if(KisFtConstant.IN_RET_CODE_DDC.equals(recvTypeCodeTemp )) {
//			recvTypeCode = KisFtConstant.RCV_FILE_TYPE_DDC;
//		} else if(KisFtConstant.IN_RET_CODE_TNS.equals(recvTypeCodeTemp )) {
//			recvTypeCode = KisFtConstant.RCV_FILE_TYPE_TNS;;
//		}
		// 전송유형을 고정
		recvTypeCode = KisFtConstant.RCV_FILE_TYPE_EDI;
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR01, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		
		/**
		 * 수신 파일 구분 :         
		 * - E : EDI 입금/반송 내역 파일 (150Byte)
		 * - D : DDC 입금/반송 내역 파일 (150Byte)
		 * - T : 거래내역 파일 (150Byte)
		 */
		sb.append(CustStringUtils.fillSpaceString(recvTypeCode, 1, "L", " ")); 
		
		/**
		 * ----- 2020.02.26 아래 주석의 내용은 무시하고 적용해야 된다고 함. 
		 * 수신 파일명 : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(30)으로 빈공간은 ' '로 채워야 됨.
		 * 	수신 파일명 – 입금/반송 내역 파일명(23 자리) 
		 * - 기관코드 (10자리) : 영문(5) + 숫자(5) 
		 * - 입금반송코드(5자리)   : 
		 *     REPLY(EDI 입금/반송내역)
		 *     DDCRE(DDC 입금/반송내역)
		 *     TRANS(거래내역 추출 DATA)
		 * - 작업일자 (6자리)   : YYMMDD
		 * - ex : MAGNET0001-REPLY.030902
		 */
		String rcvFileName = fileName;
//		try {
//			rcvFileName = CustStringUtils.getRecvFileName(orgCode, recvTypeCode, recvDate );
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		log.info("### Build file name : [" + rcvFileName+ "]");
		sb.append(CustStringUtils.fillSpaceString(rcvFileName, 30, "R", " "));
		
		/**
		 * Space : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(55)으로 빈공간은 ' '로 채워야 됨.
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 55, "L", " "));
		
		
		return sb;
		
	}
	
	public StringBuilder makeFr01(String recvTypeCodeValue, String fileName, String fileNamePrefix) throws IOException {
		
		if(!( ("".equals(recvTypeCodeValue) || "E".equals(recvTypeCodeValue) || 
				"D".equals(recvTypeCodeValue) || "T".equals(recvTypeCodeValue))) ) {
			throw new IOException("Incorrect receive file type! please check receive file type.");
		}
		
//		String orgCode = properties.getProperty("socket.server.orgCode");
//		String orgCode = fileName.substring(0, 10);
		//String orgCode = orgCode;
		log.info("### KisFTUtils().makrFr01(fileName) : orgCode : " + orgCode);
		
//		String recvTypeCode = properties.getProperty("socket.server.recvCode");
//		String recvTypeCodeTemp = fileName.substring(11, 16);
//		log.info("### KisFTUtils().makrFr01(fileName) : recvTypeCodeTemp : " + recvTypeCodeTemp);
		String recvTypeCode = "";
//		String recvDate = fileName.substring(17, 23);
		String recvDate = "";
//		log.info("### KisFTUtils().makrFr01(fileName) : recvDate : " + recvDate);

		recvTypeCode = recvTypeCodeValue;
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR01, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		
		/**
		 * 수신 파일 구분 :         
		 * - E : EDI 입금/반송 내역 파일 (150Byte)
		 * - D : DDC 입금/반송 내역 파일 (150Byte)
		 * - T : 거래내역 파일 (150Byte)
		 */
		sb.append(CustStringUtils.fillSpaceString(recvTypeCode, 1, "L", " ")); 
		
		/**
		 * 수신 파일명 : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(30)으로 빈공간은 ' '로 채워야 됨.
		 * 	수신 파일명 – 입금/반송 내역 파일명(23 자리) 
		 * - 기관코드 (10자리) : 영문(5) + 숫자(5) 
		 * - 입금반송코드(5자리)   : 
		 *     REPLY(EDI 입금/반송내역)
		 *     DDCRE(DDC 입금/반송내역)
		 *     TRANS(거래내역 추출 DATA)
		 * - 작업일자 (6자리)   : YYMMDD
		 * - ex : MAGNET0001-REPLY.030902
		 */
//		sb.append(CustStringUtils.fillSpaceString("MAGNET0001-REPLY.200203", 30, "R", " "));
//		sb.append(CustStringUtils.fillSpaceString("socketServer.log", 30, "R", " "));
		String rcvFileName = "";
		try {
//			rcvFileName = CustStringUtils.getRecvFileName(orgCode, recvTypeCode);
//			rcvFileName = CustStringUtils.getRecvFileName(orgCode, recvTypeCode,recvDate );
//			rcvFileName = CustStringUtils.setRecvFileName(fileNamePrefix,"8" );
			
			if("T".equals(serverMode)) {
				// Test 모드 인 경우에 설정된 파일 받기 
				if("SCOURT0001_TLF".equals(fileNamePrefix)) {
					rcvFileName = CustStringUtils.setRecvFileNameDate(fileNamePrefix,"20200210" );
				} else {
					rcvFileName = CustStringUtils.setRecvFileNameDate(fileNamePrefix,"20200212" );
				}
				
			} else {
				// real 모드인 경우 
				rcvFileName = CustStringUtils.setRecvFileName(fileNamePrefix, "8");
//				rcvFileName = CustStringUtils.setRecvFileName(fileNamePrefix, sendConfigDateType.length()+"");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("### Build file name : [" + rcvFileName+ "]");
		sb.append(CustStringUtils.fillSpaceString(rcvFileName, 30, "R", " "));
		
		/**
		 * Space : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(55)으로 빈공간은 ' '로 채워야 됨.
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 55, "L", " "));
		
		
		return sb;
		
	}
	
	/**
	 * 파일 수신 승인 전문(FR12) : KIS -> 가맹점
	 * 전문 생성 
	 * @return
	 */
	public StringBuilder makeFr12() {
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR12, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		
		/**
		 * 승인 구분 :         
		 * - ‘1’ : 승인 , ‘2’ : 거절
		 */
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.ACCEPT_YES, 1, "L", " "));
		
		/**
		 * 전송할 파일 크기
		 *  char(10)
		 * 	거절시 0으로 채운다 
		 */
		sb.append(CustStringUtils.fillSpaceString("1024000", 10, "L", "0"));
		
		/**
		 * 총 레코드 수
		 *  char(10)
		 */	
		sb.append(CustStringUtils.fillSpaceString("100", 10, "L", "0"));
		
		/**
		 * 거절 코드
		 *  char(4)
		 *  승인시 ‘0000’ 거절시 거절코드(주 3)
		 *  
		 *   ERROR 코드
         *   - 0001 : 수신 요청한 파일이 존재하지 않음
         *   - 0002 : 기타 전문 수신함(오류 전문)
         *   - 0003 : 기타 에러시
		 */	
		//sb.append("0000");
		sb.append(CustStringUtils.fillSpaceString("0", 4, "L", "0"));
		
		/**
		 * Space
		 *  char(41)
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 41, "L", " "));
		
		
		return sb;
		
	}
	
	
	/**
	 * 파일 수신 전문(FR02) : KIS -> 가맹점
	 * FR12 에서 승인시 fr02를 바로 실행.
	 * 수신 데이터 char(1024)
	 * @return
	 */
	public void makeFr02() {
		
	}
	/**
	 * 파일 수신종료 요청 전문 (FR13) : KIS -> 가맹점
	 * FR12 에서 승인시 fr02를 바로 실행.
	 * 수신 데이터 char(1024)
	 * @return
	 */
	public StringBuilder makeFr13() {
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR13, 4, "L", " ")); //head 4
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		/**
		 * Space
		 *  char(86)
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 86, "L", " "));
		return sb;
	}
	
	/**
	 * 파일 수신종료 요청 전문 (FR13) : KIS -> 가맹점
	 * FR12 에서 승인시 fr02를 바로 실행.
	 * 수신 데이터 char(1024)
	 * @return
	 */
	public StringBuilder makeFr03(String fileSize) {
		
		log.info("Fr03 fileSize : " + fileSize);
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR03, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		/**
		 * 총 수신 파일 크기
		 *  char(6)
		 */	
		sb.append(CustStringUtils.fillSpaceString(fileSize, 6, "L", "0"));
		
		/**
		 * Space
		 *  char(80)
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 80, "L", " "));
		log.info("Fr03 return msg : [" + sb.toString() + "]");
		return sb;
	}
	
	
	//로그 출력 함수(msg)
	private void printMessage(String msg){
		ServerLog.getInstance().info(this.getClass().getName(), msg);
	}
	//로그 출력 함수(msg, throwable)
	private void printMessage(String msg, Throwable e){
		ServerLog.getInstance().info(this.getClass().getName(), msg, e);
	}	
	
	
	/**
	 * 송신
	 * 파일 전송 시작 전문(FT01) : 가맹점 -> KIS
	 * 
	 * @param recvTypeCodeValue
	 * @return
	 * @throws IOException
	 * 
	 * 
	 * 
	 * 전송 파일명 – 매출내역 청구 파일명(30자리)(압축 푼 파일)
     *   - 기관코드 (10자리) : 영문(5) + 숫자(5)
     *   - 순번(2자리)
     *   - 매출시작일(4자리) : MMDD
     *   - 매출마감일(4자리) : MMDD
     *   - 작업일자(6자리)    : YYMMDD
     *   - ex : MAGNET0001-01-0819-0824.030825
     *  전송 파일명 – 압축파일 명
     *   - 작업일자 (6자리) : YYMMDD
     *
	 */
	public StringBuilder makeFT01(String sendTypeCodeValue) throws IOException {
		
		if(!( ("".equals(sendTypeCodeValue) || "E".equals(sendTypeCodeValue) || 
				"I".equals(sendTypeCodeValue) || "B".equals(sendTypeCodeValue))) ) {
			throw new IOException("Incorrect receive file type! please check receive file type.");
		}
		
		sendTypeCode = sendTypeCodeValue;
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FT01, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		
		/**
		 * 파일 총 Byte크기
		 * 좌측 ‘0’ 채움(파일을 압축한 상태)
		 */
		sb.append(CustStringUtils.fillSpaceString(sendTypeCode, 10, "L", "0")); 
		
		
		/**
		 * 총 Record 개수
		 * 좌측 ‘0’ 채움(파일을 압축한 상태)
		 */
		sb.append(CustStringUtils.fillSpaceString(sendTypeCode, 10, "L", "0")); 
		
		/**
		 * 데이터 구분
		 * ‘E’ : EDI, ‘I’ : ISP, ‘B’ : BATCH
		 */
		sb.append(CustStringUtils.fillSpaceString(sendTypeCode, 1, "L", "")); 
		/**
		 * 데이터 구분
		 * '1' : Real Data, '0' : Test Data
		 */
		sb.append(CustStringUtils.fillSpaceString(sendTestYn, 1, "L", "")); 
		
		/**
		 * 전송 파일명 : (주 2) 뒤에.Z이 붙지 않은 파일명.
		 *  char(30)으로 빈공간은 ' '로 채워야 됨.
		 *  전송 파일명 – 매출내역 청구 파일명(30자리)(압축 푼 파일)
		 *       - 기관코드 (10자리) : 영문(5) + 숫자(5)
		 *       - 순번(2자리)
		 *       - 매출시작일(4자리) : MMDD
		 *       - 매출마감일(4자리) : MMDD
		 *       - 작업일자(6자리)    : YYMMDD
		 *       - ex : MAGNET0001-01-0819-0824.030825
       	 *	전송 파일명 – 압축파일 명
         *		- 작업일자 (6자리) : YYMMDD
         *
		 */
//		sb.append(CustStringUtils.fillSpaceString("MAGNET0001-REPLY.200203", 30, "R", " "));
//		sb.append(CustStringUtils.fillSpaceString("socketServer.log", 30, "R", " "));
		String sendFileName = "";
		try {
			sendFileName = CustStringUtils.getRecvFileName(orgCode, sendTypeCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("### Build file name : [" + sendFileName+ "]");
		sb.append(CustStringUtils.fillSpaceString(sendFileName, 30, "R", " "));
		
		/**
		 * Space : 
		 *  char(34)으로 빈공간은 ' '로 채워야 됨.
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 34, "L", " "));
		
		
		return sb;
		
	}
		

	/**
	 * 송신 : 특정 파일 지정하여 전송 요청하는 경우
	 * 
	 * 파일 전송 시작 전문(FT01) : 가맹점 -> KIS
	 * 
	 * @param recvTypeCodeValue
	 * @return
	 * @throws IOException
	 * 
	 * 
	 * 
	 * 전송 파일명 – 매출내역 청구 파일명(30자리)(압축 푼 파일)
     *   - 기관코드 (10자리) : 영문(5) + 숫자(5)
     *   - 순번(2자리)
     *   - 매출시작일(4자리) : MMDD
     *   - 매출마감일(4자리) : MMDD
     *   - 작업일자(6자리)    : YYMMDD
     *   - ex : MAGNET0001-01-0819-0824.030825
     *  전송 파일명 – 압축파일 명
     *   - 작업일자 (6자리) : YYMMDD
     *
	 */
	public StringBuilder makeFT01(String sendTypeCodeValue, String fileName) throws IOException {
		if(!( ("".equals(sendTypeCodeValue) || "E".equals(sendTypeCodeValue) || 
				"I".equals(sendTypeCodeValue) || "B".equals(sendTypeCodeValue))) ) {
			throw new IOException("Incorrect receive file type! please check receive file type.");
		}
		
		if("".equals(fileName)) {
			throw new IOException("fileName is not null");
		}
		
		File file;
		
		StringBuilder sb = new StringBuilder();
		
		try {
			file = new File(sendPath + File.separator + fileName);
			
			if(!file.exists()) {
				log.info("Send file is not exist error!!");
			}
			log.info("### KisFTUtils().makrFT01(fileName) : orgCode : " + orgCode);
			
			String sendTypeCode = sendTypeCodeValue;
//			String sendDate = fileName.substring(24, 30);
//			log.info("### KisFTUtils().makrFT01(fileName) : recvDate : " + sendDate);
//			
			
			//sb.append("100");	// 전체 길이
			
			//전문구분코드
			sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FT01, 4, "L", " ")); //head 4
			
			//기관 코드(10자리)
			sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
			
			/**
			 * 파일 총 Byte크기
			 * 좌측 ‘0’ 채움(파일을 압축한 상태)
			 */
			long lfSize = file.length();
			String strFileSize = Long.toString(lfSize);
			sb.append(CustStringUtils.fillSpaceString(strFileSize, 10, "L", "0")); 
			
			
			/**
			 * 총 Record 개수
			 * 좌측 ‘0’ 채움(파일을 압축한 상태)
			 */
			sb.append(CustStringUtils.fillSpaceString("0", 10, "L", "0")); 
			
			/**
			 * 데이터 구분
			 * ‘E’ : EDI, ‘I’ : ISP, ‘B’ : BATCH
			 */
			sb.append(CustStringUtils.fillSpaceString(sendTypeCode, 1, "L", "")); 
			/**
			 * 데이터 구분
			 * '1' : Real Data, '0' : Test Data
			 */
			sb.append(CustStringUtils.fillSpaceString(sendTestYn, 1, "L", "")); 
			
			/**
			 * 전송 파일명 : (주 2) 뒤에.Z이 붙지 않은 파일명.
			 *  char(30)으로 빈공간은 ' '로 채워야 됨.
			 *  전송 파일명 – 매출내역 청구 파일명(30자리)(압축 푼 파일)
			 *       - 기관코드 (10자리) : 영문(5) + 숫자(5)
			 *       - 순번(2자리)
			 *       - 매출시작일(4자리) : MMDD
			 *       - 매출마감일(4자리) : MMDD
			 *       - 작업일자(6자리)    : YYMMDD
			 *       - ex : MAGNET0001-01-0819-0824.030825
	       	 *	전송 파일명 – 압축파일 명
	         *		- 작업일자 (6자리) : YYMMDD
	         *
			 */
//			sb.append(CustStringUtils.fillSpaceString("MAGNET0001-REPLY.200203", 30, "R", " "));
//			sb.append(CustStringUtils.fillSpaceString("socketServer.log", 30, "R", " "));
			String sendFileName = fileName; // 정해진 파일에서 읽어들인 파일 이름. 
			
			log.info("### Build file name : [" + sendFileName+ "]");
			sb.append(CustStringUtils.fillSpaceString(sendFileName, 30, "R", " "));
			
			/**
			 * Space : 
			 *  char(34)으로 빈공간은 ' '로 채워야 됨.
			 */	
			sb.append(CustStringUtils.fillSpaceString("", 34, "L", " "));
			
			
			
		} catch (Exception e) {
			log.error("Send file is not exist error!!");
			e.printStackTrace();
		}
		
		
		return sb;
		
		
	}
		
	
	
	/**
	 * 파일 전송 승인 전문(FT12) : KIS -> 가맹점
	 *
	 * @return
	 * @throws IOException
	 */
	public StringBuilder makeFT12() throws IOException {
		
		StringBuilder sb = new StringBuilder();
			
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FT12, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		
		/**
		 * 승인 구분 :         
		 * - ‘1’ : 승인 , ‘2’ : 거절
		 */
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.ACCEPT_YES, 1, "L", " ")); // 테스트로 무조건 승인
		
		/**
		 * 거절 코드
		 *  승인시 ‘0000’ 거절시 거절코드(주 2)
		 * 	거절코드
         *    - 1001 : 기 전송 파일 송신
         *    - 1002 : 청구시 작업일 세팅 에러
		 */
		sb.append(CustStringUtils.fillSpaceString("0000", 4, "L", "0"));
		
		/**
		 * 거절 메시지
		 */
		sb.append(CustStringUtils.fillSpaceString("", 50, "L", " "));
		
		
		/**
		 * Space : 
		 *  char(31)으로 빈공간은 ' '로 채워야 됨.
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 31, "L", " "));
		
			
		return sb;
	}
	
	/**
	 * 파일 전송 종료 알림 (FT03) : 가맹점 -> KIS
	 *
	 * @return
	 */
	public StringBuilder makeFT03() {
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FT03, 4, "L", " ")); //head 4
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		/**
		 * Space
		 *  char(86)
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 86, "L", " "));
		return sb;
	}
	
	
	
	/**
	 * 파일 전송종료 응답 전문( FT13) : KIS -> 가맹점
	 *
	 * @param fileSize
	 * @return
	 */
	public StringBuilder makeFT13(String fileSize, String errCnt, String errCode) {
		
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FT13, 4, "L", " ")); //head 4
	
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString(orgCode, 10, "L", " "));
		/**
		 * 총 전송한 파일크기
		 *  char(10)
		 */	
		sb.append(CustStringUtils.fillSpaceString(fileSize, 10, "L", "0"));
		
		/**
		 * Error 발생 건수
		 *  char(10)
		 */	
		sb.append(CustStringUtils.fillSpaceString(errCnt, 10, "L", "0"));
		
		/**
		 * Error 코드
		 *  char(4)
		 *  ERROR
	     *   - 1003 : 기타 전문 수신홤 (오류 전문)
	     *   - 1004 : 기타 에러시
	     *   - 1005 : 파일 전송중 통신 에러시
	     *   - 1006 : 다운 받은 파일 Open 에러
	     *   - 1007 : 압출풀기 에러
         *
		 */	
		sb.append(CustStringUtils.fillSpaceString(errCode, 4, "L", " "));
		
		/**
		 * Space
		 *  char(62)
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 62, "L", " "));
		return sb;
	}
		
	
		
}
