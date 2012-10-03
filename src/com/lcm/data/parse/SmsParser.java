package com.lcm.data.parse;

public interface SmsParser {
	/**
	 * 
	 * @param input
	 * @return array of strings; 0 for spend, 1 for detail, 2 for installment
	 */
	public String[] parseSms(String input);
}
