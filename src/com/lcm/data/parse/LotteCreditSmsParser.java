package com.lcm.data.parse;

public class LotteCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		�Ե� ����*(2*6*) 11,000�� ��� 11/10 15:44 �Ե�����(��) ������ ����639,420��   
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(input.indexOf(":")+2).split("����")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
