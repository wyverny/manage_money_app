package com.lcm.data.parse;

public class IBKBCCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		���BC(9*5*)�躸����.11/13 13:14.�Ͻú�31,300��.�����ݾ�2,825,155��.����Ÿ�ڽ�
//		���BC(8*5*)�躸����.11/13 12:22.�Ͻú�.3,000��.6TOP��������.���̴�����ȫ������
		
		String spend = ParserUtil.getSpend(input);
		int index = input.length() - 1;
		while(input.charAt(index)!='.') {
			index--;
		}
		String detail = input.substring(index+1).trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
