package com.lcm.view;

import com.lcm.data.SettingsPreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class NotiInfoRunner extends BroadcastReceiver {
	public static final String ACTION_RESTART_PERSISTENTSERVICE = "ACTION_RESTART_PERSISTENTSERVICE";
	public static final String ACTION_RUN_INFORUNNER = "com.lcm.moneytracker.NotiInfoRunner";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("NotiInfoRunner", "RestartService called!!");
		if(intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)
				|| intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
				|| intent.getAction().equals(ACTION_RUN_INFORUNNER)) {
			// TODO: if it is set not to show notification bar infomation, the below shouldn't run
			SharedPreferences sPref = context.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
			boolean showNoti = sPref.getBoolean(SettingsPreference.PREF_NOTI_INFO,false);
			if(showNoti) {
				Intent notiInfo = new Intent(context, NotiInfo.class);
				context.startService(notiInfo);
			}
		}
	}

}
