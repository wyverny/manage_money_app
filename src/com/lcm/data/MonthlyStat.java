package com.lcm.data;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lcm.smsSmini.R;
import com.lcm.view.GsCalendar;
import com.lcm.view.MainActivity;
import com.lcm.view.Util;

public class MonthlyStat {
	private static final String TAG = "MonthlyStat";
	private DecimalFormat decimalFormat = new DecimalFormat("#,#00 ¿ø");
	private MonthlyData monthlyData;
	private Context context;
	
	/**
	 * analysis data
	 */
	public int velocityOverall;
	public int velocityWeek;
	
	public int expectedUsedFromOverallVelocity;
	public int expectedUsedFromWeekVelocity;
	
	public int recommendedTodayBudget;
	
	public MonthlyStat(Context context, MonthlyData monthlyData) {
		// TODO: when any number is zero or negative.
		this.monthlyData = monthlyData;
		this.context = context;
		
		SharedPreferences sPref =  context.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		int accountingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "15"));
		
		GregorianCalendar today = new GregorianCalendar();
		GregorianCalendar tomorrow = new GregorianCalendar();
		tomorrow.add(Calendar.DATE, 1);
		
		int[] remainingDays = monthlyData.getPassedRemainingDays(today);
		
		// spending velocity for whole past days
		int[] data1 = monthlyData.getTotalExpenseFromTo(accountingDate, tomorrow.get(Calendar.DATE));
		velocityOverall = data1[0] / data1[1];
		
		// spending velocity for past 7 days
		int[] data2 = monthlyData.getWeekExpenseFrom(tomorrow.get(Calendar.DATE));
		if(data2 != null) {
			velocityWeek = data2[0] / data2[1];
			Log.e(TAG, "velocity2: " + data2[0] + "/" + data2[1] + " = " + velocityWeek);
		} 
		
		// expectedUsedFromOverallVelocity - expected result of accounting date (total)
		// remaining budget - remaining days * velocity = result
		int remainingBudget = monthlyData.getTotalBudget() - data1[0]; 
		expectedUsedFromOverallVelocity = remainingBudget - velocityOverall * remainingDays[1];
		
		// expectedUsedFromWeekVelocity - expected result of accounting date (7 days)
		// remaining budget - remaining days * velocity = result
		expectedUsedFromWeekVelocity = remainingBudget - velocityWeek * remainingDays[1];
		
		// available amount of money user can spend for each remaining day (total)
		// remaining budget / totalbudget = available budget rate (won/days)
		if(remainingBudget <= 0)
			recommendedTodayBudget = 0;
		else {
			int weekdayBudget = sPref.getInt(SettingsPreference.PREF_WDAY_BUDGET, 30000);
			int weekendBudget = sPref.getInt(SettingsPreference.PREF_WEND_BUDGET, 105000);
			
			int ratioWDayNWEnd = weekendBudget / weekdayBudget;
			double unit = 0;
			Calendar dayFromTodayToLast = new GregorianCalendar();
			while(dayFromTodayToLast.get(Calendar.DATE) != accountingDate) {
				if(dayFromTodayToLast.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
						dayFromTodayToLast.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
					unit += 1;
				else
					unit += ratioWDayNWEnd;
				dayFromTodayToLast.add(Calendar.DATE, 1);
			}
			double recommendedDayBudget = remainingBudget / unit;
			recommendedDayBudget *= (today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY 
					|| today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ? ratioWDayNWEnd : 1;
			recommendedTodayBudget = ((int)(remainingBudget/1000))*1000;
		}
		
		
	}
}
