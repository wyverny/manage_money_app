package com.lcm.data.parse;

public class HyunDaeCardSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) {
//		[����ī��M]-���� ����*�� 22,000��(�Ͻú�) �ҹ� ����:498,660��
		
		String spend = ParserUtil.getSpend(input);
//		System.out.println("spend:"+spend+".");
		
		int last = (input.contains("����"))? input.indexOf("����") : input.length();
		String detail = input.substring(input.indexOf(")")+1,last).trim();
//		System.out.println("installment:"+detail+".");
		
		String installment = ParserUtil.getInstallment(input);
		System.out.println("installment:"+installment+".");
		
		return new String[]{spend,detail,installment};
	}

}
