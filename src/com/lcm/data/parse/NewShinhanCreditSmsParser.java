package com.lcm.data.parse;

import android.util.Log;

public class NewShinhanCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) {
//		Log.e("NewShinhanCheckSmsParser",input);
		String spend = input.substring(input.indexOf(":")+3);
		spend = spend.split("원")[0].trim();
//		Log.e("NewShinhanCheckSmsParser",spend);
		int last = (input.contains("누적"))? input.indexOf("누적") : input.length();
		String detail = input.substring(input.indexOf(")")+1,last);
		String installment = input.substring(input.indexOf("(")+1,input.indexOf(")"));
		if(installment.contains("일시불")) installment = "1";
		else {
			installment = installment.split("개월")[0].trim();
		}
//		Log.e("ShinhanCheckSmsParser", date.toString()+", "+spend+", "+detail);
//		Log.e("ShinhanCheckSmsParser",spend+" ::: "+detail);
//		for(int i=0;i<input.length();i++)
//			Log.e("ShinhanCheckSmsParser", "charAt["+i+"]: "+input.charAt(i));
		return new String[]{spend,detail,installment};
	}

}
