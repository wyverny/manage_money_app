package com.lcm.data.parse;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.lcm.data.ParsedData;
import com.lcm.data.control.CategoryDBAdaptor;
import com.lcm.data.control.ParsedDataManager;
import com.lcm.data.sms.SmsData;
import com.lcm.smsSmini.R;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class ParserFactory {
	public static final String TAG = "ParserFactory";
	public static final int IS_SMS = 1;
	public static final int IS_NOT_SMS = 0;
	
	private Context mContext = null;

	public ParserFactory(Context context) {
		mContext = context;
	}

	public ParsedData getParsedData(String input, Date date) throws NotValidSmsDataException {
		int index = -1;
		// confer to values.phone_number
		String[] contains = mContext.getResources().getStringArray(R.array.Contains);
		String[] data;
		Calendar dateCalendar = new GregorianCalendar();
		dateCalendar.setTime(date);
		
		// TODO:
		// msg.getDisplayMessageBody().replace("\n", " ").replace("\r", " ")
		index = ParserUtil.isBankSms(input, contains);
//		for(int i=0;i<contains.length; i++) {
//			if(input.contains(contains[i])) {
//				index = i;
//			}
//		}
//		Log.e("ParserFactory", date.toString());
		switch(index) {
		case 0: // 신한 체크
			data = (new ShinhanCheckSmsParser().parseSms(input));
			System.out.println("신한 체크");
			break;
		case 1: // 신한 카드
			data = (new NewShinhanCreditSmsParser().parseSms(input));
			System.out.println("신한 카드");
			break;
		case 2: // 현대카드
			data = (new HyunDaeCardSmsParser().parseSms(input));
			System.out.println("현대 카드");
			break;
		case 3: // KB카드
			data = (new KBCreditSmsParser().parseSms(input));
			System.out.println("KB 카드");
			break;
		case 4: // KB국민카드
			data = (new KBKookMinCreditSmsParser().parseSms(input));
			System.out.println("KB 국민카드");
			break;
		case 5: // KB국민체크
			data = (new KBKookMinCheckSmsParser().parseSms(input));
			System.out.println("KB 국민체크");
			break;
		case 6: // 삼성카드
			data = (new SamsungCreditSmsParser().parseSms(input));
			System.out.println("삼성 카드");
			break;
		case 7: // 외환카드
			data = (new KEBCreditSmsParser().parseSms(input));
			System.out.println("외환 카드");
			break;
		case 8: // 씨티카드
			data = (new CityCreditSmsParser().parseSms(input));
			System.out.println("씨티 카드");
			break;
		case 9: // 우리카드
			data = (new WooriCheckSmsParser().parseSms(input));
			System.out.println("우리 카드");
			break;
		case 10: // 기업BC카드
			data = (new IBKBCCreditSmsParser().parseSms(input));
			System.out.println("기업BC 카드");
			break;
		default: 
			System.out.println("못찾았어요");
			throw (new NotValidSmsDataException());
		}
		// if Date is not null, put that into parsedData so that we can get more precise data
//		Log.e("ShinhanCheckSmsParser", date.toString()+", "+data[0]+", "+data[1]);
//		if(data==null || data[0] == null || data[1] == null || data[2] == null)
//			return null;
			
		ParsedDataManager pdm = ParsedDataManager.getParsedDataManager(mContext);
		ParsedData parsedData = null;
		
		try{
			parsedData = new ParsedData(Integer.parseInt(data[0].replace(",", "")),
			Integer.parseInt(data[2].trim()),pdm.getCategory(data[1]),
			dateCalendar, data[1], contains[index], IS_SMS);
		} catch(Exception e) {
			return null;
		}
		return parsedData;
	}

	public SmsParser getParser(SmsData smsData) {
		// TODO:
		return null;
	}
	
//	private String getCategory(String detail) {
//		String cate = "unknown";
//		CategoryDBAdaptor cDBAdaptor = new CategoryDBAdaptor(mContext);
//		cDBAdaptor.open();
//		Cursor data = cDBAdaptor.fetchDB(CategoryDBAdaptor.KEY_DETAIL, detail);
//        data.moveToFirst();
//        if(data!=null && data.getCount()!=0) {
////			int detailId = data.getColumnIndex(CategoryDBAdaptor.KEY_DETAIL);
//			int categoryId = data.getColumnIndex(CategoryDBAdaptor.KEY_CATEGORY);
////			String detail = data.getString(detailId);
//			cate = data.getString(categoryId);
////			Log.e(TAG,"CategoryFound: " + detail + " :: " + cate);
//		}
//        data.close();
//		cDBAdaptor.close();
//		return cate;
//	}
}
