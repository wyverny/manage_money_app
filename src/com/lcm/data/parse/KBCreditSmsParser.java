package com.lcm.data.parse;

public class KBCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) {
//		[KB카드] 이승한님 8*9*카드 03월06일01:01 3000원 훼리미리마트 매탄 사용
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(ParserUtil.getWonIndex(input)+1).split("사용")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
		
		return new String[] {spend, detail, installment};
	}

}
