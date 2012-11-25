package com.lcm.data.parse;

public class KBCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) {
//		[KBī��] �̽��Ѵ� 8*9*ī�� 03��06��01:01 3000�� �Ѹ��̸���Ʈ ��ź ���
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(ParserUtil.getWonIndex(input)+1).split("���")[0].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
		
		return new String[] {spend, detail, installment};
	}

}
