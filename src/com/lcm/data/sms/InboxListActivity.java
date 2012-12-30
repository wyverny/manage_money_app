package com.lcm.data.sms;

import java.util.ArrayList;

import com.lcm.data.ParsedData;
import com.lcm.data.control.ExpenditureDBAdaptor;
import com.lcm.data.control.SMSDbAdapter;
import com.lcm.data.parse.NotValidSmsDataException;
import com.lcm.smsSmini.R;
import com.lcm.view.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InboxListActivity extends Activity implements OnScrollListener {
	private static final String TAG = "InboxListActivity";
	private static final boolean DEBUG = false;
	ArrayAdapter<ParsedData> inboxAdapter;
	
	private ExpenditureDBAdaptor expenditureDBAdaptor;
	Cursor smsDbCursorResult;
	ListView smsListView;
	String bodyIdString;
	String addrIdString;
	String dateIdString;
	String _idIdString;
	
	
	final ArrayList<ParsedData> parsedList = new ArrayList<ParsedData>();
	SMSConverter smsConverter;
	SMSDbAdapter smsDbAdapter;
	Context context = this;
	protected boolean confirmed = false;
	private boolean mLockListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox);

		mLockListView = true;
		smsConverter = new SMSConverter(context);
		openSmsDB(null, null);
		loadSmsDBData(20);
		
		inboxAdapter = new SmsItemAdapter(this, R.layout.sms, R.id.smsStat,	parsedList);
		smsListView = (ListView)findViewById(R.id.inboxList11);
		
		smsListView.setOnScrollListener(this);
		smsListView.setAdapter(inboxAdapter);
		Button button = ((Button) findViewById(R.id.inbox_loadButton));
		button.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP) {
					loadSelectedSms();
				}
				return true;
			}
		});
	}

	
	private void loadSelectedSms() {
//		smsDbAdapter = new SMSDbAdapter(context);
//		smsDbAdapter.open();
		ArrayList<ParsedData> parsedData = new ArrayList<ParsedData>();//ParsedData[smsList.size()];
		for(ParsedData sd : parsedList) {
			if(sd.isFlag()==true) {
				if(DEBUG) Log.i("db_log","in loadSelectedSms..");
				// make database for updated one; use parse class
				// need to find out if the data is already in the DB
				sd.setFlag(false);
				parsedData.add(sd);
			}
		}
		if(parsedData.size()!=0) { 
			Intent handleIntent = new Intent(this, HandleReceivedSms.class);
			handleIntent.putExtra("ParsedData",parsedData);
			handleIntent.putExtra("Source","InboxListActivity");
			startActivity(handleIntent);
		}
//		smsDbAdapter.close();
		finish();
	}

	private class SmsItemAdapter extends ArrayAdapter<ParsedData> {
		private ArrayList<ParsedData> items;
		private int resource;

		public SmsItemAdapter(Context context, int resource,
				int textViewResourceId, ArrayList<ParsedData> data) {
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
			ParsedData e = items.get(position);
			if (e != null) {
				((TextView) v.findViewById(R.id.smsStat)).setText("Date: "+e.getDateFormat());
				((TextView) v.findViewById(R.id.smsBody)).setText(e.getSpent() + "\t" + e.getDetail());
//				CheckBox cb = (CheckBox) v.findViewById(R.id.smsLoad);
//				cb.setChecked(e.isFlag());
				if(e.isFlag()) {
					((TextView) v.findViewById(R.id.smsStat)).setTextColor(Color.parseColor("#C23B22"));
					((TextView) v.findViewById(R.id.smsBody)).setTextColor(Color.parseColor("#C23B22"));
				} else {
					((TextView) v.findViewById(R.id.smsStat)).setTextColor(Color.BLACK);
					((TextView) v.findViewById(R.id.smsBody)).setTextColor(Color.BLACK);
				}
				//cb.setFocusable(false);
				//cb.setOnClickListener(new LoadChecked(position,cb));
				v.setOnClickListener(new LoadChecked(position));
			}
			return v;
		}
	}
	
	public class LoadChecked implements OnClickListener {
		int position;
		//CheckBox cb;
		
		public LoadChecked(int position) { //,CheckBox cb) {
			this.position = position;
			//this.cb = cb;
		}

		public void onClick(View v) {
			// check if checked sms is feasible one, otherwise show a dialog that check if it is right one
//			if(cb.isChecked()) {
//				int[] phoneNum = getResources().getIntArray(R.array.phone_no);
//				boolean isBanks = false;
//				String addr = null;
//				for(int num:phoneNum) {
//					addr = smsList.get(position).getAddr();
//					if(addr.contains("+82")) {
//						addr = addr.substring(3);
//					}
//					if(Integer.parseInt(addr)==num) isBanks = true;
//				}
//				if(!isBanks) {
//					checkDialog(addr);
//				}
//				if(confirmed) smsList.get(position).setLoad(cb.isChecked());
//				if(!confirmed) cb.setChecked(false);
//			} else {
//				smsList.get(position).setLoad(cb.isChecked());
//			}
//			smsList.get(position).setLoad(cb.isChecked());
//			CheckBox cb = (CheckBox) v.findViewById(R.id.smsLoad);
//			cb.setChecked(!cb.isChecked());
			//parsedList.get(position).setFlag(cb.isChecked());
			if(parsedList.get(position).isFlag()) {
				parsedList.get(position).setFlag(false);
				((TextView) v.findViewById(R.id.smsStat)).setTextColor(Color.BLACK);
				((TextView) v.findViewById(R.id.smsBody)).setTextColor(Color.BLACK);
			} else {
				parsedList.get(position).setFlag(true);
				((TextView) v.findViewById(R.id.smsStat)).setTextColor(Color.parseColor("#C23B22"));
				((TextView) v.findViewById(R.id.smsBody)).setTextColor(Color.parseColor("#C23B22"));
			}
		}
	}

	/**
	 * getting sms from sms data base
	 * @param smsArg
	 * @param smsString
	 */
	public void openSmsDB(String[] smsArg, String[] smsString) {
		// SAMSUNG GA CP URL: content://com.btb.sec.mms.provider/message
		// SAMSUNG CP URL: content://com.sec.mms.provider/message
			
		// LG CP URL: content://com.lge.messageprovider/msg/inbox
			// incoming number: sender, body: body.
		// General: content://sms/inbox
		bodyIdString = "body";
		addrIdString = "address";
		dateIdString = "date";
		_idIdString = "_id";
		
		boolean mmsAvailable = false;
		smsDbCursorResult = null;
		
		if(!mmsAvailable) {
			// for other phones
			smsDbCursorResult = getContentResolver().query(
					Uri.parse("content://sms/inbox"), null, null, null, null);
			if(smsDbCursorResult == null || smsDbCursorResult.getCount()==0) smsDbCursorResult.close();
			else {
				Log.e(TAG,"Normal Phone it is!!!!!!!!");
				mmsAvailable = true;
			}
		}
		if(!mmsAvailable) {
			// for LG phone
			smsDbCursorResult = getContentResolver().query(
					Uri.parse("content://com.lge.messageprovider/msg/inbox"), null, null, null, null);
			if(smsDbCursorResult == null || smsDbCursorResult.getCount()==0) smsDbCursorResult.close();
			else {
				Log.e(TAG,"LG Phone it is!!!!!!!!");
				mmsAvailable = true;
				bodyIdString = "body";
				dateIdString = "date";
				addrIdString = "sender";
				_idIdString = "_id";
			}
		}
		if(!mmsAvailable) {
			// for SamSung phone
			smsDbCursorResult = getContentResolver().query(
					Uri.parse("content://com.sec.mms.provider/message"), null, null, null, null);
			if(smsDbCursorResult == null || smsDbCursorResult.getCount()==0) smsDbCursorResult.close();
			else {
				Log.e(TAG,"Samsung Phone it is!!!!!!!!");
				mmsAvailable = true;
				bodyIdString = "Title";
				dateIdString = "RegTime";
				addrIdString = "MDN1st";
				_idIdString = "RootID";
			}
		}
		if(!mmsAvailable) {
			// for SamSungGalaxy A phone
			smsDbCursorResult = getContentResolver().query(
					Uri.parse("content://com.btb.sec.mms.provider/message"), null, null, null, null);
			if(smsDbCursorResult == null || smsDbCursorResult.getCount()==0) smsDbCursorResult.close();
			else {
				Log.e(TAG,"Samsung Phone it is!!!!!!!!");
				mmsAvailable = true;
				bodyIdString = "Title";
				dateIdString = "RegTime";
				addrIdString = "MDN1st";
				_idIdString = "RootID";
			}
		}
		
		expenditureDBAdaptor = new ExpenditureDBAdaptor(this);
		expenditureDBAdaptor.open();
	}
	
	private void loadSmsDBData(int size) {
		int bodyid = smsDbCursorResult.getColumnIndex(bodyIdString);
		int addrid = smsDbCursorResult.getColumnIndex(addrIdString);
		int dateid = smsDbCursorResult.getColumnIndex(dateIdString);
		int _idid = smsDbCursorResult.getColumnIndex(_idIdString);
		
		int index = 0;
		mLockListView = true;
		
		if (smsDbCursorResult != null) {
			while (smsDbCursorResult.moveToNext() && index < size) {
				SmsData sd = new SmsData(smsDbCursorResult.getString(bodyid),
						smsDbCursorResult.getString(addrid), smsDbCursorResult.getString(dateid), Integer.parseInt(smsDbCursorResult.getString(_idid)));
				// check if the SmsData is ok to be shown using parser
//				Log.e(TAG,"updateSMSList: "+sd.getDate() + "," + sd.getBody());
				if(smsConverter.isValidSms(sd) && !expenditureDBAdaptor.isDataExist(Long.parseLong(sd.getDate()))
						// should check if sd is already put into the DB & check DB code here 
						) {
//					Log.e("MMS------", "address: " + result.getString(addrid)+" _id: " + result.getString(_idid));
//					smsList.add(sd);
					try {
						ParsedData parsedData = smsConverter.convertSms(sd);
						if(parsedData!=null) {
							parsedList.add(parsedData);
							index++;
						}
					} catch (NotValidSmsDataException e) {
						continue;
//						e.printStackTrace();
					}
				}
			}
		}
		
		mLockListView = false;
		
		if(inboxAdapter!=null) {
//			smsListView.removeFooterView(footer);
			inboxAdapter.notifyDataSetChanged();
		}
			
	}
	
	@Override
	protected void onPause() {
		expenditureDBAdaptor.close();
		smsDbCursorResult.close();
		super.onPause();
	}


	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int count = totalItemCount - visibleItemCount;
		
		if(firstVisibleItem >= count && totalItemCount != 0
				&& mLockListView == false)
		{
//			smsListView.addFooterView(footer);
//			smsListView.
			Toast.makeText(this, "데이터 로딩 중", Toast.LENGTH_SHORT).show();
			loadSmsDBData(20);
		}
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
	/*
	private void checkDialog(String number){
		confirmed = false;
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("Do you want to include "+ number +" anyway?").setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						confirmed  = true;
						Toast.makeText(InboxListActivity.this, "Confirmed", Toast.LENGTH_SHORT).show();
						// Action for 'Yes' Button
					}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// Action for 'NO' Button
					dialog.cancel();
				}
			});
		AlertDialog alert = alt_bld.create();
		alert.setTitle("This isn\'t from any banks");
		alert.setIcon(R.drawable.icon);
		alert.show();
	} */

}
