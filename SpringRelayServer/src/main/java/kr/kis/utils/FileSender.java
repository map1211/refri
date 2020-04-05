package kr.kis.utils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSender extends Thread {

	String filePath;
	String fileName;
	Socket socket;
	DataOutputStream dos;
	FileInputStream fis;
	BufferedInputStream bis;
	LogUtil logutil;
	
	public FileSender(Socket socket, String filePath, String fileName) {
		this.socket = socket;
		this.fileName = fileName;
		this.filePath = filePath;
		this.logutil = new LogUtil();
		try {
			// 데이터 전송용 스트림 생성 
			dos = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	
	public void run() {
		try {
//			// 파일 전송을 서버에 알린다.
//			dos.writeUTF("file");
//			dos.flush();
			
			// 전송할 파일을 읽어서 socket server 에 전송 
			String result = fileRead(dos);
			logutil.printMessage("result : " + result);
			
		}  catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			// 리소스 초기화 
			try { 
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try { 
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private String fileRead(DataOutputStream dos) {
		String result; 
		
		try {
//			dos.writeUTF(fileName);
//			logutil.printMessage("fileName : " + fileName + " 을 전송하였습니다.");
			// 파일을 읽어서 서버에 전송
			File file = new File(filePath + File.separator + fileName);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			
			int len;
			int size = 1024;
			
			byte[] data = new byte[size];
			while((len = bis.read(data)) != -1 ) {
				dos.write(data, 0, len); 
			}
			
			// 서버에 전송 
			dos.flush();
			
			result = "SUCCESS";
		} catch (IOException e) {
			e.printStackTrace();
			result = "ERROR";
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return result;
	}
}
