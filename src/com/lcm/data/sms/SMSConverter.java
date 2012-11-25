package com.lcm.data.sms;

import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.lcm.data.ParsedData;
import com.lcm.data.control.ParsedDataManager;
import com.lcm.data.parse.NotValidSmsDataException;
import com.lcm.data.parse.ParserFactory;
import com.lcm.data.parse.ParserUtil;
import com.lcm.smsSmini.R;

public class SMSConverter {
	private ParsedDataManager pDBm;
	private Context mContext;
	private ParserFactory parserFactory;
	
	public SMSConverter(Context context) {
		mContext = context;
		pDBm.getParsedDataManager(context);
		parserFactory = new ParserFactory(context);
	}
	
	public boolean isValidSms(SmsData smsData) {
		String smsContent = smsData.getBody();
		return isValidSms(smsContent);
	}
	
	public boolean isValidSms(String content) {
		String[] contains = mContext.getResources().getStringArray(R.array.Contains);
		if(ParserUtil.isBankSms(content, contains)!=-1 && !content.contains("√Îº“"))
			return true;
		return false;
	}
	
	public String cutOffBody(String content) {
		String result = "";
		String[] contains = mContext.getResources().getStringArray(R.array.Contains);
		for(String c:contains) {
			if(content.contains(c)) { 
				try {
					ParsedData pd = parserFactory.getParsedData(content,null);
					result = pd.getSpent() + " " + pd.getDetail();
				} catch (NotValidSmsDataException e) {e.printStackTrace();}
				return result;
			}
		}
		return result;
	}
	
	public boolean insertParsedData(SmsData smsData) {
//		ParsedData parsedData = new ParsedData(spent, date, detail, bank, sms_id);
//		pDBm.in
		return false;
	}
	
	/**
	 * convert data that was from sms list
	 * @param b smsData
	 * @return return a data that category should be filled in
	 * @throws NotValidSmsDataException
	 */
	public ParsedData convertSms(SmsData b) throws NotValidSmsDataException {
		String content = b.getBody();
		ParsedData parsedData;
		Date date = new Date(Long.parseLong(b.getDate()));
		parsedData = parserFactory.getParsedData(content,date);
		if(parsedData!=null)
			parsedData.setSms_id(b.getSms_Id());
		return parsedData;
	}
	
	/**
	 * convert data that was from received sms
	 * @param smsMessage received message got by createFromPdu method;
	 * @return return a data that category should be filled in
	 */
	public ParsedData convertSms(SmsMessage smsMessage) throws NotValidSmsDataException {
		ParsedData parsedData;
		String content = smsMessage.getMessageBody();
		Date date = new Date(smsMessage.getTimestampMillis());
		parsedData = parserFactory.getParsedData(content, date);
		return parsedData;
	}
}
