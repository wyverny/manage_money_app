package com.lcm.data.control;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.lcm.data.ParsedData;
import com.lcm.data.ParsedData.InstallmentDatePrice;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

public class ParsedDataManager {
	private static final String TAG = "ParsedDataManager";
	private static final boolean DEBUG = false;
	ExpenditureDBAdaptor exDBAdaptor = null;
	private static Context mContext = null;
	private static ParsedDataManager parsedDataManager = null;
	
	public static ParsedDataManager getParsedDataManager(Context context) {
		if(parsedDataManager==null) {
			mContext = context;
			parsedDataManager = new ParsedDataManager(mContext);
		}
		return parsedDataManager;
	}
	
	private ParsedDataManager(Context mContext) {
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
	}
	
	//Storing data into the database
	public boolean insertParsedData(ParsedData parsedData) {
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
		if(parsedData.getSms_id()==-1) {
			// when the data is inserted manually
			Location loc = parsedData.getLocation();
			double longitude = 0.0;
			double latitude = 0.0;
			if(loc!=null) {
				longitude = loc.getLongitude();
				latitude = loc.getLatitude();
			}
			exDBAdaptor.insertDB(parsedData.getSpent(), parsedData.getCategory(),
					parsedData.getDate(), parsedData.getDetail(),
					longitude, latitude, parsedData.getBank(), parsedData.getUploaded());
		} else {
			// when the data is inserted using SMS
			Location loc = parsedData.getLocation();
			double longitude = 0.0;
			double latitude = 0.0;
			if(loc!=null) {
				longitude = loc.getLongitude();
				latitude = loc.getLatitude();
			}
			exDBAdaptor.insertDB(parsedData.getSpent(), parsedData.getCategory(),
					parsedData.getDate(), parsedData.getDetail(),
					longitude, latitude, 
					parsedData.getBank(),parsedData.getUploaded());
		}
		
		exDBAdaptor.close();
		return false;
	}
	
	public boolean insertParsedData(ArrayList<ParsedData> parsedDatas) {
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
		for(ParsedData parsedData : parsedDatas) {
			if(parsedData.getSms_id()==-1) {
				// when the data is inserted manually
				Location loc = parsedData.getLocation();
				double longitude = 0.0;
				double latitude = 0.0;
				if(loc!=null) {
					longitude = loc.getLongitude();
					latitude = loc.getLatitude();
				}
				if(parsedData.getInstallment()==1) {
					exDBAdaptor.insertDB(parsedData.getSpent(), parsedData.getCategory(),
							parsedData.getDate(), parsedData.getDetail(),
							longitude, latitude, parsedData.getBank(), parsedData.getUploaded());
				} else {
					ArrayList<ParsedData.InstallmentDatePrice> data = parsedData.getInstallmentDatePrice(mContext);
					for(int i=0; i<data.size(); i++) {
						exDBAdaptor.insertDB(data.get(i).getInstallmentPrice(), parsedData.getCategory(),
								data.get(i).getInstallmentDate(), parsedData.getDetail(),
								longitude, latitude, parsedData.getBank(), parsedData.getUploaded());
					}
				}
			} else {
				// when the data is inserted using SMS
				if(parsedData.getInstallment()==1) {
					exDBAdaptor.insertDB(parsedData.getSpent(), parsedData.getCategory(),
							parsedData.getDate(), parsedData.getDetail(),
//							parsedData.getLocation().getLongitude(), parsedData.getLocation().getLatitude(),
							0.0,0.0, parsedData.getBank(), parsedData.getUploaded());
				} else {
					ArrayList<ParsedData.InstallmentDatePrice> data = parsedData.getInstallmentDatePrice(mContext);
					for(int i=0; i<data.size(); i++) {
						Log.e(TAG,"Date Installment: " + data.get(i).getInstallmentDate().get(Calendar.MONTH));
						exDBAdaptor.insertDB(data.get(i).getInstallmentPrice(), parsedData.getCategory(),
								data.get(i).getInstallmentDate(), parsedData.getDetail(),
								0.0,0.0, parsedData.getBank(), parsedData.getUploaded());
					}
				}
				
			}
		}
		
		exDBAdaptor.close();
		if(DEBUG) Log.e(TAG,"inserting parsed data completed");
		return false;
	}
	
	// update existing data
	public boolean updateParsedData(ParsedData parsedData) {
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
		exDBAdaptor.updateDB(parsedData.getDate().get(Calendar.YEAR), parsedData.getDate().get(Calendar.MONTH), 
				parsedData.getDate().get(Calendar.DAY_OF_MONTH), 
				parsedData.getDate().getTimeInMillis(), parsedData.getSpent(), parsedData.getCategory(),
				parsedData.getDetail(), parsedData.getBank(), parsedData.getUploaded());
		
		exDBAdaptor.close();
		return false;
	}
	
	// update existing data
	public boolean updateParsedData(ArrayList<ParsedData> parsedDatas) {
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
		for(ParsedData parsedData:parsedDatas) {
			exDBAdaptor.updateDB(parsedData.getDate().get(Calendar.YEAR), parsedData.getDate().get(Calendar.MONTH), 
					parsedData.getDate().get(Calendar.DAY_OF_MONTH), 
					parsedData.getDate().getTimeInMillis(), parsedData.getSpent(), parsedData.getCategory(),
					parsedData.getDetail(), parsedData.getBank(), parsedData.getUploaded());
		}
		
		exDBAdaptor.close();
		return false;
	}
	
	// delete existing data
	public boolean deleteParsedData(ParsedData parsedData) {
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
		exDBAdaptor.deleteDB(parsedData.getDate());
		
		exDBAdaptor.close();
		return false;
	}
	
