package com.lcm.data.parse;

public class KEBCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		[외환카드]노병석님    45,180원 (주)이마트       누적액  1,939,380 11/06 23:41
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(ParserUtil.getWonIndex(input)+1).split("누적액")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
