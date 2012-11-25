package com.lcm.data.parse;

public class CityCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		씨티카드(5*3*)\n노병석님\n11/12 14:04\n일시불 12,590원\n누계 70,193원\nSK텔레콤자동
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(input.indexOf("누계"));
		detail = detail.substring(detail.indexOf("원")+1).trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
