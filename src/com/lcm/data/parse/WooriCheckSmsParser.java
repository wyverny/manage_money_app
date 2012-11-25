package com.lcm.data.parse;

public class WooriCheckSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		[체크.승인]\n30,550원\n우리카드(2*1*)임창묵님\n08/01 13:30\n지급가능액20,102원\n11
//		[체크.승인]\n9,000원\n우리카드(2*7*)강이화님\n11/13 20:11\n지급가능액5,291,847원\n주식회사
//		[체크.승인]\n9,000원\n우리카드(2*7*)강이화님\n11/13 19:10\n지급가능액5,300,847원\n탐앤탐스
		
		String spend = ParserUtil.getSpend(input);
		String detail = input.substring(input.indexOf("지급가능액")).split("원")[1].trim();
		String installment = ParserUtil.getInstallment(input);
		
//		System.out.println("spend:"+spend+".");
//		System.out.println("detail:"+detail+".");
//		System.out.println("installment:"+installment+".");
		
		return new String[] {spend, detail, installment};
	}

}
