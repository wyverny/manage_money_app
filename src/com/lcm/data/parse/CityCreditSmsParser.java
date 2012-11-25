package com.lcm.data.parse;

public class CityCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		��Ƽī��(5*3*)\n�뺴����\n11/12 14:04\n�Ͻú� 12,590��\n���� 70,193��\nSK�ڷ����ڵ�
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(input.indexOf("����"));
		detail = detail.substring(detail.indexOf("��")+1).trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
