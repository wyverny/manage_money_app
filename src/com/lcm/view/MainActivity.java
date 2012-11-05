package com.lcm.view;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;

import com.google.android.apps.iosched.ui.widget.Workspace;
import com.lcm.data.MonthlyData;
import com.lcm.data.MonthlyStat;
import com.lcm.data.SettingsPreference;
import com.lcm.data.sms.InboxListActivity;
import com.lcm.smsSmini.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
//import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int LINE_GRAPH = 1;
	private static final int DOUGHNUT_GRAPH = 2;
	private static final String TAG = "MainActivity";

	private GraphicalView mView;
	private AbstractChart mChart;
	private Workspace mWorkspace;
	private GsCalendar gsCalendar = null;
	
	private int tabHeight = 60;

	// ChartMaker mChartMaker = new ChartMaker(this);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.app_main);
		mWorkspace = (Workspace)findViewById(R.id.workspace);
		mWorkspace.setMainActivity(this);
		
		SharedPreferences sPref = getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		boolean showNoti = sPref.getBoolean(SettingsPreference.PREF_NOTI_INFO,false);
		if(showNoti) {
			Intent notiInfo = new Intent(this, NotiInfo.class);
			startService(notiInfo);
		}

//		TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
//		tabs.setup();
//
//		TabSpec tabSpec = tabs.newTabSpec("tab1");
//		tabSpec.setContent(R.id.analyseTab);
//		tabSpec.setIndicator(makeTabIndicator("Expense"));
//		tabs.addTab(tabSpec);
//
//		tabSpec = tabs.newTabSpec("tab2");
//		tabSpec.setContent(R.id.calendarTab);
//		tabSpec.setIndicator(makeTabIndicator("Calendar"));
//		tabs.addTab(tabSpec);

//		tabSpec = tabs.newTabSpec("tab3");
//		tabSpec.setContent(R.id.pieTab);
//		tabSpec.setIndicator(makeTabIndicator("Category"));
//		tabs.addTab(tabSpec);
	}

	@Override
	protected void onResume() {
		// mView.repaint();
		super.onResume();
		updateInformation();
	}

	private TextView makeTabIndicator(String text) {
		TextView tabView = new TextView(this);
		android.widget.LinearLayout.LayoutParams lp3 = new android.widget.LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, tabHeight, 1);
		lp3.setMargins(1, 0, 1, 0);
		tabView.setLayoutParams(lp3);
		tabView.setText(text);
		tabView.setTextColor(Color.WHITE);
		tabView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		tabView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.tab_indicator));
		tabView.setPadding(13, 0, 13, 0);
		return tabView;
	}

	class PreExistingViewFactory implements TabContentFactory {
		private final View preExisting;

		protected PreExistingViewFactory(View view) {
			preExisting = view;
		}

		public View createTabContent(String tag) {
			return preExisting;
		}
	}

	/**
	 * get monthly data for chosen period and update UI according to that
	 */
	private void updateInformation() {
		DecimalFormat decimalFormat = new DecimalFormat("#,#00");
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// for graph layout
		LinearLayout analyseTab = (LinearLayout) findViewById(R.id.analyseTab);
		analyseTab.removeAllViews();
		LinearLayout analyseLayout = (LinearLayout)inflater.inflate(R.layout.analyse_view,null);
		analyseTab.addView(analyseLayout);
		analyseLayout.getLayoutParams().width = LayoutParams.MATCH_PARENT;
		
		// get this month's date info and Expense data
		Calendar today = new GregorianCalendar();
		// date should be according to accounting date, say every 25th
		SharedPreferences sPref =  getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		int accountingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "15"));
//		int accountingDate = 25; // its value should come from setting
		int month = today.get(Calendar.MONTH);
		if(today.get(Calendar.DATE)<accountingDate) {
			month = (today.get(Calendar.MONTH)==0)? 11 : today.get(Calendar.MONTH)-1;
		}
		// and month should be previous month
		Util util = new Util();
		// Log.e(TAG,"getFromTo: " + (today.getYear()+1900) + "," + month);
		Calendar[] fromTo = util.getFromTo(today.get(Calendar.YEAR), month, accountingDate);
		Log.e(TAG,"Update MonthlyData: from " + fromTo[Util.FROM].getTime() + 
				" throughout " + fromTo[Util.THROUGH].getTime() + " to " + fromTo[Util.TO].getTime());
		int[] expense = null;
		double maxExpense = 0;
		MonthlyData monthlyData = new MonthlyData(this, fromTo[Util.FROM], fromTo[Util.THROUGH], fromTo[Util.TO]);
		expense = monthlyData.accumulateExpense();
		
		MonthlyStat monthlyStat = new MonthlyStat(this, monthlyData);
		
		int[] remainingDays = monthlyData.getPassedRemainingDays(today);
