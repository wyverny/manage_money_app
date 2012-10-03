package com.lcm.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import com.lcm.data.ParsedData;
import com.lcm.data.ParsedDataManager;
import com.lcm.data.sms.HandleReceivedSms;
import com.lcm.smsSmini.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class HandleParsedData extends Activity {
	private static final String TAG = "HandleParsedData";
	MyAdapter myAdapter;
	ListView listView;
	ArrayList<ParsedData> data_handled;
	ParsedDataManager parsedDataManager;
	ParsedData deleted_data;
	ArrayList<ParsedData> deleted_datas;
	ArrayList<ParsedData> created_datas;
	int year, month, date;
	Date chosenDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handle_list);
		deleted_datas = new ArrayList<ParsedData>();
		created_datas = new ArrayList<ParsedData>();
		
		Bundle extra = getIntent().getExtras();
		int year = extra.getInt("Year");
		int month = extra.getInt("Month");
		int date = extra.getInt("Date");
		
		parsedDataManager = ParsedDataManager.getParsedDataManager(HandleParsedData.this);
		GregorianCalendar gc = new GregorianCalendar(year, month, date);
		
		GregorianCalendar gnc = (GregorianCalendar)gc.clone();
		
		gnc.add(GregorianCalendar.DATE, 1);
		data_handled = parsedDataManager.getParsedDataFromDatabase(new Date(gc.getTimeInMillis()), new Date(gnc.getTimeInMillis()));
		gc.add(GregorianCalendar.HOUR, 12);
		chosenDate = new Date(gc.getTimeInMillis());
		
//		Log.e(TAG,"Data handled: " + data_handled);
		myAdapter = new MyAdapter(this, R.layout.handle_data, R.id.handle_detail, data_handled);
	    listView = (ListView) findViewById(R.id.handle_listview);
//		listView = (ListView) findViewById(android.R.id.list);
//	    setListAdapter(myAdapter);
	    listView.setAdapter(myAdapter);
	    listView.setItemsCanFocus(true);
	    
