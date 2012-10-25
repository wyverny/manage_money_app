package com.lcm.data.parse;

import android.util.Log;

public class ShinhanCreditSmsParser implements SmsParser {

	public String[] parseSms(String input) {
//		신한카드정상승인임창묵님        11/06 23:24     38,070원 (일시불) 인터넷교보문고
//		신한카드정상승인임창묵님        11/06 20:38     24,230원(일시불) 하모니마트
//		신한카드정상승인임창묵님        11/06 18:09     13,320원(일시불) 예스이십사 주식
//		신한카드정상승인임창묵님        11/06 12:18     4,050원(일시불) 파리바게뜨대륭６
		String spend = input.substring(input.indexOf(":"));
		spend = spend.substring(8);
		spend = spend.split("원")[0];
		String detail = input.substring(input.indexOf(")")+1);
		String installment = input.substring(input.indexOf("(")+1,input.indexOf(")"));
		if(installment.contains("일시불")) installment = "1";
		else {
			installment = installment.split("개월")[0];
		}
//		Log.e("ShinhanCheckSmsParser", date.toString()+", "+spend+", "+detail);
//		Log.e("ShinhanCheckSmsParser",spend+" ::: "+detail);
//		for(int i=0;i<input.length();i++)
//			Log.e("ShinhanCheckSmsParser", "charAt["+i+"]: "+input.charAt(i));
		return new String[]{spend,detail,installment};
	}

}
