package com.lcm.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.lcm.data.control.ParsedDataManager;
import com.lcm.smsSmini.R;
import com.lcm.view.SettingsPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MonthlyData {
	private static final String TAG = "MonthlyData";
	private static final boolean DEBUG = false;
	private Calendar from;
//	private GregorianCalendar nextFromGregorianCalendar;
	private Calendar to;
	private Calendar turning;
	private int totalDays;
	private int beginingMonth;
	private int[] eachDate;
	private int[] eachExpense;
	private HashMap<Integer, ArrayList<ParsedData>> eachDaysData;
	public HashMap<String, Integer> categoryExpense;
	private int[] accumExpense = null;
	private int maxExpense;
	private int totalBudget = 0;
	
	private ParsedDataManager parsedDataManager;
	private Context mContext;
	
	/**
	 * get initial analysing expense data for given period
	 * @param context
	 * @param from
	 * @param to
	 * @param turning
	 */
	public MonthlyData(Context context, Calendar from, Calendar turning, Calendar to) {
		this.from = from;
		this.to = to;
		this.turning = turning;
		mContext = context;
		
		SharedPreferences sPref = mContext.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		setTotalBudget(Integer.parseInt(sPref.getString(SettingsPreference.PREF_TOTAL_EXPENSE, "1000000")));
		
		int fromDays = turning.get(Calendar.DAY_OF_MONTH);

		totalDays = fromDays - from.get(Calendar.DAY_OF_MONTH) + to.get(Calendar.DAY_OF_MONTH);

		/**
		 * so the date starts from the 'From' date, say 25. then it gets the end of that month, the 'Turning' date, say 31.
		 * then it goes to the next month and the date goes from 1 to the 'To' date, 24.
		 * so the eachDate stores date numbers like; 25 26 27 ... 31 1 2 3 ... 23 24. 
		 */
		// getting date for each index
		eachDate = new int[totalDays];
		if(DEBUG) Log.e(TAG,"TotalDays: " + totalDays + ", " + fromDays + ", " +
				from.get(Calendar.DAY_OF_MONTH) + ", "+to.get(Calendar.DAY_OF_MONTH));
		
		int index=0;
		beginingMonth = from.get(Calendar.MONTH)+1;
		for(int j=from.get(Calendar.DAY_OF_MONTH); j<=fromDays; j++) {
			eachDate[index] = j;
			index++;
		}
		for(int j=1; j<=to.get(Calendar.DAY_OF_MONTH)-1; j++) {
			eachDate[index] = j;
			index++;
		}
		
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
		
		// for category information
		categoryExpense = new HashMap<String, Integer>();
		
		// calculate each day's total expense 
		eachExpense = new int[totalDays];
		calculateExpense();
	}
	
	public double getEachDate(int index) {
		return eachDate[index];
	}
	
	public int getDatesIndex(int date) {
		for (int i = 0; i < eachDate.length; i++) {
			if(eachDate[i]==date) return i;
		}
		return 0;
	}
	
	public void putParsedData(ArrayList<ParsedData> parsedDatas) {
		for(ParsedData parsedData : parsedDatas) {
			putParsedData(parsedData);
		}
	}
	
	public void putParsedData(ParsedData parsedData) {
		if(parsedData.getDate().compareTo(from)<0||parsedData.getDate().compareTo(to)>0) {
			if(DEBUG) Log.e(TAG,"From: " + from.toString() + ", To: " + to.toString() + ", parsedData: " + parsedData.getDate());
			return;
		}
		int day = parsedData.getDate().get(Calendar.DAY_OF_MONTH);
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
		if(accumExpense != null)
			return accumExpense;
		
		int day = 0;
		Calendar date = new GregorianCalendar();
//		if(date.compareTo(from)<0||date.compareTo(to)>0) {
//			day = date.get(Calendar.DAY_OF_MONTH);
//		}
		accumExpense = new int[eachExpense.length];
		maxExpense = accumExpense[0] = eachExpense[0];
		for(int i=1; i<accumExpense.length; i++) {
			//accumExpense[i] = 0;
//			if(day!=0 && i <= day) {
			
			accumExpense[i] = accumExpense[i-1] + eachExpense[i];
			if(eachExpense[i] != 0) {
				maxExpense = accumExpense[i];
//				Log.e(TAG,""+i+"th day's expense: " + accumExpense[i]);
//			}
			}
		}
		
//		String result = ""; 
		for(int i=0; i<accumExpense.length; i++) {
			accumExpense[i] = getTotalBudget() - accumExpense[i];
//			result += (totalExpense - accumExpense[i]) + ",";
		}
//		Log.e(TAG,result);
		
		return accumExpense;
	}

	public int getTotalUsedUp() {
		return maxExpense;
	}

	public void setTotalBudget(int totalExpense) {
		this.totalBudget = totalExpense;
	}

	public int getTotalBudget() {
		return totalBudget;
	}
	
	/**
	 * to know how many days have been passed from starting day
	 * and are remaining until ending day
	 * @param pivot means today
	 * @return returns two sized array; 0 for passed days 1 for remaining days
	 */
	public int[] getPassedRemainingDays(Calendar pivot) {
		int[] days = {0,0};
		int i=0;
		for(i=0; i<totalDays; i++) {
			if(eachDate[i]==pivot.get(Calendar.DATE)) break;
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
//			if(DEBUG) Log.e(TAG,"Date: " + eachDate[i] + " expense: " + eachExpense[i]);
			if(eachDate[i]==day) return eachExpense[i];
		}
		return 0;
	}
	
	public HashMap<String, Integer> getCategoryExpense() {
		if(categoryExpense.size()!=0)
			return categoryExpense;
		
		String[] categories = mContext.getResources().getStringArray(R.array.category);
		ArrayList<String> categoriesArrayList = new ArrayList<String>();
		for (String string : categories) {
			categoriesArrayList.add(string);
		}
		int[] spend = new int[categories.length];
		for (ArrayList<ParsedData> eachDay : eachDaysData.values()) {
			for (ParsedData parsedData : eachDay) {
				int index = categoriesArrayList.indexOf(parsedData.getCategory());
				if(index >= 0)
					spend[index] += parsedData.getSpent();
			}
		}
		for (int i = 0; i < spend.length; i++) {
			categoryExpense.put(categories[i], spend[i]);
		}
		
		return categoryExpense;
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
		//to--;
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
	
	public String[] printBriefInfo() {
		String[] days = new String[totalDays];
		for(int i=0; i<totalDays; i++) {
			days[i] = "Day " + beginingMonth + "" + eachDate[i] + " : Expense " + eachExpense[i]; 
		}
		return days;
	}
}
