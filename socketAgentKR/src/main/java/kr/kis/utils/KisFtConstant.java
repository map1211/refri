package kr.kis.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class KisFtConstant {

	public static String RCV_SUCC 				= "SUCCESS";
	public static String RCV_FAIL 				= "FAIL";
	
	// ���� �ڵ�
	// ��������
	public static String CODE_FR01 				= "FR01";  // ���� ���� ��û ����(FR01) : ������ -> KIS
	public static String CODE_FR12 				= "FR12";  // ���� ���� ���� ����(FR12) : KIS -> ������
	public static String CODE_FR02 				= "FR02";  // ���� ���� ����(FR02) : KIS -> ������
	public static String CODE_FR13 				= "FR13";  // ���� �������� ��û ���� (FR13) : KIS -> ������
	public static String CODE_FR03 				= "FR03";  // ���� �������� ���� ���� (FR03) : ������ -> KIS
	
	// �۽�����
	public static String CODE_FT01 				= "FT01";  // ���� ���� ���� ����(FT01) : ������ -> KIS
	public static String CODE_FT12 				= "FT12";  // ���� ���� ���� ����(FT12) : KIS -> ������
	public static String CODE_FT03 				= "FT03";  // ���� ���� ���� �˸� (FT03) : ������ -> KIS
	public static String CODE_FT13 				= "FT13";  // ���� �������� ���� ����( FT13) : KIS -> ������

	
	
	// ���������� ���Ǵ� �ڵ� 
	// FR01
	// ���� ���� ���� 
	public static String RCV_FILE_TYPE_EDI 		= "E"; // EDI �Ա�/�ݼ� ���� ���� (150Byte)
	public static String RCV_FILE_TYPE_DDC 		= "D"; // DDC �Ա�/�ݼ� ���� ���� (150Byte)
	public static String RCV_FILE_TYPE_TNS 		= "T"; // �ŷ����� ���� (150Byte)
	
	// �Աݹݼ��ڵ�(5�ڸ�)    
	public static String IN_RET_CODE_EDI 		= "REPLY"; // (EDI �Ա�/�ݼ۳���)
	public static String IN_RET_CODE_DDC 		= "DDCRE"; // (DDC �Ա�/�ݼ۳���)
	public static String IN_RET_CODE_TNS 		= "TRANS"; // (�ŷ����� ���� DATA)
	
	
	// FR12 
	public static String ACCEPT_YES 			= "1";  // ���� ���� ��1�� : ���� , ��2�� : ����
	public static String ACCEPT_NO  			= "2";  // ���� ���� ��1�� : ���� , ��2�� : ����

	// ���� ���Ž� ���Ǵ� �����ڵ�
	public static String ERROR_CODE_YES   		= "0000";  // ���� �ڵ� ���ν� ��0000�� ������ �����ڵ�(�� 3)
	public static String ERROR_CODE_0001  		= "0001";  // ���� ��û�� ������ �������� ����
	public static String ERROR_CODE_0002  		= "0002";  // ��Ÿ ���� ������(���� ����)
	public static String ERROR_CODE_0003  		= "0003";  // ��Ÿ ������
	
	// ���� �۽Ž� ���Ǵ� �����ڵ�
	public static String REJECT_CODE_1001  		= "1001";  // �� ���� ���� �۽�
	public static String REJECT_CODE_1002  		= "1002";  // û���� �۾��� ���� ����
	
	public static String ERROR_CODE_1003  		= "1003";  // ��Ÿ ���� �����c (���� ����)
	public static String ERROR_CODE_1004  		= "1004";  // ��Ÿ ������
	public static String ERROR_CODE_1005  		= "1005";  // ���� ������ ��� ������
	public static String ERROR_CODE_1006  		= "1006";  // �ٿ� ���� ���� Open ����
	public static String ERROR_CODE_1007  		= "1007";  // ����Ǯ�� ����
	
	
	// �۽������� ���Ǵ� �ڵ� 
	// FT01
	// ���� ���� ���� 
	public static String SEND_FILE_TYPE_EDI 	= "E"; // EDI 
	public static String SEND_FILE_TYPE_ISP		= "I"; // ISP 
	public static String SEND_FILE_TYPE_BATCH 	= "B"; // BATCH 
	
	// TEST/REAL ����  
	public static String SEND_FILE_TEST 		= "1"; // (TEST DATA)
	public static String SEND_FILE_REAL 		= "0"; // (REAL DATA)
	
	


	


	
}
