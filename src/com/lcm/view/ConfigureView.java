package com.lcm.view;

import com.lcm.smsSmini.R;

import android.os.Bundle;
import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ConfigureView extends Activity {
	TextView totalExpense;
	SeekBar weekDay;
	SeekBar weekEnd;
	TextView wdayBudget, wendBudget;
	TextView wdayMax, wendMax;
	CheckBox showNoti;
	
	SharedPreferences sPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure);
		
		sPref =  getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		
		totalExpense = (TextView)findViewById(R.id.total_expense);
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
				Toast.makeText(ConfigureView.this, "AfterTextChanged" + totalExpense.getText(), Toast.LENGTH_SHORT).show();
				int total = Integer.parseInt(totalExpense.getText().toString());
				weekDay.setMax(total/22 - (total/22) % 500);
				wdayMax.setText(""+(total/22 - (total/22) % 500));
				weekEnd.setMax(total/9 - (total/9) % 500);
				wendMax.setText(""+(total/9 - (total/9) % 500));
			}
		});
		
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
		wdayMax.setText(""+wdayProgress);
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
		
		Button saveButton = (Button)findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = sPref.edit();
				editor.putString(SettingsPreference.PREF_TOTAL_EXPENSE, totalExpense.getText().toString());
				editor.putString(SettingsPreference.PREF_WDAY_BUDGET, ""+weekDay.getProgress());
				editor.putString(SettingsPreference.PREF_WEND_BUDGET, ""+weekEnd.getProgress());
				editor.putBoolean(SettingsPreference.PREF_NOTI_INFO, showNoti.isChecked());
				editor.commit();
				ConfigureView.this.finish();
			}
		});
	}
}
