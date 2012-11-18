package com.lcm.web;

import com.lcm.smsSmini.R;

import android.content.Context;

public class MonetaUtil {
	Context context;
	String[] cateChild;
	String[] cateChildValue;
	
	public MonetaUtil(Context context) {
		this.context = context;
		cateChild = context.getResources().getStringArray(R.array.category);
		cateChildValue = context.getResources().getStringArray(R.array.category_child);
	}
	
	public String getCategoryValue(String cate) {
		int index = -1; 
		for(int i=0; i<cateChild.length; i++) {
			if(cateChild[i].equals(cate)) {
				index = i;
				break;
			}
		}
		if(index==-1)
			return cateChildValue[0];
		return cateChildValue[index];
	}
}
