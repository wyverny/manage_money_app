package com.lcm.data.parse;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.lcm.data.CategoryDBAdaptor;
import com.lcm.data.ParsedData;
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
		for(int i=0;i<contains.length; i++) {
			if(input.contains(contains[i])) {
				index = i;
			}
		}
//		Log.e("ParserFactory", date.toString());
		switch(index) {
		case 0:
			data = (new ShinhanCheckSmsParser().parseSms(input));
			break;
		case 1:
			data = (new ShinhanCreditSmsParser().parseSms(input));
			break;
		case 2:
			data = (new NewShinhanCreditSmsParser().parseSms(input));
			break;
		default:
			throw (new NotValidSmsDataException());
		}
		// if Date is not null, put that into parsedData so that we can get more precise data
//		Log.e("ShinhanCheckSmsParser", date.toString()+", "+data[0]+", "+data[1]);
		ParsedData parsedData = new ParsedData(Integer.parseInt(data[0].replace(",", "")),
				Integer.parseInt(data[2].trim()),getCategory(data[1]),
				dateCalendar, data[1], contains[index], IS_SMS);
		return parsedData;
	}

	public SmsParser getParser(SmsData smsData) {
		// TODO:
		return null;
	}
	
	private String getCategory(String detail) {
		String cate = "unknown";
		CategoryDBAdaptor cDBAdaptor = new CategoryDBAdaptor(mContext);
		cDBAdaptor.open();
		Cursor data = cDBAdaptor.fetchDB(CategoryDBAdaptor.KEY_DETAIL, detail);
        data.moveToFirst();
        if(data!=null && data.getCount()!=0) {
//			int detailId = data.getColumnIndex(CategoryDBAdaptor.KEY_DETAIL);
			int categoryId = data.getColumnIndex(CategoryDBAdaptor.KEY_CATEGORY);
//			String detail = data.getString(detailId);
			cate = data.getString(categoryId);
//			Log.e(TAG,"CategoryFound: " + detail + " :: " + cate);
		}
        data.close();
		cDBAdaptor.close();
		return cate;
	}
}
