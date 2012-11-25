package com.lcm.data.parse;

public class KBKookMinCheckSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		KB����üũ(5*3*) �����ƴ� 11/13 15:52 	276,200�� �����۵��ܰ��� ���
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(ParserUtil.getWonIndex(input)+1).split("���")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
		System.out.println("spend:"+spend+".");
		System.out.println("detail:"+detail+".");
		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
