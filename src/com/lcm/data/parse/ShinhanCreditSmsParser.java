package com.lcm.data.parse;

import android.util.Log;

public class ShinhanCreditSmsParser implements SmsParser {

	public String[] parseSms(String input) {
//		����ī�����������â����        11/06 23:24     38,070�� (�Ͻú�) ���ͳݱ�������
//		����ī�����������â����        11/06 20:38     24,230��(�Ͻú�) �ϸ�ϸ�Ʈ
//		����ī�����������â����        11/06 18:09     13,320��(�Ͻú�) �����̽ʻ� �ֽ�
//		����ī�����������â����        11/06 12:18     4,050��(�Ͻú�) �ĸ��ٰԶߴ븢��
		String spend = input.substring(input.indexOf(":"));
		spend = spend.substring(8);
		spend = spend.split("��")[0];
		String detail = input.substring(input.indexOf(")")+1);
		String installment = input.substring(input.indexOf("(")+1,input.indexOf(")"));
		if(installment.contains("�Ͻú�")) installment = "1";
		else {
			installment = installment.split("����")[0];
		}
//		Log.e("ShinhanCheckSmsParser", date.toString()+", "+spend+", "+detail);
//		Log.e("ShinhanCheckSmsParser",spend+" ::: "+detail);
//		for(int i=0;i<input.length();i++)
//			Log.e("ShinhanCheckSmsParser", "charAt["+i+"]: "+input.charAt(i));
		return new String[]{spend,detail,installment};
	}

}
