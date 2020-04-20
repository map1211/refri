package kr.kis.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class KisFtConstant {

	public static String RCV_SUCC 				= "SUCCESS";
	public static String RCV_FAIL 				= "FAIL";
	
	// 전문 코드
	// 수신전문
	public static String CODE_FR01 				= "FR01";  // 파일 수신 요청 전문(FR01) : 가맹점 -> KIS
	public static String CODE_FR12 				= "FR12";  // 파일 수신 승인 전문(FR12) : KIS -> 가맹점
	public static String CODE_FR02 				= "FR02";  // 파일 수신 전문(FR02) : KIS -> 가맹점
	public static String CODE_FR13 				= "FR13";  // 파일 수신종료 요청 전문 (FR13) : KIS -> 가맹점
	public static String CODE_FR03 				= "FR03";  // 파일 수신종료 응답 전문 (FR03) : 가맹점 -> KIS
	
	// 송신전문
	public static String CODE_FT01 				= "FT01";  // 파일 전송 시작 전문(FT01) : 가맹점 -> KIS
	public static String CODE_FT12 				= "FT12";  // 파일 전송 승인 전문(FT12) : KIS -> 가맹점
	public static String CODE_FT03 				= "FT03";  // 파일 전송 종료 알림 (FT03) : 가맹점 -> KIS
	public static String CODE_FT13 				= "FT13";  // 파일 전송종료 응답 전문( FT13) : KIS -> 가맹점

	
	
	// 수신전문에 사용되는 코드 
	// FR01
	// 수신 파일 구분 
	public static String RCV_FILE_TYPE_EDI 		= "E"; // EDI 입금/반송 내역 파일 (150Byte)
	public static String RCV_FILE_TYPE_DDC 		= "D"; // DDC 입금/반송 내역 파일 (150Byte)
	public static String RCV_FILE_TYPE_TNS 		= "T"; // 거래내역 파일 (150Byte)
	
	// 입금반송코드(5자리)    
	public static String IN_RET_CODE_EDI 		= "REPLY"; // (EDI 입금/반송내역)
	public static String IN_RET_CODE_DDC 		= "DDCRE"; // (DDC 입금/반송내역)
	public static String IN_RET_CODE_TNS 		= "TRANS"; // (거래내역 추출 DATA)
	
	
	// FR12 
	public static String ACCEPT_YES 			= "1";  // 승인 구분 ‘1’ : 승인 , ‘2’ : 거절
	public static String ACCEPT_NO  			= "2";  // 승인 구분 ‘1’ : 승인 , ‘2’ : 거절

	// 전문 수신시 사용되는 오류코드
	public static String ERROR_CODE_YES   		= "0000";  // 거절 코드 승인시 ‘0000’ 거절시 거절코드(주 3)
	public static String ERROR_CODE_0001  		= "0001";  // 수신 요청한 파일이 존재하지 않음
	public static String ERROR_CODE_0002  		= "0002";  // 기타 전문 수신함(오류 전문)
	public static String ERROR_CODE_0003  		= "0003";  // 기타 에러시
	
	// 전문 송신시 사용되는 오류코드
	public static String REJECT_CODE_1001  		= "1001";  // 기 전송 파일 송신
	public static String REJECT_CODE_1002  		= "1002";  // 청구시 작업일 세팅 에러
	
	public static String ERROR_CODE_1003  		= "1003";  // 기타 전문 수신홤 (오류 전문)
	public static String ERROR_CODE_1004  		= "1004";  // 기타 에러시
	public static String ERROR_CODE_1005  		= "1005";  // 파일 전송중 통신 에러시
	public static String ERROR_CODE_1006  		= "1006";  // 다운 받은 파일 Open 에러
	public static String ERROR_CODE_1007  		= "1007";  // 압출풀기 에러
	
	
	// 송신전문에 사용되는 코드 
	// FT01
	// 수신 파일 구분 
	public static String SEND_FILE_TYPE_EDI 	= "E"; // EDI 
	public static String SEND_FILE_TYPE_ISP		= "I"; // ISP 
	public static String SEND_FILE_TYPE_BATCH 	= "B"; // BATCH 
	
	// TEST/REAL 구분  
	public static String SEND_FILE_TEST 		= "1"; // (TEST DATA)
	public static String SEND_FILE_REAL 		= "0"; // (REAL DATA)
	
	


	


	
}
