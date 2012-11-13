package com.lcm.data.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lcm.data.control.ExpenditureDBAdaptor.DatabaseHelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {

	private static final String TAG = "ExportDatabaseCSVTask";
	Context mContext;
//	private ProgressDialog dialog;

	// can use UI thread here
	public ExportDatabaseCSVTask(Context context) {
		mContext = context;
//		dialog = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute()
	{
//		this.dialog.setMessage("Exporting database...");
//		this.dialog.show();
	}

	// automatically done on worker thread (separate from UI thread)
	protected Boolean doInBackground(final String... args) {

		// File dbFile = getDatabasePath("excerDB.db");
		DatabaseHelper DBob = new DatabaseHelper(mContext);
		File exportDir = new File(Environment.getExternalStorageDirectory(), "");

		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}

//		DateFormat dateFormat = new SimpleDateFormat("yy_MM_dd");
//		File file = new File(exportDir, "MoneyTracker_" + dateFormat.format(new Date()) + ".csv");
		File file = new File(exportDir, "MoneyTracker.csv");
		try	{
			file.createNewFile();
			String line = "שלום, hello, привет";
			CSVWriter csvWrite = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file), "euc_kr"));
			
			SQLiteDatabase db = DBob.getReadableDatabase();
			Cursor curCSV = db.rawQuery("SELECT * FROM "+ExpenditureDBAdaptor.DATABASE_TABLE, null);
			csvWrite.writeNext(curCSV.getColumnNames());
			while (curCSV.moveToNext()) {
				String arrStr[] = { curCSV.getString(0), curCSV.getString(1),
				curCSV.getString(2), curCSV.getString(3), curCSV.getString(4),
				curCSV.getString(5), curCSV.getString(6), curCSV.getString(7),
				curCSV.getString(8), curCSV.getString(9), curCSV.getString(10),
				curCSV.getString(11)};
				csvWrite.writeNext(arrStr);
			}		
		    
			csvWrite.close();
			curCSV.close();
			db.close();
			DBob.close();

			return true;
		}

		catch (SQLException sqlEx) {
			Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
			return false;
		}
		catch (IOException e) {
			Log.e("MainActivity", e.getMessage(), e);
			return false;
		}
	}

	// can use UI thread here

	@Override
	protected void onPostExecute(final Boolean success) {
//		if (this.dialog.isShowing()) {
//			this.dialog.dismiss();
//		}
		if (success) {
			Toast.makeText(mContext, "Export successful!", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(mContext, "Export failed", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * http://en.wikipedia.org/wiki/Byte_order_mark#UTF-8
	 *  I spent some time but found solution for your problem.
		First I opened notepad and wrote the following line: שלום, hello, привет Then I saved it as file he-en-ru.csv using UTF-8. Then I opened it with MS excel and everything worked well.
		Now, I wrote a simple java program that prints this line to file as following: PrintWriter w = new PrintWriter(new OutputStreamWriter(os, "UTF-8")); w.print(line); w.flush(); w.close();
		When I opened this file using excel I saw "gibrish."
		Then I tried to read content of 2 files and (as expected) saw that file generated by notepad contains 3 bytes prefix: 239 EF 187 BB 191 BF So, I modified my code to print this prefix first and the text after that:

    String line = "שלום, hello, привет";
    OutputStream os = new FileOutputStream("c:/temp/j.csv");
    os.write(239);
    os.write(187);
    os.write(191);

    PrintWriter w = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));

    w.print(line);
    w.flush();
    w.close();
And it worked! I opened the file using excel and saw text as I expected.

Bottom line: write these 3 bytes before writing the content. This prefix indicates that the content is in UTF-8.
	 */
}