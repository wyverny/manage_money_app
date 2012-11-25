package com.lcm.data.parse;

//import android.util.Log;

public class ShinhanCreditSmsParser implements SmsParser {

	public String[] parseSms(String input) throws NotValidSmsDataException{
//		����ī�����������â����        11/06 23:24     38,070�� (�Ͻú�) ���ͳݱ�������
//		����ī�����������â����        11/06 20:38     24,230��(�Ͻú�) �ϸ�ϸ�Ʈ
//		����ī�����������â����        11/06 18:09     13,320��(�Ͻú�) �����̽ʻ� �ֽ�
//		����ī�����������â����        11/06 12:18     4,050��(�Ͻú�) �ĸ��ٰԶߴ븢��
//		���ѹ���ī��9014���� 10/27 00:51      100,400�� ����â��        �ܾ�1,677,600��
//		���ѹ���ī��9014���� 10/26 21:56      387,000�� 6��             �ܾ�1,807,800��
//		����ī���������ȯ�� 11/13 20:20 5,590��(�Ͻú�) ũ�����Ǿ� ����1,177,725��
//		[����ī��]       ����ȯ�� 11/20�����ݾ�(11/12����)     20,420��(����:�����߾�)
		
		String spend;
		String detail;
		String installment;
		
		try {
		spend = input.substring(input.indexOf(":"));
		spend = spend.substring(8);
		spend = spend.split("��")[0].trim();
		detail = input.substring(input.indexOf(")")+1);
		installment = input.substring(input.indexOf("(")+1,input.indexOf(")"));
		if(installment.contains("�Ͻú�")) installment = "1";
		else {
			installment = installment.split("����")[0];
		}
		} catch(Exception e) {
			throw new NotValidSmsDataException();
		}
		
//		Log.e("ShinhanCheckSmsParser", date.toString()+", "+spend+", "+detail);
//		Log.e("ShinhanCheckSmsParser",spend+" ::: "+detail);
//		for(int i=0;i<input.length();i++)
//			Log.e("ShinhanCheckSmsParser", "charAt["+i+"]: "+input.charAt(i));
		return new String[]{spend,detail,installment};
	}

}
