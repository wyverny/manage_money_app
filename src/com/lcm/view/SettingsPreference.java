package com.lcm.view;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.lcm.data.control.ExportDatabaseCSVTask;
import com.lcm.smsSmini.R;
import com.lcm.web.WebUpdateListActivity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
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
	public static final String PREF_SHOW_GUIDE_POPUP = "show_guide_popup";
	public static final String PREF_NOTI_ICON = "noti_icon_on";
	public static final String PREF_EXCEL_BACKUP = "excel_backup";
	public static final String PREF_EXCEL_EMAIL = "excel_email";
	public static final String PREF_UPLOAD_MONETA = "upload_moneta";
	public static final String PREF_MONETA_ID = "moneta_id";
	
	private Preference mDatePreference;
	private EditTextPreference mExpensePreference;
	private EditTextPreference mCalStartPreference;
	private EditTextPreference mWeekDayPreference;
	private EditTextPreference mWeekEndPreference;
	
	private CheckBoxPreference mNotiInfoPref;
	private Preference mExcelBackupPref;
	private Preference mExcelEmailPref;
	private Preference mUploadMonetaPref;
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
//				Toast.makeText(SettingsPreference.this, "Noti:" + notiOn, Toast.LENGTH_SHORT).show();
				if(notiOn)
					sendBroadcast(new Intent(NotiInfoRunner.ACTION_RUN_INFORUNNER));	
				else
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
		mExcelBackupPref = (Preference)findPreference(PREF_EXCEL_BACKUP);
		mExcelBackupPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				try {
					new ExportDatabaseCSVTask(SettingsPreference.this).execute("");
				} catch (Exception e) {
					Log.e("Error in Backup DataBase -> CSV",e.toString());
				}
				return false;
			}
			
		});
		mExcelEmailPref = (Preference)findPreference(PREF_EXCEL_EMAIL);
		mExcelEmailPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				try {
					DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd");
					File exportDir = new File(Environment.getExternalStorageDirectory(), "");
					File file = new File(exportDir, "MoneyTracker.csv");
					new ExportDatabaseCSVTask(SettingsPreference.this).execute("");
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("plain/csv");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Money Tracker " + dateFormat.format(new Date()));
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
					startActivity(intent);
				} catch (Exception e) {
					Log.e("Error in Backup DataBase -> CSV",e.toString());
				}
				return false;
			}
		});
		
		mUploadMonetaPref = (Preference)findPreference(PREF_UPLOAD_MONETA);
		mUploadMonetaPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent intent = new Intent(SettingsPreference.this, WebUpdateListActivity.class);
				startActivity(intent);
				return false;
			}
		});
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
    	
//		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		getSharedPreferences(PREFERENCES_NAME, 0).registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		SharedPreferences sPref = getSharedPreferences(PREFERENCES_NAME, 0);
	    if (key.equals(PREF_TOTAL_EXPENSE)) {
	        mExpensePreference.setSummary(sharedPreferences.getString(key, "planned amount of money to spend for a month"));
	        String totalExpString = sharedPreferences.getString(PREF_TOTAL_EXPENSE, "1500000");
	        if(totalExpString.equals("")) {
	        	totalExpString = "1000";
	    		sPref.edit().putString(PREF_TOTAL_EXPENSE, "1500000").commit();
	    		mExpensePreference.setText(totalExpString);
	    	}
	        int totalExpense = Integer.parseInt("0"+totalExpString);
	        SharedPreferences.Editor editor = sPref.edit(); 
	        editor.putString(PREF_WEND_BUDGET, ""+totalExpense/30);
	        mWeekDayPreference.setText(""+totalExpense/30);
	        editor.putString(PREF_WDAY_BUDGET, ""+totalExpense/30);
	        mWeekEndPreference.setText(""+totalExpense/30);
	        editor.commit();
	    } else if(key.equals(PREF_CAL_FROM)) {
	    	String calStart = sharedPreferences.getString(key, "accumulate expense from");
	    	if(calStart.equals("") || Integer.parseInt(calStart) <= 0 || Integer.parseInt(calStart) >= 31) {
	    		calStart = "1";
	    		sPref.edit().putString(PREF_CAL_FROM, "1").commit();
	    		mCalStartPreference.setText(calStart);
	    	}
	    	mCalStartPreference.setSummary(calStart);
	    	
	    } else if(key.equals(PREF_WDAY_BUDGET)) {
	    	String wday = sharedPreferences.getString(key, "0");
	    	if(wday.equals("")) {
	    		wday = "0";
	    		sPref.edit().putString(PREF_WDAY_BUDGET, wday).commit();
	    		mWeekDayPreference.setText(wday);
	    	}
	    	int[] eachBudget = getEachBudget(Integer.parseInt(wday),0);
	    	if(!sharedPreferences.getString(PREF_WEND_BUDGET, "30000").equals(eachBudget[1]+"")) {
	    		mWeekEndPreference.setText(""+eachBudget[1]);
	    		sPref.edit().putString(PREF_WEND_BUDGET, ""+eachBudget[1]).commit();
	    	}
	    	mWeekDayPreference.setSummary(sharedPreferences.getString(PREF_WDAY_BUDGET, "30000"));
	    	mWeekEndPreference.setSummary(sharedPreferences.getString(PREF_WEND_BUDGET, "1050000"));
	    } else if(key.equals(PREF_WEND_BUDGET)) {
	    	String wend = sharedPreferences.getString(key, "0");
	    	if(wend.equals("")) {
	    		wend = "0";
	    		sPref.edit().putString(PREF_WDAY_BUDGET, wend).commit();
	    		mWeekEndPreference.setText(wend);
	    	}
	    	int[] eachBudget = getEachBudget(0,Integer.parseInt(wend));
	    	if(!sharedPreferences.getString(PREF_WDAY_BUDGET, "1050000").equals(eachBudget[0]+"")) {
	    		mWeekDayPreference.setText(""+eachBudget[0]);
	    		sPref.edit().putString(PREF_WDAY_BUDGET, ""+eachBudget[0]).commit();
	    	}
	    	mWeekDayPreference.setSummary(sharedPreferences.getString(PREF_WDAY_BUDGET, "30000"));
	    	mWeekEndPreference.setSummary(sharedPreferences.getString(PREF_WEND_BUDGET, "1050000"));
	    }
	}

	private int[] getEachBudget(int weekDay, int weekEnd) {
		int totalBudget = Integer.parseInt("0"+mExpensePreference.getText());
		if(weekDay==0 && weekEnd==0) {
			return new int[] {totalBudget/30, totalBudget/30};
		}
		if(weekDay==0) {
			return new int[] {(int)(totalBudget - weekEnd*8)/22, weekEnd};
		}
		return new int[] {weekDay, (int)(totalBudget - weekDay*22)/8};
	}
}