//		Log.e(TAG,"Passed: "+remainingDays[0]+ " remain: " + remainingDays[1]);
		// debugging purpose by here
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.DATE, 1);
//		Date tmr = new Date(gc.getTimeInMillis());
		
		
		int weekday = Integer.parseInt(sPref.getString(SettingsPreference.PREF_WDAY_BUDGET, "30000"));
		int weekend = Integer.parseInt(sPref.getString(SettingsPreference.PREF_WEND_BUDGET, "105000"));
		
		/**
		 * Data calculation for analyze layout
		 */
		
		/**
		 * setting Analysis View from here to below
		 */
		// 1. Budget used progress bar and detailed text
		// and time passed progress bar and detailed text
		ProgressBar budgetUsed = (ProgressBar)analyseLayout.findViewById(R.id.budget_used);
		int totalExpense = monthlyData.getTotalBudget()==0 ? 1:monthlyData.getTotalBudget();
		int budgetUsedPercent = (int)monthlyData.getTotalUsedUp()*100/totalExpense;
		budgetUsed.setProgress(budgetUsedPercent);
		TextView budgetInfo = (TextView)analyseLayout.findViewById(R.id.budget_info);
		budgetInfo.setText("��Ȱ�� ��뷮: " + monthlyData.getTotalUsedUp() + " / " + monthlyData.getTotalBudget());

		ProgressBar timePassed = (ProgressBar)analyseLayout.findViewById(R.id.time_passed);
		int timePassedPercent = (int)remainingDays[0]*100/monthlyData.getTotalDays();
		if(timePassedPercent==0) timePassedPercent=1;
		timePassed.setProgress(timePassedPercent);
		TextView timeInfo = (TextView)analyseLayout.findViewById(R.id.time_info);
		timeInfo.setText("���� �Ⱓ: " + remainingDays[0] + " / " + monthlyData.getTotalDays());
		
		
		// 2. for today's statistics
		TextView today_recommended_use = (TextView)analyseLayout.findViewById(R.id.today_recommended_use);
		TextView today_planned_use = (TextView)analyseLayout.findViewById(R.id.today_planned_used);
				
		today_recommended_use.setText(decimalFormat.format(monthlyStat.recommendedTodayBudget));
		int planned_use = Util.isWeekEnd(today)? weekend : weekday;
		today_planned_use.setText(decimalFormat.format(planned_use));
		
		// 3. for advices
		TextView plan_real_diff = (TextView)analyseLayout.findViewById(R.id.plan_real_diff);
		TextView planed_useup = (TextView)analyseLayout.findViewById(R.id.planned_amount);
		TextView real_useup = (TextView)analyseLayout.findViewById(R.id.real_amount);
		TextView expect_week = (TextView)analyseLayout.findViewById(R.id.expected_from_week);
		TextView velocity_week = (TextView)analyseLayout.findViewById(R.id.velocity_week);
		TextView expect_total = (TextView)analyseLayout.findViewById(R.id.expected_from_total);
		TextView velocity_total = (TextView)analyseLayout.findViewById(R.id.velocity_total);
		
		plan_real_diff.setText(decimalFormat.format(monthlyStat.compareToPlannedAndReal));
		planed_useup.setText("��ȹ ��뷮: "+decimalFormat.format(monthlyStat.plannedUsedUntilToday));
		real_useup.setText("���� ��뷮: "+decimalFormat.format(monthlyData.getTotalUsedUp()));
		expect_week.setText(decimalFormat.format(monthlyStat.expectedUsedFromWeekVelocity));
		velocity_week.setText("������ ��� ��뷮 " + decimalFormat.format(monthlyStat.velocityWeek));
		expect_total.setText(decimalFormat.format(monthlyStat.expectedUsedFromOverallVelocity));
		velocity_total.setText("��ü ��� ��뷮 "+decimalFormat.format(monthlyStat.velocityOverall));		
		
		/**
		 *  for calendar layout
		 */
		LinearLayout calendarTab = ((LinearLayout) findViewById(R.id.calendarTab));
