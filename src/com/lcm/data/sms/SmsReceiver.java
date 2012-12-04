package com.lcm.data.sms;

import java.util.Date;

import com.lcm.data.ParsedData;
import com.lcm.data.control.ParsedDataManager;
import com.lcm.data.parse.NotValidSmsDataException;
import com.lcm.smsSmini.R;
import com.lcm.view.SettingsPreference;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	SMSConverter smsConverter;
	private static final int NOTIFICATION_ID = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		ParsedData parsedData = null;
		smsConverter = new SMSConverter(context);
		// saving number of unhandled messages
		SharedPreferences sharedPreferences = context.getSharedPreferences("unhandledMsg", context.MODE_PRIVATE);
		int count = sharedPreferences.getInt("UnhandledMsgCount", 0) + 1;
		sharedPreferences.edit().putInt("UnhandledMsgCount", count);
		
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			Bundle bundle = intent.getExtras();
			if(bundle==null) {
				return;
			} else {
				Object[] pdusObj = (Object[])bundle.get("pdus");
				if(pdusObj==null) {
					return;
				}
				SmsMessage[] messages = new SmsMessage[pdusObj.length];
				String str = "";
				
				for(int i=0; i<messages.length; i++) {
					messages[i] =SmsMessage.createFromPdu((byte[])pdusObj[i]);
					 str += "SMS from " + messages[i].getOriginatingAddress();
		                str += " :";
		                str += messages[i].getMessageBody().toString();
		                str += "\n";
					Log.e("SmsReceiver", ""+messages[i].getMessageBody());
					if(smsConverter.isValidSms(messages[i].getMessageBody())) {
						try {
							// TODO: if setting is automatically insert, then it should be changed
							SharedPreferences sPref = context.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
							boolean autoSave = sPref.getBoolean(SettingsPreference.PREF_AUTO_SAVE,false);
							
							if(!autoSave) {// if not automatically insert
								Toast.makeText(context, "카드 문자를 받았습니다!!", Toast.LENGTH_SHORT).show();
								Notification notification = new Notification(R.drawable.sms_icon, "카드 사용 문자 받음", new Date().getTime());
								Intent handleIntent = new Intent(context,InboxListActivity.class);
								PendingIntent handleMessage = PendingIntent.getActivity(context, 0, handleIntent, 0);
								notification.flags |= Notification.FLAG_AUTO_CANCEL;
								notification.setLatestEventInfo(context, "카드 문자", "카드 사용 승인 문자를 받았습니다.", handleMessage);
								NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
								notificationManager.notify(NOTIFICATION_ID, notification);
							} else { // automatically insert
								parsedData = smsConverter.convertSms(messages[i]);
								if(parsedData==null)
									return;
								
								ParsedDataManager parsedDataManager = ParsedDataManager.getParsedDataManager(context);
								parsedDataManager.insertParsedData(parsedData);
								Toast.makeText(context, "카드 문자를 자동 저장하였습니다!!", Toast.LENGTH_SHORT).show();
								/*
								parsedData.setSms_id(-1);
								Notification notification = new Notification(R.drawable.icon, "spend message", new Date().getTime());
								Intent handleIntent = new Intent(context, HandleReceivedSms.class);
								handleIntent.putExtra("Source","SmsReceiver");
								handleIntent.putExtra("ParsedData",parsedData);
								handleIntent.putExtra("SmsData_body", messages[i].getMessageBody());
								handleIntent.putExtra("SmsData_time", messages[i].getTimestampMillis());
								PendingIntent handleMessage = PendingIntent.getActivity(context, 0, handleIntent, 0);
								notification.setLatestEventInfo(context, (CharSequence)"Spend message", (CharSequence)(count+" unhandled messages"), handleMessage);
								Toast.makeText(context, "Spend Data has been saved automatically!!", Toast.LENGTH_SHORT).show();
								// TODO: parsedData should be processed further and inserted directly into the DB.
								*/
							}
						} catch (NotValidSmsDataException e) {e.printStackTrace();}
					}
				}
//				Toast.makeText(context, str, Toast.LENGTH_LONG).show();
			}
		}
	}

}
