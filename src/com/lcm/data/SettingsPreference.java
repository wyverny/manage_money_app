package com.lcm.data;

import java.util.Calendar;

import com.lcm.smsSmini.R;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.DatePicker;
import android.widget.Toast;

public class SettingsPreference extends PreferenceActivity implements OnDateSetListener {
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
	private CheckBoxPreference mNotiInfoPref;
	private CheckBoxPreference mNotiIconPref;

	PreferenceManager pm = getPreferenceManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getPreferenceManager().setSharedPreferencesName(PREFERENCES_NAME);
		
		addPreferencesFromResource(R.xml.setting);
		mDatePreference = findPreference("date");
		mExpensePreference = (EditTextPreference)findPreference(PREF_TOTAL_EXPENSE);
		mCalStartPreference = (EditTextPreference)findPreference(PREF_CAL_FROM);
		mNotiInfoPref = (CheckBoxPreference)findPreference(PREF_NOTI_INFO);
		mNotiInfoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
//				boolean notiOn = pm.getSharedPreferences().getBoolean(PREF_NOTI_INFO, false);
				SharedPreferences sPref = getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
				boolean notiOn = sPref.getBoolean(SettingsPreference.PREF_NOTI_INFO,false);
				Toast.makeText(SettingsPreference.this, "Noti:" + notiOn, Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		mNotiIconPref = (CheckBoxPreference)findPreference(PREF_NOTI_ICON);
		mNotiIconPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
//				boolean iconOn = pm.getSharedPreferences().getBoolean(PREF_NOTI_ICON, false);
				SharedPreferences sPref = getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
				boolean iconOn = sPref.getBoolean(SettingsPreference.PREF_NOTI_ICON,false);
				Toast.makeText(SettingsPreference.this, "Icon:" + iconOn, Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		mExpensePreference.setSummary("700000");
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
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_DATEPICKER:
			final Calendar calendar = Calendar.getInstance();
			dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
		
	}
}
