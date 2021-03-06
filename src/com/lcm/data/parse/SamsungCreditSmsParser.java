package com.lcm.data.parse;

public class SamsungCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) {
//		삼성카드승인 \n11/13 00:50\n7,000원\n일시불\nGS25죽전단대점\n*누적\n2,827,070원

		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(input.indexOf("누적")).split("원")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
