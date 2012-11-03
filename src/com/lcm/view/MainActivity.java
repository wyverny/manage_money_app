package com.lcm.view;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;

import com.google.android.apps.iosched.ui.widget.Workspace;
import com.lcm.data.MonthlyData;
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
		DecimalFormat decimalFormat = new DecimalFormat("#,#00 원");
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// for graph layout
		LinearLayout analyseTab = (LinearLayout) findViewById(R.id.analyseTab);
		analyseTab.removeAllViews();
		LinearLayout analyseLayout = (LinearLayout)inflater.inflate(R.layout.new_analyse_view,null);
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
		
		int[] remainingDays = monthlyData.getPassedRemainingDays(today);
//		Log.e(TAG,"Passed: "+remainingDays[0]+ " remain: " + remainingDays[1]);
		// debugging purpose by here
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.DATE, 1);
//		Date tmr = new Date(gc.getTimeInMillis());
		
		/**
		 * Data calculation for analyze layout
		 */
		// TODO: implement analyse view
		// 1. velocity of spending for total passed days
		// total spend / passed days = result (won/days)
		int[] data1 = monthlyData.getTotalExpenseFromTo(accountingDate, gc.get(Calendar.DATE));
		int velocity1 = data1[0] / data1[1];
		Log.e(TAG, "velocity1: " + data1[0] + "/" + data1[1] + " = " + velocity1);
		
		// 2. velocity of spending for last 7 days
		// total spend / 7 days = result (won/days)
		int[] data2 = monthlyData.getWeekExpenseFrom(gc.get(Calendar.DATE));
		int velocity2 = 0;
		if(data2 != null) {
			velocity2 = data2[0] / data2[1];
			Log.e(TAG, "velocity2: " + data2[0] + "/" + data2[1] + " = " + velocity2);
		}
		
		/**
		 * setting Analysis View from here to below
		 */
		// 1. Budget used progress bar and detailed text
		ProgressBar budgetUsed = (ProgressBar)analyseLayout.findViewById(R.id.budget_used);
		int totalExpense = monthlyData.getTotalBudget()==0 ? 1:monthlyData.getTotalBudget();
		int budgetUsedPercent = (int)data1[0]*100/totalExpense;
		budgetUsed.setProgress(budgetUsedPercent);
		TextView budgetInfo = (TextView)analyseLayout.findViewById(R.id.budget_info);
		budgetInfo.setText("생활비 사용량: " + data1[0] + " / " + monthlyData.getTotalBudget());
//		budgetInfo.setText(data1[0]+"("+(data1[0]*100)/monthlyData.getTotalExpense() + "%) 사용 / 전체 일정 중" +
//				(int)(remainingDays[0]*100/(remainingDays[0]+remainingDays[1])) + "% 지남");
		
		// 2. time passed progress bar and detailed text
		ProgressBar timePassed = (ProgressBar)analyseLayout.findViewById(R.id.time_passed);
		int timePassedPercent = (int)remainingDays[0]*100/monthlyData.getTotalDays();
		if(timePassedPercent==0) timePassedPercent=1;
		timePassed.setProgress(timePassedPercent);
		TextView timeInfo = (TextView)analyseLayout.findViewById(R.id.time_info);
		timeInfo.setText("지난 기간: " + remainingDays[0] + " / " + monthlyData.getTotalDays());
		
		// 3. expected expenses till current date
		TextView expectedPercent = (TextView)analyseLayout.findViewById(R.id.expected_percent);
		Log.e(TAG,"getTotalDays: "+monthlyData.getTotalDays()+ " remainingDays[0]: "+ remainingDays[0]);
		Log.e(TAG,"budgetUsedPercent: "+budgetUsedPercent+ " timePassedPercent: "+ timePassedPercent);
		expectedPercent.setText(""+(budgetUsedPercent*100/timePassedPercent));
		TextView expectedDetail = (TextView)analyseLayout.findViewById(R.id.expected_detail);
		int remainedBudget = monthlyData.getTotalBudget() - data1[0];
		int trendBudget = (monthlyData.getTotalBudget()/monthlyData.getTotalDays()*remainingDays[0]) - data1[0];
		String remainedDetail = "계획량 대비 ";
		remainedDetail = (trendBudget>0)? 
				remainedDetail+decimalFormat.format(trendBudget)+" 덜 사용" :
					remainedDetail+(decimalFormat.format(-1*trendBudget))+" 더 사용";
		remainedDetail += " (예상: " + decimalFormat.format(trendBudget +data1[0])+")";
		expectedDetail.setText(remainedDetail);
		
		// 6. guidance; expected result of accounting date (7 days)
				// remaining budget - remaining days * velocity = result
		int expectation2 = remainedBudget - velocity2 * remainingDays[1];
		int expectPercent2 = (int)((double)(data1[0] + velocity2 * remainingDays[1]) / (double)monthlyData.getTotalBudget() * 100.0);
		TextView trendPercent = (TextView)analyseLayout.findViewById(R.id.trend_percent);
		trendPercent.setText(""+expectPercent2);
		TextView trendDetail = (TextView)analyseLayout.findViewById(R.id.trend_detail);
		trendDetail.setText("현 추세로 사용 시 말일에 " + decimalFormat.format(expectation2) + " 남음");
		
