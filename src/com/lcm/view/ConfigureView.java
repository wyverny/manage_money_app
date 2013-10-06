package com.lcm.view;

import com.lcm.data.sms.InboxListActivity;
import com.lcm.smsSmini.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ConfigureView extends Activity {
	EditText totalExpense;
	EditText accountDay;
	SeekBar weekDay;
	SeekBar weekEnd;
	TextView wdayBudget, wendBudget;
	TextView wdayMax, wendMax;
	CheckBox showNoti;
	
	SharedPreferences sPref;
	boolean showGuidePopup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure);
		this.setTitle("생활비 알림 설정");
		
		sPref =  getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		showGuidePopup = sPref.getBoolean(SettingsPreference.PREF_SHOW_GUIDE_POPUP, true);
		
		totalExpense = (EditText)findViewById(R.id.total_expense);
		totalExpense.setText(sPref.getString(SettingsPreference.PREF_TOTAL_EXPENSE, "1000000"));
		totalExpense.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(totalExpense.getText().toString().equals(""))
					totalExpense.setText("0");
//				Toast.makeText(ConfigureView.this, "AfterTextChanged" + totalExpense.getText(), Toast.LENGTH_SHORT).show();
				int total = Integer.parseInt(totalExpense.getText().toString());
				if(!totalExpense.getText().toString().equals(""+total))
					totalExpense.setText(""+total);
				weekDay.setMax(total/22 - (total/22) % 500);
				wdayMax.setText(""+(total/22 - (total/22) % 500));
				weekEnd.setMax(total/9 - (total/9) % 500);
				wendMax.setText(""+(total/9 - (total/9) % 500));
				int cursorPos = Math.min(totalExpense.length(), 2000);
				totalExpense.setSelection(cursorPos, cursorPos);
			}
		});
		
		accountDay = (EditText)findViewById(R.id.account_day);
		accountDay.setText(sPref.getString(SettingsPreference.PREF_CAL_FROM, "15"));
