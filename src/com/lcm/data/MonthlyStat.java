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
	DecimalFormat decimalFormat = new DecimalFormat("#,#00 ¿ø");
	MonthlyData monthlyData;
	Context context;
	
	public MonthlyStat(Context context, MonthlyData monthlyData) {
		this.monthlyData = monthlyData;
		this.context = context;
		
		SharedPreferences sPref =  context.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		int accountingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "15"));
		
		
		
	}
}
