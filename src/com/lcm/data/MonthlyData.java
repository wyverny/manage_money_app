package com.lcm.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MonthlyData {
	private static final String TAG = "MonthlyData";
	private static final boolean DEBUG = false;
	private Date from;
//	private GregorianCalendar nextFromGregorianCalendar;
	private Date to;
	private Date turning;
	private int totalDays;
	private int beginingMonth;
	private int[] eachDate;
	private int[] eachExpense;
	private HashMap<Integer, ArrayList<ParsedData>> eachDaysData;
	private int maxExpense;
	private int totalExpense = 0;
	
	private ParsedDataManager parsedDataManager;
	private Context mContext;
	
	public String[] printBriefInfo() {
		String[] days = new String[totalDays];
		for(int i=0; i<totalDays; i++) {
			days[i] = "Day " + beginingMonth + "" + eachDate[i] + " : Expense " + eachExpense[i]; 
		}
		return days;
	}
	
	/**
	 * get initial analysing expense data for given period
	 * @param context
	 * @param from
	 * @param to
	 * @param turning
	 */
	public MonthlyData(Context context, Date from, Date turning, Date to) {
		this.from = from;
		this.to = to;
		this.turning = turning;
		mContext = context;
		SharedPreferences sPref = mContext.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		setTotalExpense(Integer.parseInt(sPref.getString(SettingsPreference.PREF_TOTAL_EXPENSE, "700000")));
		
//		GregorianCalendar fromGregorianCalendar = new GregorianCalendar(from.getYear(), from.getMonth(), from.getDate());
		int fromDays = turning.getDate();//fromGregorianCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

//		nextFromGregorianCalendar =	new GregorianCalendar(from.getYear(), (from.getMonth()<11)? (from.getMonth()+1) : 0, from.getDate());
		
		totalDays = fromDays - from.getDate() + to.getDate();
		/**
		 * so the date starts from the 'From' date, say 25. then it gets the end of that month, the 'Turning' date, say 31.
		 * then it goes to the next month and the date goes from 1 to the 'To' date, 24.
		 * so the eachDate stores date numbers like; 25 26 27 ... 31 1 2 3 ... 23 24. 
		 */
		
		// getting date for each index
		eachDate = new int[totalDays];
		if(DEBUG) Log.e(TAG,"TotalDays: " + totalDays + ", " + fromDays + ", " + from.getDate() + ", "+to.getDate());
		int index=0;
		beginingMonth = from.getMonth()+1;
		for(int j=from.getDate(); j<=fromDays; j++) {
			eachDate[index] = j;
			index++;
		}
		for(int j=1; j<=to.getDate()-1; j++) {
			eachDate[index] = j;
			index++;
		}
//		Log.e(TAG,"from: " + from.getDate() + " maxdays: " + fromDays + " to: " + to.getDate());
		
		// getting expense data for each day 
		eachDaysData = new HashMap<Integer, ArrayList<ParsedData>>(totalDays);
		for(int i=0; i< totalDays; i++) {
			if(DEBUG) Log.e(TAG,"initializing day: " + eachDate[i]);
			eachDaysData.put(eachDate[i], new ArrayList<ParsedData>());
		}
		parsedDataManager = ParsedDataManager.getParsedDataManager(context);
		ArrayList<ParsedData> data = parsedDataManager.getParsedDataFromDatabase(from,to);
		if(data!=null) {
			putParsedData(data);
		}
		
		// calculate each day's total expense 
		eachExpense = new int[totalDays];
		calculateExpense();
		
//		if(data!=null) {
//			monthlyData = new MonthlyData(mContext, from, to);
//			monthlyData.putParsedData(data);
//			expense = monthlyData.accumulateExpense();
//			maxExpense = monthlyData.getMaxExpense();
//		} else {
//			expense = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
//		}
	}
	
	public double getEachDate(int index) {
		return eachDate[index];
	}
	
	public void putParsedData(ArrayList<ParsedData> parsedDatas) {
		for(ParsedData parsedData : parsedDatas) {
			putParsedData(parsedData);
		}
	}
	
	public void putParsedData(ParsedData parsedData) {
		if(parsedData.getDate().compareTo(from)<0||parsedData.getDate().compareTo(to)>0) {
//		if((parsedData.getDate().getYear()+1900)!=year || parsedData.getDate().getMonth()!=month) {
			if(DEBUG) Log.e(TAG,"From: " + from.toString() + ", To: " + to.toString() + ", parsedData: " + parsedData.getDate());
			return;
		}
//		int day = 100*(parsedData.getDate().getMonth()+1)+ parsedData.getDate().getDate();
		int day = parsedData.getDate().getDate();
		if(DEBUG) Log.e(TAG,"Getting day: " + day);
		eachDaysData.get(day).add(parsedData);
	}
	
	/**
	 * calculate each day's expense
	 * @return array contains each day's total expense
	 */
	public int[] calculateExpense() {
		for(int i=0; i<totalDays; i++) {
			eachExpense[i] = 0;
			for(ParsedData parsedData: eachDaysData.get(eachDate[i])) {
				eachExpense[i] += parsedData.getSpent();
			}
			if(DEBUG) Log.e(TAG," " + i + " Day "  + eachDate[i] + " : " + eachExpense[i]);
		}
		
		return eachExpense;
	}
	
	/**
	 * calculate accumulated expense from account starting day
	 * @return accumulated data array
	 */
	public int[] accumulateExpense() {
		int day = 0;
		Date date = new Date();
		if(date.compareTo(from)<0||date.compareTo(to)>0) {
			day = date.getDate();
		}
		int[] accumExpense = new int[eachExpense.length];
		accumExpense[0] = eachExpense[0];
		for(int i=1; i<accumExpense.length; i++) {
			accumExpense[i] = 0;
			if(day!=0 && i <= day) {
				accumExpense[i] = accumExpense[i-1] + eachExpense[i];
				maxExpense = accumExpense[i];
				Log.e(TAG,""+i+"th day's expense: " + accumExpense[i]);
			}
		}
		
//		String result = ""; 
		for(int i=0; i<accumExpense.length; i++) {
			accumExpense[i] = getTotalExpense() - accumExpense[i];
//			result += (totalExpense - accumExpense[i]) + ",";
		}
//		Log.e(TAG,result);
		
		return accumExpense;
	}

	public int getMaxExpense() {
		return maxExpense;
	}

	public void setTotalExpense(int totalExpense) {
		this.totalExpense = totalExpense;
	}

	public int getTotalExpense() {
		return totalExpense;
	}
	
	/**
	 * to know how many days have been passed from starting day
	 * and are remaining until ending day
	 * @param pivot means today
	 * @return returns two sized array; 0 for passed days 1 for remaining days
	 */
	public int[] getPassedRemainingDays(Date pivot) {
		int[] days = {0,0};
		int i=0;
		for(i=0; i<totalDays; i++) {
			if(eachDate[i]==pivot.getDate()) break;
			days[0]++;
		}
		days[1] = totalDays - i;
		return days;
	}
	
	// ************************************************************** //
	//     from this on, all method is to provide analysis data   	  //
	// ************************************************************** //
	
	/**
	 * gives particular day's total expense
	 * calculateExpense method should be run prior to this
	 */
	public int getDatesExpense(int day) {
//		Log.e(TAG,"getDatesExpense:" + day);
		for(int i=0; i<totalDays; i++) {
			if(DEBUG) Log.e(TAG,"Date: " + eachDate[i] + " expense: " + eachExpense[i]);
			if(eachDate[i]==day) return eachExpense[i];
		}
		return 0;
	}
	
	/**
	 * gives particular period's total expense
	 * calculateExpense method should be run prior to this
	 * @param from starting day
	 * @param to finishing day
	 * @return returns double array that includes {total expense, number of days}
	 */
	public int[] getTotalExpenseFromTo(int from, int to) {
		int fIndex = 0, tIndex = 0;
		int total = 0;
		to--;
		Log.e(TAG,"from: " + from + " To: " + to);
		Log.e(TAG,"Each Date:");
		for(int i=0; i<totalDays; i++) {
			// Log.e(TAG,"" + i +"th index: " + eachDate[i]);
			if(eachDate[i]==from) fIndex = i;
			if(eachDate[i]==to) tIndex = i;
		}
			
		if(fIndex > tIndex) return null;
		for(int i=fIndex; i<tIndex; i++) {
			total += eachExpense[i];
		}
		Log.e(TAG,"Total: " + total + " tIndex: " +tIndex + " fIndex: " + fIndex);
		int index = ((tIndex-fIndex)==0)? 1 :(tIndex-fIndex); 
		return new int[]{total, index};
	}
	
	/**
	 * returns total amount of expense for past seven days from 'day' parameter
	 * @param day pivot day
	 * @return returns double array that includes {total expense, number of days}
	 */
	public int[] getWeekExpenseFrom(int day) {
		int dIndex = 0;
		for(int i=0; i<totalDays; i++) {
			if(eachDate[i]==day) {
				dIndex = i; break;
			}
		}
		if(dIndex<7) return getTotalExpenseFromTo(eachDate[0], eachDate[dIndex]);
		else return getTotalExpenseFromTo(eachDate[dIndex-7], eachDate[dIndex]);
	}
	
	public int getTotalDays() {
		return totalDays;
	}

	public int getEachExpense(int day) {
		return eachExpense[day];
	}
	
}
