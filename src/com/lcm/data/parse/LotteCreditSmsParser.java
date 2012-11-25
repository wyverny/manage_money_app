package com.lcm.data.parse;

public class LotteCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		롯데 이정*(2*6*) 11,000원 취소 11/10 15:44 롯데쇼핑(주) 평촌점 누적639,420원   
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(input.indexOf(":")+2).split("누적")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