//	    listView.setOnItemClickListener(new OnItemClickListener(){
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				Toast.makeText(HandleParsedData.this, "item clicked", Toast.LENGTH_SHORT).show();
//			}
//	    });
	    
	    LinearLayout handleListBtnLayout = (LinearLayout)findViewById(R.id.handlelist_btn_layout);
	    Button createBtn = (Button)handleListBtnLayout.findViewById(R.id.handle_createButton);
	    createBtn.setOnClickListener(onClickListener);
	    Button deleteBtn = (Button)handleListBtnLayout.findViewById(R.id.handle_deleteButton);
	    deleteBtn.setOnClickListener(onClickListener);
	    
	    Button button = (Button)findViewById(R.id.handle_saveButton);
	    button.setOnClickListener(onClickListener);
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View btn) {
			switch (btn.getId()) {
			case R.id.handle_saveButton:
				// update modified data
				parsedDataManager.updateParsedData(data_handled);
				// deleted deleted data
				parsedDataManager.deleteParsedData(deleted_datas);
				// insert created data
				parsedDataManager.insertParsedData(created_datas);
				
				Toast.makeText(HandleParsedData.this, "������ ���� ��...", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case R.id.handle_createButton:
				// TODO: create new parsedData; need to create
				newParsedDataDialog();
				break;
			case R.id.handle_deleteButton:
				// TODO: delete selected parsedData
//				Log.e(TAG,((ParsedData)listView.getSelectedItem()).toString());
				Log.e(TAG,"selectedItem position"+listView.getSelectedItemPosition());
//				int selectedItem = listView.getSelectedItemPosition();
//				deleted_data.add(data_handled.get(selectedItem));
				if(deleted_data==null) return;
				data_handled.remove(deleted_data);
//				myAdapter.remove(deleted_data); // XXX is this right way to do???
				deleted_datas.add(deleted_data);
				deleted_data = null;
				myAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			
		}
	};
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//	    Object o = this.getListAdapter().getItem(position);
//	    String pen = o.toString();
		Toast.makeText(this,"list item clicked",Toast.LENGTH_SHORT).show();
	}
	
	class MyAdapter extends ArrayAdapter<ParsedData> {

//		private Map<Integer, EditText> editTexts;
	    private ArrayList<ParsedData> items;
	    private String[] category;
	    private Context context;
	    private int resource;
	   
	    public MyAdapter(Context context, int resource, int textViewResourceId,
	            ArrayList<ParsedData> items) {
	        super(context, resource, textViewResourceId, items);
	        this.resource = resource;
	        this.context = context;
	        this.items = items;
	        category = getResources().getStringArray(R.array.category);
//	        editTexts = new HashMap<Integer, EditText>();
	    }
	    
	    private int findCategoryIndex(String cate) {
	    	for(int i=0; i<category.length; i++) {
	    		if(category[i].equals(cate)) return i;
	    	}
	    	return -1;
	    }

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

	        final ParsedData page = items.get(position);
	        if (page != null) {
		        TextView handleSpend = (TextView)v.findViewById(R.id.handle_spend);
		        TextView handleDate = (TextView)v.findViewById(R.id.handle_date);
		        TextView handleDetail = (TextView)v.findViewById(R.id.handle_detail);
		        Spinner handleCategory = (Spinner)v.findViewById(R.id.handle_category);
		        
//		        Log.e("HandleReceivedSms","position: "+position+" : "+page+",,,,"+handleSpend);
		        // Spend and Date
		        handleSpend.setText("���: " + page.getSpent()+ " ��");
		        handleDate.setText("����: " + DateFormat.getDateFormat(context).format(page.getDate()));
		        
		        // Detail
//		        Log.e("HANDLE_DETAIL","..."+handleDetail.getText().toString());
		        handleDetail.setText(page.getDetail());
		        handleDetail.setOnClickListener(new DetailClick(position));
//	        	handleDetail.addTextChangedListener(new DetailWatcher(position,handleDetail));
		        
		        // Category
		        ArrayAdapter adapter = ArrayAdapter
		        		.createFromResource(HandleParsedData.this,
		        				R.array.category,
		        				android.R.layout.simple_spinner_item);
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        handleCategory.setAdapter(adapter);
		        handleCategory.setSelection(findCategoryIndex(page.getCategory()));
		        handleCategory.setOnItemSelectedListener(new CategorySelectedListener(position,handleCategory));
	        }
	        v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					deleted_data = page;
					Toast.makeText(HandleParsedData.this, "clicked", Toast.LENGTH_SHORT).show();
				}
			});
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
		
		LayoutInflater inflator = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflator.inflate(R.layout.detail_dialog, (ViewGroup)findViewById(R.id.layout_root));
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		final EditText detail = (EditText)layout.findViewById(R.id.dialogDetailText);
		detail.setText(number);
		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				data_handled.get(in).setDetail(detail.getText().toString());
				myAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("���", new DialogInterface.OnClickListener() {
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
	
	private void newParsedDataDialog() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		
		LayoutInflater inflator = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflator.inflate(R.layout.new_parsed_data, (ViewGroup)findViewById(R.id.layout_root));
		
		final Date date = chosenDate;
		TextView dateView = (TextView)layout.findViewById(R.id.new_date);
		dateView.setText(DateFormat.getDateFormat(this).format(date));
		final EditText spendView = (EditText)layout.findViewById(R.id.new_spend);
		spendView.setText("0");
		final EditText detailView = (EditText)layout.findViewById(R.id.new_detail);
		detailView.setText("���ݻ��");
		final Spinner categoryView = (Spinner)layout.findViewById(R.id.new_category);
        ArrayAdapter adapter = ArrayAdapter
        		.createFromResource(this,
        				R.array.category,
        				android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryView.setAdapter(adapter);
        
		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ParsedData pd = new ParsedData(Integer.parseInt(spendView.getText().toString()),1,
						categoryView.getSelectedItem().toString(), date, detailView.getText().toString());
				data_handled.add(pd);
				created_datas.add(pd);
				myAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("���", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
}