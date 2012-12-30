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
import android.view.View.OnFocusChangeListener;
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
		
		int initWeekDay = total/22 - (total/22) % 500; 
		weekDay = (SeekBar)findViewById(R.id.weekday);
		weekDay.setMax(initWeekDay);
		weekDay.setProgress(initWeekDay);
		wdayBudget.setText(""+initWeekDay);
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
		
		int initWeekEnd = total/9 - (total/9) % 500;
		weekEnd = (SeekBar)findViewById(R.id.weekend);
		weekEnd.setMax(initWeekEnd);
		wendBudget.setText("0");
		weekEnd.setProgress(0);
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
	}
	
}
