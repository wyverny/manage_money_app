package com.lcm.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.lcm.data.ParsedData;
import com.lcm.data.control.ParsedDataManager;
import com.lcm.smsSmini.R;
import com.lcm.view.MainActivity;
import com.lcm.view.SettingsPreference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WebUpdateListActivity extends Activity {
	private static final String TAG = "InboxListActivity";
	private static final boolean DEBUG = true;
	
	public static final String ACTION_UPDATE_WEBLIST = "com.lcm.moneytracker.UpdateWebList";
	
	private String AllSelect = "��� ����";
	private String AllDeselect = "��� ����";
	
	SharedPreferences sPref;
	ParsedDataManager parsedDataManager;
	ArrayAdapter<ParsedData> notUploadedAdapter;
	ArrayList<ParsedData> notUploadedList;
	Button selectButton;
	
	EditText login_id, login_password;
	
	Context context = this;
	protected boolean confirmed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(DEBUG) {
			StrictMode.enableDefaults();
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handle_list);
		
		updateNotUploadedList();
		
		notUploadedAdapter = new MyAdapter(this, R.layout.parsed_data_readonly, R.id.new_detail, notUploadedList);
		((ListView) findViewById(R.id.handle_listview)).setAdapter(notUploadedAdapter);
		Button sendButton = ((Button) findViewById(R.id.handle_saveButton));
		sendButton.setText("������ ������");
		sendButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP) {
					// login process
					getLoginInformation();
				}
				return true;
			}
		});
		
		selectButton = ((Button) findViewById(R.id.handle_createButton));
		selectButton.setText(AllSelect);
		selectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(selectButton.getText().equals(AllSelect)) {
					for (ParsedData parsedData : notUploadedList) {
						parsedData.setFlag(true);
					}
					selectButton.setText(AllDeselect);
				} else {
					for (ParsedData parsedData : notUploadedList) {
						parsedData.setFlag(false);
					}
					selectButton.setText(AllSelect);
				}
				notUploadedAdapter.notifyDataSetChanged();
