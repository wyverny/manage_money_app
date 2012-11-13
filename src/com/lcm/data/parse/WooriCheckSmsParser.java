package com.lcm.data.parse;

public class WooriCheckSmsParser implements SmsParser {

	@Override
	public String[] parseSms(String input) throws NotValidSmsDataException {
//		[체크.승인]
//		30,550원
//		우리카드(2*1*)임창묵님
//		08/01 13:30
//		지급가능액20,102원
//		11
		
//		[체크.승인]
//		9,000원
//		우리카드(2*7*)강이화님
//		11/13 20:11
//		지급가능액5,291,847원
//		주식회사
		
//		[체크.승인]
//		9,000원
//		우리카드(2*7*)강이화님
//		11/13 19:10
//		지급가능액5,300,847원
//		탐앤탐스
		
		
		return null;
	}

}
