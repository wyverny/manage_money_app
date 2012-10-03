package com.lcm.data.sms;

import java.util.ArrayList;

import com.lcm.data.SMSDbAdapter;
import com.lcm.smsSmini.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity {
	public static final String TAG = "MenuActivity";
	SmsItemAdapter loadAdapter;
	final ArrayList<SmsData> loadList = new ArrayList<SmsData>();
	ArrayList<SmsData> selectedList;
	SMSDbAdapter sdba;
	Context context = this;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_main);
        
        loadFromDB();
        
        loadAdapter = new SmsItemAdapter(this, R.layout.sms, R.id.smsStat,
				loadList);
		((ListView) findViewById(R.id.loadList)).setAdapter(loadAdapter);
		View v = findViewById(R.layout.sms_main);
		Button gettingSmsButton = ((Button) findViewById(R.id.extract));
		gettingSmsButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP) {
					Intent intentInbox = new Intent(MenuActivity.this, InboxListActivity.class);
					startActivity(intentInbox);
				}
				return true;
			}
		});
		
		Button registButton = ((Button) findViewById(R.id.upload));
		registButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// collect checked sms
				if(event.getAction()==MotionEvent.ACTION_UP) {
					loadSelectedSms();
					// TODO: check if they have right format or not
					
					// TODO: get Parser using parser factory and parse them
					
					// TODO: send them to sending activity
//					loadSelectedSms();
				}
				return true;
			}
		});
    }
    
    
    private void loadFromDB() {
    	loadList.clear();
    	sdba = new SMSDbAdapter(this);
    	sdba.open();
    	Cursor result = sdba.fetchAllSMS();
    	Log.e(TAG, "loadFromDB size: " + result.getCount());
    	int bodyId = result.getColumnIndex(SMSDbAdapter.KEY_BODY);
		int addrId = result.getColumnIndex(SMSDbAdapter.KEY_ADDRESS);
		int dateId = result.getColumnIndex(SMSDbAdapter.KEY_TIME);
		int loadId = result.getColumnIndex(SMSDbAdapter.KEY_UPLOAD);
		int _idid = result.getColumnIndex(SMSDbAdapter.KEY_ID);
	
		boolean load = false;
		SmsData sd;
		if (result != null) {
			while (result.moveToNext()) {
				load = (result.getInt(loadId)==1) ? true : false;
				sd = new SmsData(result.getString(bodyId),
						result.getString(addrId), 
						result.getString(dateId),
						result.getInt(_idid),
						false);
				loadList.add(sd);
			}
		}
		result.close();
    	sdba.close();
	}

	@Override
	protected void onStart() {
		super.onStart();
		loadFromDB();
		loadAdapter.notifyDataSetChanged();
	}


	private void loadSelectedSms() {
//		int total = 0;
//    	System.gc();
    	selectedList = new ArrayList<SmsData>();
		sdba = new SMSDbAdapter(context);
		sdba.open();
		
		for(SmsData sd : loadList) {
			if(sd.isLoad()==true) {
				selectedList.add(sd);
//				total++;
			}
		}
		sdba.close();
		
	}
    
	private class SmsItemAdapter extends ArrayAdapter<SmsData> {
		private ArrayList<SmsData> items;
		private int resource;

		public SmsItemAdapter(Context context, int resource,
				int textViewResourceId, ArrayList<SmsData> data) {
			super(context, resource, textViewResourceId, data);
			this.items = data;
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(resource, null);
			}
			SmsData e = items.get(position);
			if (e != null) {
				((TextView) v.findViewById(R.id.smsStat)).setText("¢Ï"
						+ e.getAddr() + ", " + e.getDateFormat());
				((TextView) v.findViewById(R.id.smsBody)).setText(e.getBody());
				CheckBox cb = (CheckBox) v.findViewById(R.id.smsLoad); 
				cb.setChecked(e.isLoad());
				cb.setOnClickListener(new LoadChecked(position,cb));
			}
			return v;
		}
	}
    
    public class LoadChecked implements OnClickListener {
		int position;
		CheckBox cb;
		
		public LoadChecked(int position,CheckBox cb) {
			this.position = position;
			this.cb = cb;
		}

		public void onClick(View v) {
			Toast.makeText(MenuActivity.this, ""+position + "checked:"+cb.isChecked(), Toast.LENGTH_SHORT).show();
//			loadList.get(position).setLoad(cb.isChecked());
		}
	}
}