package com.lcm.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

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

//	@Override
//	public void onCreate() {
//		Log.e(TAG,"Service Created");
//		//unregisterRestartAlarm();
//		super.onCreate();
//	}

	
	
	@Override
	public void onStart(Intent intent, int startId) {
		showNotificationInfo();
		super.onStart(intent, startId);
	}

//	@Override
//	public void onDestroy() {
//		Log.e(TAG,"Service Destroyed");
//		//registerRestartAlarm();
//		super.onDestroy();
//	}

	private void registerRestartAlarm() {
		// TODO: if it is set not to show notification bar information, the below shouldn't run
//		Log.e(TAG,"Register Restart Alarm");
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
//		Log.e(TAG,"Unregister Restart Alarm");
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
		CharSequence tickerText = getText(R.string.app_name); // 작은 노티 문장
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon,tickerText,when);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
	    notification.flags |= Notification.FLAG_NO_CLEAR;
		
		// expanded 노티바 관련
//		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout_10_10);
		int[] data = getMonthly();
		int drawbleId = getDrawbleIDfromPercent(100 - data[0], 100-data[2]);
		
		RemoteViews contentView = new RemoteViews(getPackageName(), getLayoutIDfromPercent(100-data[0], 100-data[2]));
		DecimalFormat format = new DecimalFormat("#,#00 원");
		
		contentView.setTextViewText(R.id.remain_percent, (100 - data[0]) + getText(R.string.percent).toString());
		contentView.setTextViewText(R.id.remain_detail, "전체 생활비 " + format.format(data[1]) + " 남음");
		contentView.setTextViewText(R.id.today_percent, (100 - data[2]) + getText(R.string.percent).toString());
		contentView.setTextViewText(R.id.today_detail, "오늘 생활비 "+ format.format(data[3]) + " 남음"); 
		notification.contentView = contentView;
		
		Intent notificationInetnt = new Intent(this,MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationInetnt, 0);
		notification.contentIntent = contentIntent;

		// whether icon is seen or not according to the setting
		SharedPreferences sPref = getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		boolean showNotiIcon = sPref.getBoolean(SettingsPreference.PREF_NOTI_INFO,false);
		long hiddenTime = SDK_VERSION >= 9 ? Long.MAX_VALUE : -Long.MAX_VALUE;
//		Log.e(TAG,"HiddenTime: " + hiddenTime + "SDK: " + SDK_VERSION);
		notification.when = showNotiIcon ? System.currentTimeMillis() : hiddenTime;
	    notification.icon = showNotiIcon? drawbleId : R.drawable.ic_placeholder;
		
		nm.notify(NOTI_INFO_ID, notification);
	}
	
	private int getDrawbleIDfromPercent(int remainPercent, int todayPercent) {
		if(remainPercent >= 66) {
			if(todayPercent >= 66) {
				return R.drawable.progress_100_100;
			} else if(todayPercent >= 33) {
				return R.drawable.progress_100_60;
			} else {
				return R.drawable.progress_100_30;
			}
		}
		if(remainPercent >= 33) {
			if(todayPercent >= 66) {
				return R.drawable.progress_60_100;
			} else if(todayPercent >= 33) {
				return R.drawable.progress_60_60;
			} else {
				return R.drawable.progress_60_30;
			}
		} else {
			if(todayPercent >= 66) {
				return R.drawable.progress_30_100;
			} else if(todayPercent >= 33) {
				return R.drawable.progress_30_60;
			} else {
				return R.drawable.progress_30_30;
			}
		}
	}
	
	private int getLayoutIDfromPercent(int remainPercent, int todayPercent) {
		if(remainPercent >= 66) {
			if(todayPercent >= 66) {
				return R.layout.notification_layout_10_10;
			} else if(todayPercent >= 33) {
				return R.layout.notification_layout_10_6;
			} else {
				return R.layout.notification_layout_10_3;
			}
		}
		if(remainPercent >= 33) {
			if(todayPercent >= 66) {
				return R.layout.notification_layout_6_10;
			} else if(todayPercent >= 33) {
				return R.layout.notification_layout_6_6;
			} else {
				return R.layout.notification_layout_6_3;
			}
		} else {
			if(todayPercent >= 66) {
				return R.layout.notification_layout_3_10;
			} else if(todayPercent >= 33) {
				return R.layout.notification_layout_3_6;
			} else {
				return R.layout.notification_layout_3_3;
			}
		}
	}
	
	private int[] getMonthly() {
		// get this month's date info and Expense data
		Calendar today = new GregorianCalendar();
		// date should be according to accounting date, say every 25th
		SharedPreferences sPref =  getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		int accountingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "15"));
		int month = today.get(Calendar.MONTH);
		if(today.get(Calendar.DATE)<accountingDate) {
			month = (today.get(Calendar.MONTH)==0)? 11 : today.get(Calendar.MONTH)-1;
		}
		// and month should be previous month
		Util util = new Util();
		// Log.e(TAG,"getFromTo: " + (today.getYear()+1900) + "," + month);
		Calendar[] fromTo = util.getFromTo(today.get(Calendar.YEAR), month, accountingDate);
		int[] expense = null;
		double maxExpense = 0;
		MonthlyData monthlyData = new MonthlyData(this, fromTo[Util.FROM], fromTo[Util.THROUGH], fromTo[Util.TO]);
		expense = monthlyData.accumulateExpense();
		
		int[] remainingDays = monthlyData.getPassedRemainingDays(today);
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.DATE, 1);
		
		int[] data1 = monthlyData.getTotalExpenseFromTo(accountingDate, gc.get(Calendar.DATE));
		int totalExpense = monthlyData.getTotalBudget()==0? 1:monthlyData.getTotalBudget();
		int budgetUsedPercent = (int)data1[0]*100/totalExpense;
		int timePassedPercent = (int)remainingDays[0]*100/monthlyData.getTotalDays();
		if(timePassedPercent==0) timePassedPercent=1;
//		Log.e(TAG,"getTotalDays: "+monthlyData.getTotalDays()+ " remainingDays[0]: "+ remainingDays[0]);
//		Log.e(TAG,"budgetUsedPercent: "+budgetUsedPercent+ " timePassedPercent: "+ timePassedPercent);
		int remainPercent = budgetUsedPercent;//*100/timePassedPercent;
		int remainedBudget = monthlyData.getTotalBudget() - data1[0];
		
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