//		accountDay.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {}
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
//			@Override
//			public void afterTextChanged(Editable s) {
//				if(accountDay.getText().toString().equals(""))
//					accountDay.setText("1");
////				Toast.makeText(ConfigureView.this, "AfterTextChanged" + accountDay.getText(), Toast.LENGTH_SHORT).show();
//				int account = Integer.parseInt(accountDay.getText().toString());
//				if(account > 30)
//					accountDay.setText("30");
//			}
//		});
		
		wdayBudget = (TextView)findViewById(R.id.wday_budget);
		wendBudget = (TextView)findViewById(R.id.wend_budget);
		wdayMax = (TextView)findViewById(R.id.wday_max);
		wendMax = (TextView)findViewById(R.id.wend_max);
		
		String totalString = totalExpense.getText().toString();
		if(totalString.equals("")) totalString = "0";
		int total = Integer.parseInt(totalString);

		String prefWday = sPref.getString(SettingsPreference.PREF_WDAY_BUDGET, "");
		String prefWend = sPref.getString(SettingsPreference.PREF_WEND_BUDGET, "");
		
		int initWeekDay = total/22 - (total/22) % 500; 
		weekDay = (SeekBar)findViewById(R.id.weekday);
		weekDay.setMax(initWeekDay);
		int wdayProgress = (prefWday.equals("")) ? initWeekDay : Integer.parseInt(prefWday);
		weekDay.setProgress(wdayProgress);
		wdayBudget.setText(""+wdayProgress);
		wdayMax.setText(""+initWeekDay);
		weekDay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int prog;
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				if(prog % 500 != 0)
					prog = prog - prog % 500;
				weekDay.setProgress(prog);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				prog = progress;
				
				int total = Integer.parseInt(totalExpense.getText().toString());
				int wend = (total - 22 * prog) / 9;
				wend = wend - (wend % 500);
				weekEnd.setProgress(wend);
				
				wdayBudget.setText(""+prog);
				wendBudget.setText(""+wend);
			}
		});
		ImageButton wdayUp = (ImageButton)findViewById(R.id.wday_up);
		wdayUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int wday = weekDay.getProgress();
				if(wday+500 <= weekDay.getMax())
					weekDay.setProgress(wday+500);
			}
		});
		ImageButton wdayDown = (ImageButton)findViewById(R.id.wday_down);
		wdayDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int wday = weekDay.getProgress();
				if(wday-500 >= 0)
					weekDay.setProgress(wday-500);
			}
		});
		
		int initWeekEnd = total/9 - (total/9) % 500;
		weekEnd = (SeekBar)findViewById(R.id.weekend);
		weekEnd.setMax(initWeekEnd);
		int wendProgress = (prefWend.equals("")) ? 0 : Integer.parseInt(prefWend);
		wendBudget.setText(""+wendProgress);
		weekEnd.setProgress(wendProgress);
		wendMax.setText(""+initWeekEnd);
		weekEnd.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int prog;
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				if(prog % 500 != 0)
					prog = prog - prog % 500;
				weekEnd.setProgress(prog);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				prog = progress;
				
				int total = Integer.parseInt(totalExpense.getText().toString());
				int wday = (total - 9 * prog) / 22;
				wday = wday - (wday % 500);
				weekDay.setProgress(wday);
				
				wdayBudget.setText(""+wday);
				wendBudget.setText(""+prog);
			}
		});
		
		ImageButton wendUp = (ImageButton)findViewById(R.id.wend_up);
		wendUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int wend = weekEnd.getProgress();
				if(wend+500 <= weekEnd.getMax())
					weekEnd.setProgress(wend+500);
			}
		});
		ImageButton wendDown = (ImageButton)findViewById(R.id.wend_down);
		wendDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int wend = weekEnd.getProgress();
				if(wend-500 >= 0)
					weekEnd.setProgress(wend-500);
			}
		});
		
		showNoti = (CheckBox)findViewById(R.id.show_noti);
		showNoti.setChecked(sPref.getBoolean(SettingsPreference.PREF_NOTI_INFO, false));
		showNoti.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				sPref.edit().putBoolean(SettingsPreference.PREF_NOTI_INFO, showNoti.isChecked()).commit();
				sendBroadcast(new Intent(NotiInfoRunner.ACTION_RUN_INFORUNNER));
			}
		});
		
		Button saveButton = (Button)findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				SharedPreferences.Editor editor = sPref.edit();
				
				if(accountDay.getText().toString().equals(""))
					accountDay.setText("1");
				int account = Integer.parseInt(accountDay.getText().toString());
				if(account > 30)
					accountDay.setText("30");
				
				editor.putString(SettingsPreference.PREF_TOTAL_EXPENSE, totalExpense.getText().toString());
				editor.putString(SettingsPreference.PREF_CAL_FROM, accountDay.getText().toString());
				editor.putString(SettingsPreference.PREF_WDAY_BUDGET, ""+weekDay.getProgress());
				editor.putString(SettingsPreference.PREF_WEND_BUDGET, ""+weekEnd.getProgress());
				editor.putBoolean(SettingsPreference.PREF_SHOW_GUIDE_POPUP, false);
				editor.commit();
				
				if(showGuidePopup) {
					new AlertDialog.Builder(ConfigureView.this)
					.setMessage("기존 문자데이터를 등록하시겠어요??")
					.setPositiveButton("네", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intentSettings = new Intent(ConfigureView.this,
									InboxListActivity.class);
							intentSettings.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intentSettings);
							ConfigureView.this.finish();
						}
					})
					.setNegativeButton("나중에", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent intentSettings = new Intent(ConfigureView.this,
									MainActivity.class);
							intentSettings.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intentSettings);
							ConfigureView.this.finish();
						}
					})
					.show();
				} else {
					Intent intentSettings = new Intent(ConfigureView.this,
							MainActivity.class);
					startActivity(intentSettings);
					ConfigureView.this.finish();
				}
			}
		});
		
		int cursorPos = Math.min(totalExpense.length(), 2000);
		totalExpense.setSelection(cursorPos, cursorPos);
	}
}
