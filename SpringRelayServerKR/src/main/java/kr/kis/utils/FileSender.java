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
			// ������ ���ۿ� ��Ʈ�� ���� 
			dos = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	
	public void run() {
		try {
//			// ���� ������ ������ �˸���.
			
			// ������ ������ �о socket server �� ���� 
			String result = fileRead(dos);
			logutil.printMessage("result : " + result);
			
		}  catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			// ���ҽ� �ʱ�ȭ 
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
			// ������ �о ������ ����
			File file = new File(filePath + File.separator + fileName);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			
			int len;
			int size = 1024;
			
			byte[] data = new byte[size];
			while((len = bis.read(data)) != -1 ) {
				dos.write(data, 0, len); 
			}
			
			// ������ ����  
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
