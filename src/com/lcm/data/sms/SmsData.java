package com.lcm.data.sms;

import android.text.format.DateFormat;

public class SmsData {
	String body, addr;
	String date;
	int sms_Id;
	boolean load;

	public SmsData(String body, String addr, String date, int sms_Id) {
		this.body = body;
		this.addr = addr;
		this.date = date;
		this.sms_Id = sms_Id;
		this.load = false;
	}
	
	public SmsData(String body, String addr, String date, int sms_Id, boolean load) {
		this.body = body;
		this.addr = addr;
		this.date = date;
		this.sms_Id = sms_Id;
		this.load = load;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getDate() {
		return date;
	}
	
	public String getDateFormat() {
		return DateFormat.format("MMM dd, yyyy h:mmaa",Long.parseLong(date)).toString();
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isLoad() {
		return load;
	}

	public void setLoad(boolean load) {
		this.load = load;
	}

	public int getSms_Id() {
		return sms_Id;
	}

	public void setSms_Id(int sms_Id) {
		this.sms_Id = sms_Id;
	}

	@Override
	public String toString() {
		return "date: " + date + " addr: " + addr + " body: " + body;
	}

}
