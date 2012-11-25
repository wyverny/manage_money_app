package com.lcm.data.parse;

public class IBKBCCreditSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		기업BC(9*5*)김보람님.11/13 13:14.일시불31,300원.누적금액2,825,155원.도스타코스
//		기업BC(8*5*)김보람님.11/13 12:22.일시불.3,000원.6TOP적립예정.바이더웨이홍대사랑점
		
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
