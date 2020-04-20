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
	
	public static boolean isBlank(String src) {
		return StringUtils.isEmpty(StringUtils.defaultString(src).trim());
	}
	
	public static String getYesterday() throws Exception {
		Calendar day = Calendar.getInstance();
		day.add(Calendar.DATE, -1); // 어제 일자까지의 해당월 데이터 생성
		return new SimpleDateFormat("yyyy-MM-dd").format(day.getTime());
	}
	
	public static String getToday() throws Exception {
		return getToday("yyyy-MM-dd");
	}
	
	public static String getToday(String dateFormat) throws Exception {
		Calendar day = Calendar.getInstance();
		return new SimpleDateFormat(dateFormat).format(day.getTime());
	}	 
}
