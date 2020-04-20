package kr.kis.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

public class CustStringUtils {
	/**
	 * 
	 * @param src				:: 값
	 * @param totalLength		:: src 를 포함한 총 문자열 길이
	 * @param alignType			:: 채울문자가 들어갈 위치 L : left , R : right
	 * @param fillString		:: 채울 문자, 
	 *                ex : :" ", "0", "#" 등 
	 * @return
	 */
	 public static String fillSpaceString(String src, int totalLength, String alignType, String fillString) {
		 String strReturn = "";
		 
		 if("".equals(alignType) ) {
			 alignType = "L";
		 }

		 if(!"".equals(src.trim()) && src.length() > 0) {
			 
			 if("L".equals(alignType)) {
				 if("0".equals(fillString)  ) {
					 strReturn = fillZero(totalLength- (src.trim().length())) + src.trim(); 
					 
				 } else {
					 strReturn = fillString(totalLength- (src.trim().length()), fillString) + src.trim(); 
					 
				 }
			 } else {
				 if("0".equals(fillString)  ) {
					 strReturn = src + fillZero(totalLength- (src.trim().length())); 
					 
				 } else {
					 strReturn = src + fillString(totalLength- (src.trim().length()), fillString); 
					 
				 }
			 }
			 
		 } else if("".equals(src.trim()) ){
			 
			 if("0".equals(fillString)  ) {
				 strReturn = fillZero(totalLength);
			 } else {
				 strReturn = fillString(totalLength, fillString);
			 }
		 }
		 
		 return strReturn;
	 }
	 
	 
	 /**
	  * 
	  * @param length
	  * @return
	  */
	 private static String fillZero(int length) {
		 String str = "";
		 
		 for(int i=0; i < length; i++) {
			 str += "0";
		 }
		 
		 return str;
	 }
	 
	 
	 /**
	  * 
	  * @param length
	  * @param fillstr
	  * @return
	  */
	 private static String fillString(int length, String fillstr) {
		 String str = "";
		 
		 for(int i=0; i < length; i++) {
			 str += fillstr;
		 }
		 
		 return str;
	 }
	 
	 
	public static String defaultStringIfBlank(String src, String tgt) {
		if (isBlank(src)) {
			return tgt;
		}
	
			return src;
	}
	
	/**
	 * blank 여부 확인 
	 * @param src
	 * @return
	 */
	public static boolean isBlank(String src) {
		return StringUtils.isEmpty(StringUtils.defaultString(src).trim());
	}
	
	/**
	 * 어제일자 구하기 
	 * 10 : yyyy-mm-dd, 8 : yyyymmdd , 6 : yymmdd
	 * @return
	 * @throws Exception
	 */
	public static String getYesterday(String t) throws Exception {
		Calendar day = Calendar.getInstance();
		day.add(Calendar.DATE, -1);// 어제 일자까지의 해당월 데이터 생성 
		
		String rslt = "";
		
		if("".equals(t))  {
			t = "10";
		}
		
		if("10".equals(t)) {
			rslt = new SimpleDateFormat("yyyy-MM-dd").format(day.getTime());
		} else if("8".equals(t)) {
			rslt = new SimpleDateFormat("yyyyMMdd").format(day.getTime());
		} else if("6".equals(t)) {
			rslt = new SimpleDateFormat("yyMMdd").format(day.getTime());
			
		}
				
		return rslt;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getToday() throws Exception {
		return getToday("yyyy-MM-dd");
	}
	
	public static String getToday(String dateFormat) throws Exception {
		Calendar day = Calendar.getInstance();
		return new SimpleDateFormat(dateFormat).format(day.getTime());
	}	 
	
	/**
	 * 
	 * @param orgCode : 기관코드
	 * @param recvCode : 수신파일 구분코드
	 * @return : 완성된 수신 파일명 
	 * @throws Exception 
	 */
	public static String getRecvFileName(String orgCode, String recvCode) throws Exception {
		String rslt = "";
		
		rslt = orgCode + "-" ;
		
		if(KisFtConstant.RCV_FILE_TYPE_EDI.equals(recvCode)) {
			rslt +=KisFtConstant.IN_RET_CODE_EDI + "."+  getYesterday("6");
		} else 	if(KisFtConstant.RCV_FILE_TYPE_DDC.equals(recvCode)) {
			rslt +=KisFtConstant.IN_RET_CODE_DDC + "."+  getYesterday("6");
		} else 	if(KisFtConstant.RCV_FILE_TYPE_TNS.equals(recvCode)) {
			rslt +=KisFtConstant.IN_RET_CODE_TNS + "."+  getYesterday("6");
		}
		
		
		return rslt;
		
	}
	
	/**
	 * fileNamePrefix+어제일자(yyyymmdd) 로 파일명 생성해서 반환.
	 * 
	 * @param fileNamePrefix
	 * @param dateLength
	 * @return
	 * @throws Exception
	 */
	public static String setRecvFileName(String fileNamePrefix, String dateLength) throws Exception {
		String rslt = "";
		
		rslt = fileNamePrefix ;
		
		rslt += "."+  getYesterday(dateLength);
		
		return rslt;
		
	}
	
	/**
	 * fileNamePrefix + 주어진 일자(setDate)  로 파일명 반환
	 * 
	 * @param fileNamePrefix
	 * @param setDate
	 * @return
	 * @throws Exception
	 */
	public static String setRecvFileNameDate(String fileNamePrefix, String setDate) throws Exception {
		String rslt = "";
		
		rslt = fileNamePrefix ;
		
		rslt += "."+  setDate;
		
		return rslt;
		
	}
	
	public static String getRecvFileName(String orgCode, String recvCode, String recvDate) throws Exception {
		String rslt = "";
		
		rslt = orgCode + "-" ;
		
		if(KisFtConstant.RCV_FILE_TYPE_EDI.equals(recvCode)) {
			rslt +=KisFtConstant.IN_RET_CODE_EDI + "."+  recvDate;
		} else 	if(KisFtConstant.RCV_FILE_TYPE_DDC.equals(recvCode)) {
			rslt +=KisFtConstant.IN_RET_CODE_DDC + "."+  recvDate;
		} else 	if(KisFtConstant.RCV_FILE_TYPE_TNS.equals(recvCode)) {
			rslt +=KisFtConstant.IN_RET_CODE_TNS + "."+  recvDate;
		}
		
		
		return rslt;
		
	}
	

}
