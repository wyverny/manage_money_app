package com.lcm.data;

import java.util.Calendar;

import com.lcm.smsSmini.R;
import com.lcm.view.NotiInfoRunner;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.DatePicker;
import android.widget.Toast;

public class SettingsPreference extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final int DIALOG_DATEPICKER = 0;
	public static final String PREFERENCES_NAME = "SettingPref";
	public static final String PREF_TOTAL_EXPENSE = "total_expense";
	public static final String PREF_WDAY_BUDGET = "weekday_budget";
	public static final String PREF_WEND_BUDGET = "weekend_budget";
	public static final String PREF_CAL_FROM = "calculate_from";
	public static final String PREF_AUTO_SAVE = "auto_save";
	public static final String PREF_NOTI_INFO = "noti_info_on";
	public static final String PREF_NOTI_ICON = "noti_icon_on";
	
	private Preference mDatePreference;
	private EditTextPreference mExpensePreference;
	private EditTextPreference mCalStartPreference;
	private EditTextPreference mWeekDayPreference;
	private EditTextPreference mWeekEndPreference;
	
	private CheckBoxPreference mNotiInfoPref;
//	private CheckBoxPreference mNotiIconPref;

	PreferenceManager pm = getPreferenceManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getPreferenceManager().setSharedPreferencesName(PREFERENCES_NAME);
		
		addPreferencesFromResource(R.xml.setting);
		mDatePreference = findPreference("date");
		mExpensePreference = (EditTextPreference)findPreference(PREF_TOTAL_EXPENSE);
		mExpensePreference.getEditText().setKeyListener(DigitsKeyListener.getInstance(false,true));
		
		mCalStartPreference = (EditTextPreference)findPreference(PREF_CAL_FROM);
		mCalStartPreference.getEditText().setKeyListener(DigitsKeyListener.getInstance(false,true));
		
		mWeekDayPreference = (EditTextPreference)findPreference(PREF_WDAY_BUDGET);
		mWeekDayPreference.getEditText().setKeyListener(DigitsKeyListener.getInstance(false,true));
		
		mWeekEndPreference = (EditTextPreference)findPreference(PREF_WEND_BUDGET);
		mWeekEndPreference.getEditText().setKeyListener(DigitsKeyListener.getInstance(false,true));
		
		mNotiInfoPref = (CheckBoxPreference)findPreference(PREF_NOTI_INFO);
		mNotiInfoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
//				boolean notiOn = pm.getSharedPreferences().getBoolean(PREF_NOTI_INFO, false);
				SharedPreferences sPref = getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
				boolean notiOn = sPref.getBoolean(SettingsPreference.PREF_NOTI_INFO,false);
				Toast.makeText(SettingsPreference.this, "Noti:" + notiOn, Toast.LENGTH_SHORT).show();
				if(notiOn)
					sendBroadcast(new Intent(NotiInfoRunner.ACTION_RUN_INFORUNNER));
				return false;
			}
		});
//		mNotiIconPref = (CheckBoxPreference)findPreference(PREF_NOTI_ICON);
//		mNotiIconPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				boolean iconOn = pm.getSharedPreferences().getBoolean(PREF_NOTI_ICON, false);
//				SharedPreferences sPref = getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
//				boolean iconOn = sPref.getBoolean(SettingsPreference.PREF_NOTI_ICON,false);
//				Toast.makeText(SettingsPreference.this, "Icon:" + iconOn, Toast.LENGTH_SHORT).show();
//				return false;
//			}
//		});
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(preference==mDatePreference) {
			removeDialog(DIALOG_DATEPICKER);
			showDialog(DIALOG_DATEPICKER);
		} else if(preference.equals((CheckBoxPreference)findPreference("sub_checkbox"))) {
			
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mExpensePreference.setSummary(mExpensePreference.getText());
		mCalStartPreference.setSummary(mCalStartPreference.getText());
		mWeekDayPreference.setSummary(mWeekDayPreference.getText());
    	mWeekEndPreference.setSummary(mWeekEndPreference.getText());
    	
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    if (key.equals(PREF_TOTAL_EXPENSE)) {
	        mExpensePreference.setSummary(sharedPreferences.getString(key, "planned amount of money to spend for a month"));
	    } else if(key.equals(PREF_CAL_FROM)) {
	    	mCalStartPreference.setSummary(sharedPreferences.getString(key, "accumulate expense from"));
	    } else if(key.equals(PREF_WDAY_BUDGET)) {
	    	int[] eachBudget = getEachBudget(Integer.parseInt(sharedPreferences.getString(key, "0")),0);
	    	sharedPreferences.edit().putString(PREF_WEND_BUDGET, ""+eachBudget[1]).commit();
	    	mWeekDayPreference.setSummary(""+eachBudget[0]);
	    	mWeekEndPreference.setSummary(""+eachBudget[1]);
	    } else if(key.equals(PREF_WEND_BUDGET)) {
	    	int[] eachBudget = getEachBudget(0,Integer.parseInt(sharedPreferences.getString(key, "0")));
	    	sharedPreferences.edit().putString(PREF_WDAY_BUDGET, ""+eachBudget[0]).commit();
	    	mWeekDayPreference.setSummary(""+eachBudget[0]);
	    	mWeekEndPreference.setSummary(""+eachBudget[1]);
	    }
	}

	private int[] getEachBudget(int weekDay, int weekEnd) {
		int totalBudget = Integer.parseInt(mExpensePreference.getText());
		if(weekDay==0 && weekEnd==0) {
			return new int[] {totalBudget/30, totalBudget/30};
		}
		if(weekDay==0) {
			return new int[] {(int)(totalBudget - weekEnd*8)/22, weekEnd};
		}
		return new int[] {weekDay, (int)(totalBudget - weekDay*22)/8};
	}
}
