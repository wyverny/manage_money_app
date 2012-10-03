package com.lcm.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SMSDbAdapter {
	public static final String DATABASE_NAME = "smsApp";
	public static final String DATABASE_TABLE = "smsList";
	public static int DATABASE_VERSION = 1;
	// below is for columns in the database
	// unique part
	public static final String KEY_ID = "_id";
	public static final String KEY_TIME = "time"; // this can be the time a sms has come or the time user inserted
	// raw sms data part
	public static final String KEY_ADDRESS = "addr";
	public static final String KEY_BODY = "body";
	public static final String KEY_UPLOAD = "upload";
	// parsed data part
	public static final String KEY_SPENT = "spent";
	public static final String KEY_CATEGORY = "category";
	// end of columns definition
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context ctx;
	
	private static final String DB_CREATE = "create table smslist ("
		+ KEY_ID + " integer primary key autoincrement, "
		+ KEY_TIME + " text not null, "
		+ KEY_ADDRESS + " text, "
		+ KEY_BODY + " text, "
		+ KEY_UPLOAD + " integer"
		+ KEY_SPENT + " integer"
		+ KEY_CATEGORY + " text);";
	
	public static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO: ALL saved sms list should be put back into the inbox list
		}
	}

	
	// basic functions for initialization and annihilation
	public SMSDbAdapter(Context ctx) {
		this.ctx = ctx;
	}
	
	public SMSDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(ctx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	//operations related to database; for the data from sms
	public long createDB(String time, String body, String addr, boolean upload, int spent, String category) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TIME, time);
		cv.put(KEY_ADDRESS, addr);
		cv.put(KEY_BODY, body);
		int load = (upload) ? 1 : 0;
		cv.put(KEY_UPLOAD, load);
		cv.put(KEY_SPENT, spent);
		cv.put(KEY_CATEGORY, category);
		Log.i("db_log","in createDB");
//		System.out.println(cv.toString());
		return mDb.insert(DATABASE_TABLE, null, cv);
	}
	
	//operations related to database; for the data from sms // this shouldn't be used
	/*public long createDB(String time, String body, String addr, boolean upload) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TIME, time);
		cv.put(KEY_ADDRESS, addr);
		cv.put(KEY_BODY, body);
		int load = (upload) ? 1 : 0;
		cv.put(KEY_UPLOAD, load);
		Log.i("db_log","in createDB");
//		System.out.println(cv.toString());
		return mDb.insert(DATABASE_TABLE, null, cv);
	}*/
	
	//operations related to database; for the data from manual input
	public long createDB(String time, int spent, String category) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TIME, time);
		cv.put(KEY_SPENT, spent);
		cv.put(KEY_CATEGORY, category);
		return mDb.insert(DATABASE_TABLE, null, cv);
	}
	
	
	
	public boolean deleteSMS(String time) {
		return mDb.delete(DATABASE_TABLE, KEY_TIME + "=" + time, null) > 0;
	}
	
	public Cursor fetchAllSMS() {
		return mDb.query(DATABASE_TABLE, new String[] {KEY_TIME, KEY_BODY, KEY_ADDRESS, KEY_UPLOAD}, null, null, null, null, null);
	}
	
	public Cursor fetchSMS(String time) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, 
				new String[] {KEY_TIME, KEY_BODY, KEY_ADDRESS, KEY_UPLOAD}, 
				KEY_TIME + "=" + time, null, null, null,null,null);
		if(mCursor!=null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public boolean updateSMS(String time, String body, String address, boolean upload) {
		int load = (upload) ? 1 : 0;
		ContentValues args = new ContentValues();
		args.put(KEY_TIME, time);
		args.put(KEY_BODY, body);
		args.put(KEY_ADDRESS, address);
		args.put(KEY_UPLOAD, load);
		
		return mDb.update(DATABASE_TABLE, args, KEY_TIME + "=" + time, null) > 0;
		
	}
	
	public boolean updateSMS(int time, boolean upload) {
		ContentValues args = new ContentValues();
		int load = (upload) ? 1 : 0;
		args.put(KEY_UPLOAD, load);

		return mDb.update(DATABASE_TABLE, args, KEY_TIME + "=" + time, null) > 0;
		
	}
}
