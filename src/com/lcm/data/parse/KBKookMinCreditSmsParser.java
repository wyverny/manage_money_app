package com.lcm.data.parse;

public class KBKookMinCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		KB����ī�� ����ȯ�� 11/11 17:12 134,850��(06��) 	11����(1599-011 ���� 1,430,104��
		
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(ParserUtil.getWonIndex(input)+1).split("����")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
		System.out.println("spend:"+spend+".");
		System.out.println("detail:"+detail+".");
		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
