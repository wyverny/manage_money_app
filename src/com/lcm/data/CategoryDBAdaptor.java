package com.lcm.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CategoryDBAdaptor {
	public static final String DATABASE_NAME = "Category";
	public static final String DATABASE_TABLE = "category_list";
	public static int DATABASE_VERSION = 1;
	// below is for columns in the database
	public static final String KEY_ID = "_id";
	public static final String KEY_DETAIL = "detail";
	public static final String KEY_CATEGORY = "category";
	// end of columns definition
	public static final String UNKNOWN = "__Unknown";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context ctx;
	
	private static final String DB_CREATE = "create table "+ DATABASE_TABLE +" ("
		+ KEY_ID + " integer, "
		+ KEY_DETAIL + " text primary key, "
		+ KEY_CATEGORY + " text not null);";
	private static final String TAG = "CategoryDBAdaptor";
	
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
	public CategoryDBAdaptor(Context ctx) {
		this.ctx = ctx;
	}
	
	public CategoryDBAdaptor open() throws SQLException {
		mDbHelper = new DatabaseHelper(ctx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	// when data is inserted manually
	public long insertDB(String detail, String category) {
		ContentValues cv = new ContentValues();
		if(detail!=null) cv.put(KEY_DETAIL, detail);
		else cv.put(KEY_DETAIL, "");
		if(category!=null) cv.put(KEY_CATEGORY, category);
		else cv.put(KEY_CATEGORY, UNKNOWN);
//		Log.i("db_log","in createDB");
//		System.out.println(cv.toString());
		return mDb.insert(DATABASE_TABLE, null, cv);
	}
	
	public boolean deleteDB(String detail) {
		return mDb.delete(DATABASE_TABLE, 
				KEY_DETAIL + "=\"" + detail + "\"", null) > 0;
	}
	
	public Cursor fetchAllDB() {
		return mDb.query(DATABASE_TABLE, 
				new String[] {KEY_DETAIL, KEY_CATEGORY},
				null, null, null, null, null);
	}
	
	/**
	 * arg should be either KEY_DETAIL or KEY_CATEGORY
	 */
	public Cursor fetchDB(String arg, String value) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, 
				new String[] {KEY_DETAIL, KEY_CATEGORY}, 
				arg + "=\"" + value + "\"",
				null, null, null,null,null);
		if(mCursor!=null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public boolean isDataExist(String detail) {
		boolean exist = false;
		
		Cursor mCursor = mDb.query(DATABASE_TABLE, 
				new String[] {KEY_DETAIL, KEY_CATEGORY}, 
						KEY_DETAIL + " =\"" + detail + "\"",
				null, null,null,null);
		if(mCursor.getCount()!=0) {
			exist = true;
		}
		mCursor.close();
		return exist;
	}
	
	public boolean updateDB(String category, String detail) {
		ContentValues args = new ContentValues();
//		args.put(KEY_DETAIL, detail);
		args.put(KEY_CATEGORY, category);
		
		return mDb.update(DATABASE_TABLE, args, 
				KEY_DETAIL + "=" + detail, null) > 0;
		
	}
}