//		calendarTab.removeAllViews();
//		calendarTab.removeView(calendarTab.findViewById(R.layout.calendar));
		View calendarView = inflater.inflate(R.layout.calendar, null);
		calendarView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		LinearLayout calendarLayout = (LinearLayout) calendarView.findViewById(R.id.calendarLayout);
//		TextView calYear = ((TextView)calendarView.findViewById(R.id.calendar_year)); 
//		calYear.setText(""+(1900+today.getYear()));
//		TextView calMonth = ((TextView)calendarView.findViewById(R.id.calendar_month)); 
//		calMonth.setText(""+(today.getMonth()+1));
//		TextView calDate = ((TextView)calendarView.findViewById(R.id.calendar_day)); 
//		calDate.setText(""+accountingDate);
		calendarTab.addView(calendarView);
		if(gsCalendar==null) {
			gsCalendar = new GsCalendar(MainActivity.this,calendarLayout);
			gsCalendar.initCalendar();
			gsCalendar.setControl(new Button[] {
					null, //(Button) calendarView.findViewById(R.id.pre_year_button),
					null, //(Button) calendarView.findViewById(R.id.post_year_button),
					(Button) calendarView.findViewById(R.id.pre_month_button),
					(Button) calendarView.findViewById(R.id.post_month_button) });
//			gsCalendar.setViewTarget(new TextView[] {
//					(TextView) calendarView.findViewById(R.id.calendar_year),
//					(TextView) calendarView.findViewById(R.id.calendar_month),
//					(TextView) calendarView.findViewById(R.id.calendar_day)});
		} else {
			gsCalendar.redraw();
		}
		calendarTab.postInvalidate();

		// for category layout
//		LinearLayout pieLayout = (LinearLayout) findViewById(R.id.pieTab);
//		pieLayout.removeAllViews();
//		mChart = getChart(DOUGHNUT_GRAPH,fromTo[0],fromTo[1],fromTo[2]);
//		mView = new GraphicalView(MainActivity.this, mChart);
//		pieLayout.addView(mView);
//		pieLayout.postInvalidate();

		// for text layout
//		EditText textInfo = (EditText) graphLayout
//				.findViewById(R.id.generalInfoEditText);
//		ProgressBar expenseProgress = (ProgressBar) graphLayout
//				.findViewById(R.id.expenseProgressBar);
	}
	
	private AbstractChart getChart(int whichGraph, Date from, Date to, Date turning) {
		switch (whichGraph) {
		case LINE_GRAPH:
			// return mChartMaker.drawLineChart(from,to,turning);
		case DOUGHNUT_GRAPH:
			// return mChartMaker.drawDoughnutChart(from,to,turning);
		default:
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.options_menu_main_setting:
			Intent intentSettings = new Intent(MainActivity.this,
					SettingsPreference.class);
			startActivity(intentSettings);
			return true;
			
		case R.id.options_menu_main_sms:
			Toast.makeText(this, "SMS�� �ҷ����� ��...", Toast.LENGTH_SHORT).show();
			Intent intentInbox = new Intent(MainActivity.this,
					InboxListActivity.class);
			startActivity(intentInbox);
			return true;
			
		case R.id.options_menu_main_exit:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		gsCalendar = null;
		finish();
	}

	public void setIndicator(int which) {
		LinearLayout indicator = (LinearLayout)findViewById(R.id._indicator);
		ImageView first = (ImageView) indicator.findViewById(R.id.screen1);
		ImageView second = (ImageView) indicator.findViewById(R.id.screen2);
		switch (which) {
		case 1:
			first.setImageDrawable(getResources().getDrawable(R.drawable.selected_no));
			second.setImageDrawable(getResources().getDrawable(R.drawable.selected_yes));
			break;
		case 0:
		default:
			first.setImageDrawable(getResources().getDrawable(R.drawable.selected_yes));
			second.setImageDrawable(getResources().getDrawable(R.drawable.selected_no));
			break;
		}
	}
	
}
