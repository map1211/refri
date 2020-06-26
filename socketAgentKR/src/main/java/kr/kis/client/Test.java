package kr.kis.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File file = new File("d:\\utils" + File.separator + "test3.text");
		FileOutputStream output = new FileOutputStream("d:\\test3.txt");

		// 시간 측정위해
		long lStartTime = System.currentTimeMillis();
		try {
			// 송신할 파일의 크기 구함. 
			long lSendFileSize = file.length(); 
			System.out.println("222222 :: " + lSendFileSize);
			FileInputStream fis = new FileInputStream(file);
			int len;
			byte[] buf = new byte[1024];
			long total = 0L;
				
			if( lSendFileSize < Long.parseLong("1024")) {
				System.out.println("333 :: " + lSendFileSize);
				buf = new byte[(int)lSendFileSize];
				while((len = fis.read( buf )) != -1) {
					if(len == 0 ) {
						break;
					}
					System.out.println("333 :: " + len);
					output.write(buf, 0, len);
					total += len;
					
					// 64kbps, 패킷 수 제한으로 delay 추가함.
					// 2020.06.26
					try{    
						Thread.sleep(1000/3);
					} catch (Exception e) {	
						
					}
				}

			} else {			
				System.out.println("444 :: " + lSendFileSize);
				while((len = fis.read(buf)) != -1) {
					// 아래 주석시 ############### 소요 시간: 5.67 초
					// 아래 주석 해제시 ############### 소요 시간: 5.667 초
					// delay 해제시 ############### 소요 시간: 0.001 초
					if(len == 0) {
						break;
					}
					output.write(buf, 0, len);
					total += len;
					
					// 64kbps, 패킷 수 제한으로 delay 추가함.
					// 2020.06.26
					try{    
						Thread.sleep(1000/3);
					} catch (Exception e) {	
						
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		long lFinishTime = System.currentTimeMillis();
		long lEstTime = (lFinishTime - lStartTime);
		System.out.println("############### 소요 시간: " + lEstTime / 1000.0 + " 초");
				
	}

}
