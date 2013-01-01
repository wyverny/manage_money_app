package com.lcm.view;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.util.Log;

public class Util {
	private static final String TAG = "Util";
	public static final int FROM = 0;
	public static final int THROUGH = 1;
	public static final int TO = 2;

	public Calendar[] getFromTo(Calendar today, int accountingDate) {
		Calendar fromCalendar = Calendar.getInstance();
		Calendar turningCalendar = Calendar.getInstance();
		Calendar toCalendar = Calendar.getInstance();
		if(today.get(Calendar.DATE) < accountingDate) {
			toCalendar.add(Calendar.DATE,accountingDate-toCalendar.get(Calendar.DATE));
			fromCalendar = (Calendar)toCalendar.clone();
			fromCalendar.add(Calendar.MONTH, -1);
		} else {
			fromCalendar.add(Calendar.DATE, fromCalendar.DATE - accountingDate);
			toCalendar = (Calendar)fromCalendar.clone();
			toCalendar.add(Calendar.MONTH, 1);
		}
		
		turningCalendar = (Calendar)toCalendar.clone();
		turningCalendar.add(Calendar.DATE, -accountingDate);
		
		toCalendar.set(Calendar.HOUR, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);
		
		return new Calendar[] {fromCalendar, turningCalendar, toCalendar};
	}
	
	/**
	 * @return this method returns three values in Date array; from, to, and turning respectively
	 * in a month period, from, to, and turning means follow
	 * From: starting date
	 * To: a month later from From date
	 * Turning: when month period goes to next month, it gives the info when From date's month ends
	 * 		otherwise it is the same with To date
	 */
	/*
	public Calendar[] getFromTo(int year, int month, int fromValue) {
		// getting a month period to acquire data from database. Date[0] is from, Date[1] is to.
		GregorianCalendar fromDate = new GregorianCalendar(year,month,fromValue); //fromDate.setDate(fromValue);
		
		// calculating to date
		GregorianCalendar gc = new GregorianCalendar(year,month,fromValue);
		int toValue = (fromValue==1)? gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) : fromValue-1;
		int toMonth, toYear;
		
		if(fromValue!=1) {
			toMonth = (fromDate.get(GregorianCalendar.MONTH)==11)? 0:fromDate.get(GregorianCalendar.MONTH)+1;
			toYear = (fromDate.get(GregorianCalendar.MONTH)==11)? 1:0;
			System.out.println(toYear + " " + fromDate.get(GregorianCalendar.YEAR));
			toYear += fromDate.get(GregorianCalendar.YEAR);
		} else {
			toMonth = fromDate.get(GregorianCalendar.MONTH);
			toYear = fromDate.get(GregorianCalendar.YEAR);
		}
		GregorianCalendar toDate = new GregorianCalendar(toYear, toMonth, toValue);
//		GregorianCalendar cloneDate = (GregorianCalendar)toDate.clone();
//		cloneDate.add(GregorianCalendar.DATE, 1);
//		Log.e(TAG,new Date(toDate.getTimeInMillis()).toString());
//		Log.e(TAG,new Date(cloneDate.getTimeInMillis()).toString());
		toDate.add(GregorianCalendar.DATE, 1);
		//restarting point should be notified
		GregorianCalendar turningPoint = null;
		if(fromValue==1) {
			turningPoint = toDate;
		} else {
			gc.set(GregorianCalendar.DATE, gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			turningPoint = gc;
		}
		
		return new GregorianCalendar[]{fromDate, turningPoint, toDate};
	}
	*/
	
	public static boolean isWeekEnd(Calendar dayFromTodayToLast) {
		return dayFromTodayToLast.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				dayFromTodayToLast.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}
}
