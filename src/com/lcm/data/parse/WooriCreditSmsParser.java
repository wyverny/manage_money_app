package com.lcm.data.parse;

public class WooriCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
		
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(input.indexOf("누적금액")).split("원.")[1].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
