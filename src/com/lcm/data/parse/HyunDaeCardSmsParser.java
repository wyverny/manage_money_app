package com.lcm.data.parse;

public class HyunDaeCardSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) {
//		[현대카드M]-승인 김태*님 22,000원(일시불) 소백 누적:498,660원
		
		String spend = ParserUtil.getSpend(input);
//		System.out.println("spend:"+spend+".");
		
		int last = (input.contains("누적"))? input.indexOf("누적") : input.length();
		String detail = input.substring(input.indexOf(")")+1,last).trim();
//		System.out.println("installment:"+detail+".");
		
		String installment = ParserUtil.getInstallment(input);
		System.out.println("installment:"+installment+".");
		
		return new String[]{spend,detail,installment};
	}

}