//		TextView totalVelo = (TextView)analyseLayout.findViewById(R.id.total_velo);
//		totalVelo.setText(""+data1[1]+"일간 사용 속도: " + velocity1 + " (원/날)");
//		TextView totalExpect = (TextView)analyseLayout.findViewById(R.id.total_expect);
//		totalExpect.setText("월말 잔고예상치: " + expectation1 + " 원 (" + expectPercent1 + "% 사용)");
//		TextView weekVelo = (TextView)analyseLayout.findViewById(R.id.week_velo);
//		weekVelo.setText("일주일간 사용 속도: " + velocity2 + " (원/날)");
//		TextView weekExpect = (TextView)analyseLayout.findViewById(R.id.week_expect);
//		weekExpect.setText("월말 잔고예상치: " + expectation2 + " 원 (" + 	expectPercent2 + "% 사용)");
//		TextView spendGuide = (TextView)analyseLayout.findViewById(R.id.spend_guide);
//		spendGuide.setText("남은 날 일별 사용가능량: " + moneyForEachRemainDays + " 원");
		
		
		
//		maxExpense = monthlyData.getTotalExpense() + monthlyData.getTotalExpense()/20;
//		View analyseView = inflater.inflate(R., null);
//		mChart = getChart(LINE_GRAPH,fromTo[0],fromTo[1],fromTo[2]);
//		mView = new GraphicalView(MainActivity.this, mChart);
//		graphLayout.addView(mView);
//		graphLayout.postInvalidate();
		double date = monthlyData.getEachDate(remainingDays[0]);
		int exp = monthlyData.getEachExpense(remainingDays[0]);
		int weekday = Integer.parseInt(sPref.getString(SettingsPreference.PREF_WDAY_BUDGET, "20000"));
		int weekend = Integer.parseInt(sPref.getString(SettingsPreference.PREF_WEND_BUDGET, "45000"));
		int weekKind = (new GregorianCalendar()).get(GregorianCalendar.DAY_OF_WEEK);
		int todayBudget = 0; 
		if(weekKind==GregorianCalendar.SATURDAY || weekKind==GregorianCalendar.SUNDAY) {
			todayBudget = weekend;
		} else {
			todayBudget = weekday;
		}
		Log.e(TAG,"Date: " + date +" exp today: " + exp);
		TextView todayPercent = (TextView)analyseLayout.findViewById(R.id.today_expected_percent);
		todayPercent.setText(""+exp*100/todayBudget);
		TextView todayDetail = (TextView)analyseLayout.findViewById(R.id.today_expected_detail);
		todayDetail.setText("오늘  " + decimalFormat.format(todayBudget-exp) + " 남음");
		
		TextView wdayExpense = (TextView)analyseLayout.findViewById(R.id.wday_expense);
		wdayExpense.setText("주중 하루 생활비: "+decimalFormat.format(weekday));
		TextView wendExpense = (TextView)analyseLayout.findViewById(R.id.wend_expense);
		wendExpense.setText("주말 하루 생활비: "+decimalFormat.format(weekend));

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
			Toast.makeText(this, "SMS를 불러오는 중...", Toast.LENGTH_SHORT).show();
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