//				notUploadedAdapter.notifyDataSetInvalidated();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		IntentFilter updateFilter = new IntentFilter(ACTION_UPDATE_WEBLIST);
		registerReceiver(updateEventReceiver, updateFilter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		unregisterReceiver(updateEventReceiver);
	}

	private void getLoginInformation() {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
		View loginLayout = inflater.inflate(R.layout.login_dialog, null);
		
		login_id = (EditText)loginLayout.findViewById(R.id.login_id);
		sPref = getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		String moneta_id = sPref.getString(SettingsPreference.PREF_MONETA_ID,"");
		login_id.setText(moneta_id);
		login_password = (EditText)loginLayout.findViewById(R.id.login_password);
		AlertDialog.Builder loginDialog = new AlertDialog.Builder(WebUpdateListActivity.this);
		loginDialog.setTitle("���Ÿ �α���");
		loginDialog.setView(loginLayout);
		loginDialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try{
					sPref.edit().putString(SettingsPreference.PREF_MONETA_ID, login_id.getText().toString()).commit();
					
					NewMonetaInteract monetaInteract = new NewMonetaInteract(WebUpdateListActivity.this,notUploadedList);
					monetaInteract.uploadParsedDatas(login_id.getText().toString(), login_password.getText().toString());
//					Log.e(TAG,"getLoginInformation onClick come here");
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		loginDialog.create();
		loginDialog.show();
	}
	
//		ArrayList<ParsedData> parsedData = new ArrayList<ParsedData>();//ParsedData[smsList.size()];
//		for(ParsedData sd : notUploadedList) {
//			if(sd.isFlag()==true) {
//				if(DEBUG) Log.i("db_log","in loadSelectedSms..");
//				// make database for updated one; use parse class
//				// need to find out if the data is already in the DB
//				sd.setFlag(false);
//				parsedData.add(sd);
//			}
//		}
//		if(parsedData.size()!=0) {
////			Intent handleIntent = new Intent(this, HandleReceivedSms.class);
////			handleIntent.putExtra("ParsedData",parsedData);
////			handleIntent.putExtra("Source","InboxListActivity");
////			startActivity(handleIntent);
//		}
//		finish();
//	}
	
	class MyAdapter extends ArrayAdapter<ParsedData> {

//		private Map<Integer, EditText> editTexts;
	    private ArrayList<ParsedData> items;
	    private Context context;
	    private int resource;
	   
	    public MyAdapter(Context context, int resource, int textViewResourceId,
	            ArrayList<ParsedData> items) {
	        super(context, resource, textViewResourceId, items);
	        this.resource = resource;
	        this.context = context;
	        this.items = items;
	    }
	    
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        final ViewGroup paren = parent;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(resource, null);
	        }

	        final ParsedData page = items.get(position);
	        if (page != null) {
		        TextView handleSpend = (TextView)v.findViewById(R.id.new_spend);
		        TextView handleDate = (TextView)v.findViewById(R.id.new_date);
		        TextView handleDetail = (TextView)v.findViewById(R.id.new_detail);
		        TextView handleCategory = (TextView)v.findViewById(R.id.new_category);
		        
		        // Spend and Date
		        handleSpend.setText("���: " + page.getSpent()+ " ��");
		        handleDate.setText("����: " + DateFormat.getDateFormat(context).format(page.getDate().getTime()));
		        
		        // Detail
		        handleDetail.setText(page.getDetail());
		        
		        // Category
		        handleCategory.setText(page.getCategory());
		        
		        if(!notUploadedList.get(position).isFlag()) {
					((TextView) v.findViewById(R.id.new_category)).setTextColor(Color.BLACK);
					((TextView) v.findViewById(R.id.new_date)).setTextColor(Color.BLACK);
					((TextView) v.findViewById(R.id.new_detail)).setTextColor(Color.BLACK);
					((TextView) v.findViewById(R.id.new_spend)).setTextColor(Color.BLACK);
				} else {
					((TextView) v.findViewById(R.id.new_category)).setTextColor(Color.parseColor("#C23B22"));
					((TextView) v.findViewById(R.id.new_date)).setTextColor(Color.parseColor("#C23B22"));
					((TextView) v.findViewById(R.id.new_detail)).setTextColor(Color.parseColor("#C23B22"));
					((TextView) v.findViewById(R.id.new_spend)).setTextColor(Color.parseColor("#C23B22"));
				}
	        }
	        v.setOnClickListener(new LoadChecked(position));
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
			if(notUploadedList.get(position).isFlag()) {
				notUploadedList.get(position).setFlag(false);
				((TextView) v.findViewById(R.id.new_category)).setTextColor(Color.BLACK);
				((TextView) v.findViewById(R.id.new_date)).setTextColor(Color.BLACK);
				((TextView) v.findViewById(R.id.new_detail)).setTextColor(Color.BLACK);
				((TextView) v.findViewById(R.id.new_spend)).setTextColor(Color.BLACK);
			} else {
				notUploadedList.get(position).setFlag(true);
				((TextView) v.findViewById(R.id.new_category)).setTextColor(Color.parseColor("#C23B22"));
				((TextView) v.findViewById(R.id.new_date)).setTextColor(Color.parseColor("#C23B22"));
				((TextView) v.findViewById(R.id.new_detail)).setTextColor(Color.parseColor("#C23B22"));
				((TextView) v.findViewById(R.id.new_spend)).setTextColor(Color.parseColor("#C23B22"));
			}
			
			boolean checkAllChecked = false;
			boolean checkNoneChecked = false;
			for (ParsedData item : notUploadedList) {
				if(item.isFlag()) checkNoneChecked = false;
				else checkAllChecked = false;
			}
			if(checkAllChecked) {
				selectButton.setText(AllDeselect);
				return;
			}
			if(checkNoneChecked) {
				selectButton.setText(AllSelect);
				return;
			}
			selectButton.setText(AllDeselect);
		}
	}

	public void updateNotUploadedList() {
		parsedDataManager = ParsedDataManager.getParsedDataManager(this);
		notUploadedList = parsedDataManager.getNotUploadedDataFromDatabase();
		Collections.sort(notUploadedList);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
	BroadcastReceiver updateEventReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
//			Toast.makeText(WebUpdateListActivity.this, "Broadcast received", Toast.LENGTH_SHORT).show();
			updateNotUploadedList();
//			((ListView) findViewById(R.id.handle_listview)).
			notUploadedAdapter = new MyAdapter(WebUpdateListActivity.this, R.layout.parsed_data_readonly, R.id.new_detail, notUploadedList);
			((ListView) findViewById(R.id.handle_listview)).setAdapter(notUploadedAdapter);
			notUploadedAdapter.notifyDataSetInvalidated();
			
//			"mmini.kr";
		}
	};
}
