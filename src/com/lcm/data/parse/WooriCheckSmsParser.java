package com.lcm.data.parse;

public class WooriCheckSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		[üũ.����]\n30,550��\n�츮ī��(2*1*)��â����\n08/01 13:30\n���ް��ɾ�20,102��\n11
//		[üũ.����]\n9,000��\n�츮ī��(2*7*)����ȭ��\n11/13 20:11\n���ް��ɾ�5,291,847��\n�ֽ�ȸ��
//		[üũ.����]\n9,000��\n�츮ī��(2*7*)����ȭ��\n11/13 19:10\n���ް��ɾ�5,300,847��\nŽ��Ž��
		
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(input.indexOf("���ް��ɾ�")).split("��")[1].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
