package kr.kis.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {
		
		test3();
	}

	
	private static void test1() throws FileNotFoundException {
		// TODO Auto-generated method stub
				File file = new File("d:\\utils" + File.separator + "test3.text");
				FileOutputStream output = new FileOutputStream("d:\\test3.txt");

				// �ð� ��������
				long lStartTime = System.currentTimeMillis();
				try {
					// �۽��� ������ ũ�� ����. 
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
							
							// 64kbps, ��Ŷ �� �������� delay �߰���.
							// 2020.06.26
							try{    
								Thread.sleep(1000/3);
							} catch (Exception e) {	
								
							}
						}

					} else {			
						System.out.println("444 :: " + lSendFileSize);
						while((len = fis.read(buf)) != -1) {
							// �Ʒ� �ּ��� ############### �ҿ� �ð�: 5.67 ��
							// �Ʒ� �ּ� ������ ############### �ҿ� �ð�: 5.667 ��
							// delay ������ ############### �ҿ� �ð�: 0.001 ��
							if(len == 0) {
								break;
							}
							output.write(buf, 0, len);
							total += len;
							
							// 64kbps, ��Ŷ �� �������� delay �߰���.
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
				System.out.println("############### �ҿ� �ð�: " + lEstTime / 1000.0 + " ��");		
	}
	
	private static void test2() throws FileNotFoundException {
		// TODO Auto-generated method stub
		File file = new File("d:\\utils" + File.separator + "test1.text");
		FileOutputStream output = new FileOutputStream("d:\\test1-1.txt");
		
		// �ð� ��������
		long lStartTime = System.currentTimeMillis();
		try {
			// �۽��� ������ ũ�� ����. 
			long lSendFileSize = file.length(); 
			System.out.println("222222 :: " + lSendFileSize);
			FileInputStream fis = new FileInputStream(file);
			int len;
			byte[] buf = new byte[1024];
			long total = 0L;
			long writeSize = 0;
			
			if( lSendFileSize < Long.parseLong("1024")) {
				System.out.println("333 :: " + lSendFileSize);
				buf = new byte[(int)lSendFileSize];
				while((len = fis.read(buf)) != -1) {
					output.write(buf, 0, len);
					writeSize += len;
					if((writeSize + 1024) > lSendFileSize) {
						break;
					}
				}
				
			} else {			
				System.out.println("444 :: " + lSendFileSize);
				while((len = fis.read(buf)) != -1) {
					output.write(buf, 0, len);
					writeSize += len;
					if((writeSize + 1024) > lSendFileSize) {
						break;
					}
				}
			}
			
			byte[] residualData ;
			if( lSendFileSize < writeSize) {
				residualData = new byte[(int) (writeSize)];
				System.out.println("5555 :: " + writeSize);
				while((len = fis.read(residualData)) != -1) {
					output.write(residualData, 0, len);
					writeSize += len;
					if((writeSize) == lSendFileSize) {
						break;
					}
				}				
			} else {
				residualData = new byte[(int) (lSendFileSize-writeSize)];
			
				System.out.println("6666 :: " + (lSendFileSize-writeSize));
				while((len = fis.read(residualData)) != -1) {
					output.write(residualData, 0, len);
					writeSize += len;
					if((writeSize) == lSendFileSize) {
						break;
					}
				}					
				
			}				
			
			if(lSendFileSize != writeSize)  {
				System.out.println("777 :: ���� ������ Ʋ��" );
			} else {
				System.out.println("888 :: ���� ������ ����" );
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		long lFinishTime = System.currentTimeMillis();
		long lEstTime = (lFinishTime - lStartTime);
		System.out.println("############### �ҿ� �ð�: " + lEstTime / 1000.0 + " ��");		
	}
	
	private static void test3() throws FileNotFoundException {
		// TODO Auto-generated method stub
		File file = new File("d:\\" + File.separator + "test4.txt");
		FileOutputStream output = new FileOutputStream("d:\\test4-1.txt");
		
		// �ð� ��������
		long lStartTime = System.currentTimeMillis();
		try {
			// �۽��� ������ ũ�� ����. 
			long lSendFileSize = file.length(); 
			System.out.println("222 :: " + lSendFileSize);
			FileInputStream fis = new FileInputStream(file);
			int len;
			byte[] buf = new byte[1024];
			long total = 0L;
			long writeSize = 0;
			
			if( lSendFileSize < Long.parseLong("1024")) {
				System.out.println("333 :: " + lSendFileSize);
				buf = new byte[(int)lSendFileSize];
				while((len = fis.read(buf)) != -1) {
					output.write(buf, 0, len);
					writeSize += len;
					if((writeSize + 1024) > lSendFileSize) {
						System.out.println("333 :: break");
						break;
					}
				}
				
			} else {			
				System.out.println("444 :: " + lSendFileSize);
				while((len = fis.read(buf)) != -1) {
					output.write(buf, 0, len);
					writeSize += len;
					if((writeSize + 1024) > lSendFileSize) {
						System.out.println("444 :: break");
						break;
					}
				}
			}
//			
			byte[] data2 = new byte[1024];
			long writeSize2 = lSendFileSize;
			while((len = fis.read(data2)) != -1) {
				System.out.println("555 :: " + len);
				writeSize += len;
				output.write(data2, 0, len);
				writeSize2 -= len;
				if(writeSize2 < 1024) {
					System.out.println("666 :: break");
					data2 = new byte[(int)writeSize2];
				}
			}
						
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		long lFinishTime = System.currentTimeMillis();
		long lEstTime = (lFinishTime - lStartTime);
		System.out.println("############### �ҿ� �ð�: " + lEstTime / 1000.0 + " ��");		
	}
}
