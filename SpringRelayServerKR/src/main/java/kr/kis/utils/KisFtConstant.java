package kr.kis.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class KisFtConstant {

	public static String RCV_SUCC = "SUCCESS";
	public static String RCV_FAIL = "FAIL";
	
	// ���� �ڵ� 
	public static String CODE_FR01 = "FR01";  // ���� ���� ��û ����(FR01) : ������ -> KIS
	public static String CODE_FR12 = "FR12";  // ���� ���� ���� ����(FR12) : KIS -> ������
	public static String CODE_FR02 = "FR02";  // ���� ���� ����(FR02) : KIS -> ������
	public static String CODE_FR13 = "FR13";  // ���� �������� ��û ���� (FR13) : KIS -> ������
	public static String CODE_FR03 = "FR03";  // ���� �������� ���� ���� (FR03) : ������ -> KIS
	
	// FR01
	// ���� ���� ���� 
	public static String RCV_FILE_TYPE_EDI = "E"; // EDI �Ա�/�ݼ� ���� ���� (150Byte)
	public static String RCV_FILE_TYPE_DDC = "D"; // DDC �Ա�/�ݼ� ���� ���� (150Byte)
	public static String RCV_FILE_TYPE_TNS = "T"; // : �ŷ����� ���� (150Byte)
	
	// �Աݹݼ��ڵ�(5�ڸ�)    
	public static String IN_RET_CODE_EDI = "REPLY"; // (EDI �Ա�/�ݼ۳���)
	public static String IN_RET_CODE_DDC = "DDCRE"; // (DDC �Ա�/�ݼ۳���)
	public static String IN_RET_CODE_TNS = "TRANS"; // (�ŷ����� ���� DATA)
	
	
	// FR12 
	public static String ACCEPT_YES = "1";  // ���� ���� ��1�� : ���� , ��2�� : ����
	public static String ACCEPT_NO  = "2";  // ���� ���� ��1�� : ���� , ��2�� : ����

	public static String ERROR_CODE_YES   = "0000";  // ���� �ڵ� ���ν� ��0000�� ������ �����ڵ�(�� 3)
	public static String ERROR_CODE_0001  = "0001";  // ���� ��û�� ������ �������� ����
	public static String ERROR_CODE_0002  = "0002";  // ��Ÿ ���� ������(���� ����)
	public static String ERROR_CODE_0003  = "0003";  // ��Ÿ ������



	


	
}
