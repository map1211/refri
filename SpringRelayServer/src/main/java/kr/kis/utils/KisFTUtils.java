package kr.kis.utils;

public class KisFTUtils {

	

	/**
	 * 파일 수신 요청 전문(FR01) : 가맹점 -> KIS 
	 * 전문 생성 
	 * @return
	 */
	public static StringBuilder makeFr01() {
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR01, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString("MAGNET0001", 10, "L", " "));

		/**
		 * 수신 파일 구분 :         
		 * - E : EDI 입금/반송 내역 파일 (150Byte)
	     * - D : DDC 입금/반송 내역 파일 (150Byte)
	     * - T : 거래내역 파일 (150Byte)
		 */
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.RCV_FILE_TYPE_EDI, 1, "L", " "));
		
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
		sb.append(CustStringUtils.fillSpaceString("socketServer.log", 30, "R", " "));
		
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
	public static StringBuilder makeFr12() {
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR12, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString("MAGNET0001", 10, "L", " "));
		
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
	public static void makeFr02() {
		
	}
	/**
	 * 파일 수신종료 요청 전문 (FR13) : KIS -> 가맹점
	 * FR12 에서 승인시 fr02를 바로 실행.
	 * 수신 데이터 char(1024)
	 * @return
	 */
	public static StringBuilder makeFr13() {
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR13, 4, "L", " ")); //head 4
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString("MAGNET0001", 10, "L", " "));
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
	public static StringBuilder makeFr03_test() {
		
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("100");	// 전체 길이
		//전문구분코드
		sb.append(CustStringUtils.fillSpaceString(KisFtConstant.CODE_FR03, 4, "L", " ")); //head 4
		
		//기관 코드(10자리)
		sb.append(CustStringUtils.fillSpaceString("MAGNET0001", 10, "L", " "));
		/**
		 * 총 수신 파일 크기
		 *  char(6)
		 */	
		sb.append(CustStringUtils.fillSpaceString("1024000", 10, "L", "0"));
		
		/**
		 * Space
		 *  char(80)
		 */	
		sb.append(CustStringUtils.fillSpaceString("", 80, "L", " "));
		return sb;
	}
	
}