	// delete existing data
	public boolean deleteParsedData(ArrayList<ParsedData> parsedDatas) {
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
		for(ParsedData parsedData:parsedDatas)
			exDBAdaptor.deleteDB(parsedData.getDate());
		
		exDBAdaptor.close();
		return false;
	}
	
	// retrieving data
	public Cursor getParsedDataList(Calendar from, Calendar to) {
		Cursor cursor;
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
//		cursor = exDBAdaptor.fetchAllDB();
		cursor = exDBAdaptor.fetchDB(from.getTimeInMillis(), to.getTimeInMillis());
		if(DEBUG) Log.e(TAG,"from: " + from + " to: " + to);
		if(DEBUG) Log.e(TAG,"getParsedDataList: " + cursor.getCount());
		
		exDBAdaptor.close();
		return cursor;
	}
	
	public Cursor getParsedDataList(String category) {
		Cursor cursor;
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
		cursor = exDBAdaptor.fetchDB(category);
		
		exDBAdaptor.close();
		return cursor;
	}
	
	public Cursor getParsedDataList() {
		Cursor cursor;
		exDBAdaptor = new ExpenditureDBAdaptor(mContext);
		exDBAdaptor.open();
		
		cursor = exDBAdaptor.fetchAllDB();
		
		exDBAdaptor.close();
		return cursor;
	}
	
	private ArrayList<ParsedData> getParsedDataFromDatabase(int year, int month) {
		// you can confer from http://zbmon.com/moin.cgi/DateUtil.java
		return getParsedDataFromDatabase(new GregorianCalendar(year, month, 1),
				new GregorianCalendar(year+(int)((month+1)/12),
						(month<12)? month : 1, 1));
	}
	
	private ArrayList<ParsedData> getParsedDataFromDatabase(int fromYear, int fromMonth, int toYear, int toMonth) {
		return getParsedDataFromDatabase(new GregorianCalendar(fromYear, fromMonth, 1),
				new GregorianCalendar(toYear, toMonth, 1));
	}
	
	public ArrayList<ParsedData> getParsedDataFromDatabase(Calendar from, Calendar to) {
		ArrayList<ParsedData> parsedDatas = new ArrayList<ParsedData>();
		Cursor data = parsedDataManager.getParsedDataList(from, to);
		if(data==null || data.getCount()==0) {
			data.close();
			return parsedDatas;
		}
		
		int spentId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_SPEND);
		int categoryId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_CATEGORY);
		int dateId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_TIME);
		int detailId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_DETAIL);
		int locationLongId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_LOCATION_LONG);
		int locationLatiId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_LOCATION_LATI);
		int bankId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_BANK);
		int sms_idId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_SMS_ID);
		
		data.moveToFirst();
		do {
			int spend = data.getInt(spentId);
			String category = data.getString(categoryId);
			Calendar date = new GregorianCalendar();
			date.setTimeInMillis(data.getLong(dateId));
			String detail = data.getString(detailId);
			Location location = new Location("");
			location.setLongitude(data.getDouble(locationLongId));
			location.setLatitude(data.getDouble(locationLatiId));
			String bank = data.getString(bankId);
			int sms_id = data.getInt(sms_idId);
			// TODO: need to check if each data item is null or contains error in it
			ParsedData parsedData = new ParsedData(spend, 1, category, date, detail, location, bank, sms_id);
			if(DEBUG) Log.e(TAG,parsedData.toString());
			parsedDatas.add(parsedData);
		} while(data.moveToNext());
		
		data.close();
		
		return parsedDatas;
	}
	
	public ArrayList<ParsedData> getParsedDataFromDatabase() {
		Cursor data = getParsedDataList();
		if(data==null || data.getCount()==0) {
			data.close();
			return null;
		}
		
		if(DEBUG) Log.e(TAG,"getParsedDataFromDatabase:" + data.getCount());
		ArrayList<ParsedData> parsedDatas = new ArrayList<ParsedData>();
		
		int spentId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_SPEND);
		int categoryId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_CATEGORY);
		int dateId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_TIME);
		int detailId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_DETAIL);
		int locationLongId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_LOCATION_LONG);
		int locationLatiId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_LOCATION_LATI);
		int bankId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_BANK);
		int sms_idId = data.getColumnIndex(ExpenditureDBAdaptor.KEY_SMS_ID);
		
		do {
			int spend = data.getInt(spentId);
			String category = data.getString(categoryId);
			Calendar date = new GregorianCalendar();
			date.setTimeInMillis(data.getLong(dateId));
			String detail = data.getString(detailId);
			Location location = new Location("");
			location.setLongitude(data.getDouble(locationLongId));
			location.setLatitude(data.getDouble(locationLatiId));
			String bank = data.getString(bankId);
			int sms_id = data.getInt(sms_idId);
			// TODO: need to check if each data item is null or contains error in it
			ParsedData parsedData = new ParsedData(spend, 1, category, date, detail, location, bank, sms_id);
			if(DEBUG) Log.e(TAG,parsedData.toString());
			parsedDatas.add(parsedData);
		} while(data.moveToNext());
		data.close();
		
		return parsedDatas;
	}
	
	private ArrayList<Integer> getSpendOfMonth(int year, int month) {
//		DateUtils
		ArrayList<ParsedData> parsedDataOfMonth = getParsedDataFromDatabase(year, month);
		int[] spend = new int[31];
		for(int i=0; i<parsedDataOfMonth.size(); i++) {
			parsedDataOfMonth.get(i).getDate().get(Calendar.DAY_OF_MONTH);
		}
		return null;
	}
}
