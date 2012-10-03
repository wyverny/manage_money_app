package com.lcm.view;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.lcm.data.MonthlyData;
import com.lcm.data.SettingsPreference;
import com.lcm.smsSmini.R;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class NotiInfo extends Service {
	public static final int NOTI_INFO_ID = 189;
	public static final int SDK_VERSION = Integer.parseInt(Build.VERSION.SDK);
	private static final String TAG = "NotiInfo";

	@Override
	public void onCreate() {
		Log.e(TAG,"Service Created");
		unregisterRestartAlarm();
		super.onCreate();
	}

	
	
	@Override
	public void onStart(Intent intent, int startId) {
		showNotificationInfo();
		super.onStart(intent, startId);
	}



	@Override
	public void onDestroy() {
		Log.e(TAG,"Service Destroyed");
		registerRestartAlarm();
		super.onDestroy();
	}

	private void registerRestartAlarm() {
		// TODO: if it is set not to show notification bar infomation, the below shouldn't run
		Log.e(TAG,"Register Restart Alarm");
		Intent intent = new Intent(NotiInfo.this, NotiInfoRunner.class);
		intent.setAction(NotiInfoRunner.ACTION_RESTART_PERSISTENTSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(NotiInfo.this, 0, intent, 0);
		long firstTime = SystemClock.elapsedRealtime();
		firstTime += 1*1000;
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME, firstTime, 10*1000, sender);
		// excerpted from http://karyurid.tistory.com/97
	}

	private void unregisterRestartAlarm() {
		Log.e(TAG,"Unregister Restart Alarm");
		Intent intent = new Intent(NotiInfo.this, NotiInfoRunner.class);
		intent.setAction(NotiInfoRunner.ACTION_RESTART_PERSISTENTSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(NotiInfo.this, 0, intent, 0);
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.cancel(sender);
	}
	
	private void showNotificationInfo() {
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		// 노티바 관련
		int icon = R.drawable.icon; // 사용량 작은 아이콘으로 표시
		CharSequence tickerText = "Hello"; // 작은 노티 문장
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon,tickerText,when);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
	    notification.flags |= Notification.FLAG_NO_CLEAR;
		
		// expanded 노티바 관련
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
		int[] data = getMonthly();
		//contentView.setImageViewResource(, srcId)
		Toast.makeText(this, "Data:"+data[0]+","+data[2],Toast.LENGTH_SHORT).show();
		contentView.setProgressBar(R.id.remainPercent, 100, data[0], false);
		contentView.setTextViewText(R.id.remainDetail, ""+data[1]);
		contentView.setProgressBar(R.id.todayPercent, 100, data[2], false);
		contentView.setTextViewText(R.id.todayDetail, ""+data[3]); 
		notification.contentView = contentView;
		
		Intent notificationInetnt = new Intent(this,MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationInetnt, 0);
		notification.contentIntent = contentIntent;

		// whether icon is seen or not according to the setting
		SharedPreferences sPref = getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		boolean showNotiIcon = sPref.getBoolean(SettingsPreference.PREF_NOTI_INFO,false);
		long hiddenTime = SDK_VERSION >= 9 ? Long.MAX_VALUE : -Long.MAX_VALUE;
		Log.e(TAG,"HiddenTime: " + hiddenTime + "SDK: " + SDK_VERSION);
		notification.when = showNotiIcon ? System.currentTimeMillis() : hiddenTime;
	    notification.icon = showNotiIcon? R.drawable.ic_logo_white : R.drawable.ic_placeholder;
		
		nm.notify(NOTI_INFO_ID, notification);
	}
	
	private int[] getMonthly() {
		// get this month's date info and Expense data
		Date today = new Date();
		// date should be according to accounting date, say every 25th
		SharedPreferences sPref =  getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		int accountingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "25"));
		int month = today.getMonth();
		if(today.getDate()<accountingDate) {
			month = (today.getMonth()==0)? 11 : today.getMonth()-1;
		}
		// and month should be previous month
		Util util = new Util();
		// Log.e(TAG,"getFromTo: " + (today.getYear()+1900) + "," + month);
		Date[] fromTo = util.getFromTo(today.getYear()+1900, month, accountingDate);
		int[] expense = null;
		double maxExpense = 0;
		MonthlyData monthlyData = new MonthlyData(this, fromTo[Util.FROM], fromTo[Util.THROUGH], fromTo[Util.TO]);
		expense = monthlyData.accumulateExpense();
		
		int[] remainingDays = monthlyData.getPassedRemainingDays(today);
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.DATE, 1);
		
		int[] data1 = monthlyData.getTotalExpenseFromTo(accountingDate, gc.get(Calendar.DATE));
		int totalExpense = monthlyData.getTotalExpense()==0? 1:monthlyData.getTotalExpense();
		int budgetUsedPercent = (int)data1[0]*100/totalExpense;
		int timePassedPercent = (int)remainingDays[0]*100/monthlyData.getTotalDays();
		if(timePassedPercent==0) timePassedPercent=1;
		Log.e(TAG,"getTotalDays: "+monthlyData.getTotalDays()+ " remainingDays[0]: "+ remainingDays[0]);
		Log.e(TAG,"budgetUsedPercent: "+budgetUsedPercent+ " timePassedPercent: "+ timePassedPercent);
		int remainPercent = budgetUsedPercent*100/timePassedPercent;
		int remainedBudget = monthlyData.getTotalExpense() - data1[0];
		
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
		int todayPercent = exp*100/todayBudget;
		int todayRemain = todayBudget-exp;
		int[] result = {remainPercent, remainedBudget, todayPercent, todayRemain};
		return result;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
}
