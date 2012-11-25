package com.lcm.data.parse;

public class ParserUtil {
	public static String getSpend(String data) {
		int index = data.indexOf("원")-1;
		if(index < 2)
			return null;
		
		while(!Character.isDigit(data.charAt(index-1)) || !Character.isDigit(data.charAt(index-2))) {
			index = data.indexOf("원", index);
		}
			
		int wonIndex = index;
		while(data.charAt(index) == ',' || Character.isDigit(data.charAt(index))) {
			index--;
		}
		String spend = data.substring(index, wonIndex+1).replace(",", "").trim();
		return spend;
	}
	
	public static int getWonIndex(String data) {
		int index = data.indexOf("원");
		if(index < 2)
			return -1;
		
		while(!Character.isDigit(data.charAt(index-1)) || !Character.isDigit(data.charAt(index-2))) {
			index = data.indexOf("원", index+1);
		}
		
		return index;
	}
	
	public static String getInstallment(String data) {
		String installment = "1";
		if(data.contains("개월"))
			installment = installment.split("개월")[0].trim();
		return installment;
	}
	
	public static int isBankSms(String input, String[] contains) {
		int index = -1;
		for(int i=0;i<contains.length; i++) {
			String[] containTokens = contains[i].split(",");
			boolean existAll = true;
			for (String token : containTokens) {
				if(!input.contains(token)) existAll = false;
			}
			if(existAll) {
				index = i;
				break;
			}
		}
		return index;
	}
}
