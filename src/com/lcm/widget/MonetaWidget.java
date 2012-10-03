package com.lcm.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.lcm.smsSmini.R;
import com.lcm.view.MainActivity;

public class MonetaWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for(int i=0; i<appWidgetIds.length; i++) {
			int widgetId = appWidgetIds[i];
			
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
			RemoteViews remoteView = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
			remoteView.setOnClickPendingIntent(widgetId, pendingIntent);
		}
	}

}
