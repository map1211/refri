package kr.kis.server.parser;

import lombok.extern.slf4j.Slf4j;
import kr.kis.utils.KisFTUtils;
import kr.kis.utils.KisFtConstant;

@Slf4j
public class KisFtParser {

	public  String parseFr01_test(String str) {
		String retStr = "";
		
		KisFTUtils ftUtils = new KisFTUtils();
		
		String head = str.substring(0, 4);
		
		log.info("# KisFtParser :: head : " + head);
		
		if(KisFtConstant.CODE_FR01.equals(head)) { 
			//retStr  = KisFtConstant.ACCEPT_YES; // 승인코드 리턴
			retStr = ftUtils.makeFr12().toString(); 
			
			log.info("# KisFtParser :: return string : [" + retStr + "]");
		}
		
		return retStr;
	}
}
