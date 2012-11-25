package com.lcm.data.parse;

//import android.util.Log;

public class ShinhanCreditSmsParser implements SmsParser {

	public String[] parseSms(String input) throws NotValidSmsDataException{
//		신한카드정상승인임창묵님        11/06 23:24     38,070원 (일시불) 인터넷교보문고
//		신한카드정상승인임창묵님        11/06 20:38     24,230원(일시불) 하모니마트
//		신한카드정상승인임창묵님        11/06 18:09     13,320원(일시불) 예스이십사 주식
//		신한카드정상승인임창묵님        11/06 12:18     4,050원(일시불) 파리바게뜨대륭６
//		신한법인카드9014승인 10/27 00:51      100,400원 맥주창고        잔액1,677,600원
//		신한법인카드9014승인 10/26 21:56      387,000원 6가             잔액1,807,800원
//		신한카드승인지세환님 11/13 20:20 5,590원(일시불) 크린토피아 누적1,177,725원
//		[신한카드]       오규환님 11/20결제금액(11/12기준)     20,420원(결제:농협중앙)
		
		String spend;
		String detail;
		String installment;
		
		try {
		spend = input.substring(input.indexOf(":"));
		spend = spend.substring(8);
		spend = spend.split("원")[0].trim();
		detail = input.substring(input.indexOf(")")+1);
		installment = input.substring(input.indexOf("(")+1,input.indexOf(")"));
		if(installment.contains("일시불")) installment = "1";
		else {
			installment = installment.split("개월")[0];
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
