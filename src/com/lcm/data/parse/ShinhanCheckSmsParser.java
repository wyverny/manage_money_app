package com.lcm.data.parse;

public class ShinhanCheckSmsParser implements SmsParser {
	
	// TODO: have to be implemented according to sms contents
	public String[] parseSms(String input) {
//		신한체크승인    01/18 23:05     4,150원         뉴월드마트      잔액   355,041원
//		신한체크승인    05/03 21:04     4,350원         （주）준민유통  잔액   100,779원
//		Date date = new Date();
//		date.setMonth(Integer.parseInt(input.substring(10,12))-1);
//		date.setDate(Integer.parseInt(input.substring(13,15)));
//		date.setHours(Integer.parseInt(input.substring(16,18)));
//		date.setMinutes(Integer.parseInt(input.substring(19,21)));
		String spend = input.substring(26);
		spend = spend.split("원")[0];
		String detail = input.substring(41);
		detail = detail.split("잔액")[0].trim();
		String installment = "1";
//		Log.e("ShinhanCheckSmsParser", date.toString()+", "+spend+", "+detail);
//		Log.e("ShinhanCheckSmsParser", input+",  "+spend+", "+detail);
//		for(int i=0;i<input.length();i++)
//			Log.e("ShinhanCheckSmsParser", "charAt["+i+"]: "+input.charAt(i));
		return new String[]{spend,detail,installment};
	}

}
