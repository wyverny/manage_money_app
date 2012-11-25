package com.lcm.data.parse;

public class KBKookMinCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		KB국민카드 오규환님 11/11 17:12 134,850원(06월) 	11번가(1599-011 누적 1,430,104원
		
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(ParserUtil.getWonIndex(input)+1).split("누적")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
		System.out.println("spend:"+spend+".");
		System.out.println("detail:"+detail+".");
		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
