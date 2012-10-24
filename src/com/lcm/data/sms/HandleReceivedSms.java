package com.lcm.data.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.lcm.data.CategoryDBAdaptor;
import com.lcm.data.ParsedData;
import com.lcm.data.ParsedDataManager;
import com.lcm.smsSmini.R;
import com.lcm.view.MainActivity;
import com.lcm.view.NotiInfoRunner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HandleReceivedSms extends Activity {
	private static final String TAG = "HandleReceivedSms";
	private static final boolean DEBUG = false;
	MyAdapter myAdapter;
	ListView listView;
	ArrayList<ParsedData> data_handled;
	CategoryDBAdaptor cDBAdaptor;
	
	@Override
	protected void onPause() {
		cDBAdaptor.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		cDBAdaptor = new CategoryDBAdaptor(HandleReceivedSms.this);
		cDBAdaptor.open();
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handle_list);
		data_handled = new ArrayList<ParsedData>();
		
		//remove create and delete button from the layout
		LinearLayout handleListBtnLayout = (LinearLayout)findViewById(R.id.handlelist_btn_layout);
		handleListBtnLayout.removeView((Button)findViewById(R.id.handle_createButton));
		handleListBtnLayout.removeView((Button)findViewById(R.id.handle_deleteButton));
		
		Bundle extra = getIntent().getExtras();
		if(extra.getString("Source").equals("InboxListActivity")) {
			ArrayList<Parcelable> parsedDatas = extra.getParcelableArrayList("ParsedData");
			//Toast.makeText(this, "HandleReceivedSms: "+parsedDatas.size()+" received", Toast.LENGTH_SHORT).show();
			for(Parcelable p: parsedDatas) {
				data_handled.add((ParsedData)p);
				if(DEBUG) if(((ParsedData)p).isFlag()) Log.e(TAG,"ParsedData is true!!!");
			}
		} else if(extra.getString("Source").equals("SmsReceiver")) {
			ParsedData[] parsedData = (ParsedData[])extra.getParcelableArray("ParsedData");
			for(ParsedData pd:parsedData) {
				data_handled.add(pd);
			}
		}
		
//		Toast.makeText(this, "HandleReceivedSms: "+data_handled.size()+" received", Toast.LENGTH_SHORT).show();
//		handleIntent.putExtra("Kind","SmsReceiver");
//		handleIntent.putExtra("SmsData", messages);
		
		myAdapter = new MyAdapter(this, R.layout.handle_data, R.id.handle_detail, data_handled);
	    listView = (ListView) findViewById(R.id.handle_listview);
//	    listView = (ListView) findViewById(android.R.id.list);
	    listView.setAdapter(myAdapter);
	    listView.setItemsCanFocus(true);
	    
	    Button button = (Button)findViewById(R.id.handle_saveButton);
	    button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Save all data into parsedData DB
				ParsedDataManager parsedDataManager = ParsedDataManager.getParsedDataManager(HandleReceivedSms.this);
				parsedDataManager.insertParsedData(data_handled);
//				for(int i=0; i<listView.getCount(); i++) {
//					Log.e("HandleReceived",data_handled.get(i).toString());
//					View item = (View)listView.getItemAtPosition(i);
//					Log.e("HandleReceived",((EditText)item.findViewById(R.id.handle_detail)).getText().toString() 
//							+ ((Spinner)item.findViewById(R.id.handle_category)).getSelectedItem().toString());
//				}
				// Save details and responding category
				HashMap<String, String> detail_category = new HashMap<String, String>();
				for(ParsedData pd:data_handled) {
					detail_category.put(pd.getDetail(), pd.getCategory());
				}
				CategoryDBAdaptor cDBAdaptor = new CategoryDBAdaptor(HandleReceivedSms.this);
				cDBAdaptor.open();
				Iterator<String> keys = detail_category.keySet().iterator();
				do {
					String key = keys.next();
					cDBAdaptor.insertDB(key, detail_category.get(key));
					if(DEBUG) Log.e(TAG,"Insert Category: " + key +" -> "+ detail_category.get(key));
				} while(keys.hasNext());
				cDBAdaptor.close();
				Toast.makeText(HandleReceivedSms.this, getText(R.string.data_saved), Toast.LENGTH_SHORT).show();
				sendBroadcast(new Intent(NotiInfoRunner.ACTION_RUN_INFORUNNER));
				finish();
			}
		});
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(this,"list item clicked",Toast.LENGTH_SHORT).show();
	}
	
	class MyAdapter extends ArrayAdapter<ParsedData> {

//		private Map<Integer, EditText> editTexts;
	    private ArrayList<ParsedData> items;
	    private boolean manipulated[];
	    private String[] category; 
	    private Context context;
	    private int resource;
	   
	    public MyAdapter(Context context, int resource, int textViewResourceId,
	            ArrayList<ParsedData> items) {
	        super(context, resource, textViewResourceId, items);
	        this.resource = resource;
	        this.context = context;
	        this.items = items;
	        manipulated = new boolean[items.size()];
	        category = getResources().getStringArray(R.array.category);
//	        editTexts = new HashMap<Integer, EditText>();
	    }
	    
	    private int findCategoryIndex(String cate) {
	    	for(int i=0; i<category.length; i++) {
	    		if(category[i].equals(cate)) return i;
	    	}
	    	return -1;
	    }

		@SuppressWarnings("unused")
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
//			Log.e("HandleReceivedSms","position: "+position);
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(resource, null);
//	            EditText editText = new EditText(context);
//	            ((LinearLayout)v.findViewById(R.id.linearLayout2)).addView(editText);
//	            editTexts.put(position, editText);
	        }
	        
	        ParsedData page = items.get(position);
	        if (page != null) {
		        TextView handleSpend = (TextView)v.findViewById(R.id.handle_spend);
		        TextView handleDate = (TextView)v.findViewById(R.id.handle_date);
		        TextView handleDetail = (TextView)v.findViewById(R.id.handle_detail);
		        Spinner handleCategory = (Spinner)v.findViewById(R.id.handle_category);
		        
//		        Log.e("HandleReceivedSms","position: "+position+" : "+page+",,,,"+handleSpend);
		        // Spend and Date
		        String hSpend = "사용: " + page.getSpent()+ " 원";
		        if(page.getInstallment()!=1) hSpend += "("+page.getInstallment()+"개월)";
		        handleSpend.setText(hSpend);
		        handleDate.setText("일자: " + DateFormat.getDateFormat(context).format(page.getDate().getTime()));
		        
		        // Detail
//		        Log.e("HANDLE_DETAIL","..."+handleDetail.getText().toString());
		        handleDetail.setText(page.getDetail());
		        handleDetail.setOnClickListener(new DetailClick(position));
//	        	handleDetail.addTextChangedListener(new DetailWatcher(position,handleDetail));
		        
		        // Category
		        ArrayAdapter adapter = ArrayAdapter
		        		.createFromResource(HandleReceivedSms.this,
		        				R.array.category,
		        				android.R.layout.simple_spinner_item);
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        handleCategory.setAdapter(adapter);

		        	// find detail if it was put before, then if it is, use used category
		        String categ = page.getCategory();
		        
		        if(false) {
			        Cursor full = cDBAdaptor.fetchAllDB();
			        if(full==null || full.getCount()==0)
			        	Log.e(TAG,"Full is null or size 0!!!!");
			        else {
			    		int categoryId = full.getColumnIndex(CategoryDBAdaptor.KEY_CATEGORY);
			    		int detailId = full.getColumnIndex(CategoryDBAdaptor.KEY_DETAIL);
			    		
			    		full.moveToFirst();
			    		do {
			    			String category = full.getString(categoryId);
			    			String detail = full.getString(detailId);
			    			Log.e(TAG,"Detail: "+detail + " Category: " + category);
			    		} while(full.moveToNext());
			        }
			        full.close();
		        }
		        
		        // if category is changed flag is true
		        if(!page.isFlag()) {
			        Cursor data = cDBAdaptor.fetchDB(CategoryDBAdaptor.KEY_DETAIL, page.getDetail());
			        data.moveToFirst();
			        if(data!=null && data.getCount()!=0) {
						int categoryId = data.getColumnIndex(CategoryDBAdaptor.KEY_CATEGORY);
						categ = data.getString(categoryId);
						if(DEBUG) Log.e(TAG,page.getDetail()+"'s category found: " + categ);
					}
			        data.close();
		        }
		        boolean temp = page.isFlag();
	        	handleCategory.setSelection(findCategoryIndex(categ)); // this invokes itemSelected Listener
	        	page.setFlag(temp);
	        	Log.e(TAG,"Detail" + page.getDetail() + " Categ: " + categ + " Flag: " + page.isFlag());
		        handleCategory.setOnItemSelectedListener(new CategorySelectedListener(position,handleCategory));
	        }
	        
	        return v;
	    }
		
		private class DetailClick implements OnClickListener {
			private int index;
			public DetailClick(int index) {
				this.index = index;
			}
			@Override
			public void onClick(View v) {
				checkDialog(data_handled.get(index).getDetail(),index);
			}
		}
		
		private class DetailWatcher implements TextWatcher {
			private EditText editText;
			private int index;
//			ParsedData item;
			public DetailWatcher(int index, EditText editText) {
//				Log.e("HandleReceivedSms","index: " + index + " text: "+ et.toString() + ", " + item.getDetail());
				this.editText = editText;
				this.index = index;
//				this.item = item;
//				editText.setText(data_handled.get(index).getDetail());
				Log.e("HandleReceivedSms","index: "+index+" detail: "+ data_handled.get(index).getDetail() + " __s__: "+editText.getText().toString());
//				if(editTexts.get(index)!=null)
//					editTexts.get(index).setText(data_handled.get(index).getDetail());
			}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(data_handled.get(index).getDetail().equals(editText.getText().toString())) return;
//				else 
//					Log.e("HandleReceivedSms","index: "+index+" detail: "+ data_handled.get(index).getDetail() + " __s__: "+editText.getText().toString());
//				Log.e("HandleReceivedSms","index: "+ index+ " onTextChanged: "+s.toString());
//				data_handled.get(index).setDetail(editTexts.get(index).getText().toString());
//				data_handled.get(index).setDetail(s.toString());
//				notifyDataSetChanged();
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			public void afterTextChanged(Editable s) {
//				Log.e("HandleReceivedSms","__index: " + index + " text: "+ editText.toString() + "item: " + item.getDetail());
//				item.get(index).setDetail(s.toString());
//				editText.setText(item.getDetail());
//				Log.e("HandleReceivedSms","__index: "+index+" detail: "+item.toString());
			}
		}
		
		private class CategorySelectedListener implements OnItemSelectedListener {
			private int index;
			private Spinner spinner;
			public CategorySelectedListener(int index, Spinner spinner) {
				this.index = index;
				this.spinner = spinner;
			}
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				data_handled.get(index).setCategory(spinner.getSelectedItem().toString());
				data_handled.get(index).setFlag(true);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				spinner.setSelection(3);
			}
		}
	}
	private void checkDialog(String number, int index){
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		final int in = index;
		
		Context mContext = HandleReceivedSms.this;
		LayoutInflater inflator = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflator.inflate(R.layout.detail_dialog, (ViewGroup)findViewById(R.id.layout_root));
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		final EditText detail = (EditText)layout.findViewById(R.id.dialogDetailText);
		detail.setText(number);
		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				data_handled.get(in).setDetail(detail.getText().toString());
				myAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog = builder.create();
		alertDialog.show();
		imm.showSoftInput(detail, 0);
		detail.requestFocus();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
}
