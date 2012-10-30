package com.lcm.web;

/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
//import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class MonetaInteract {
	private static final String PARAM_ID = "custId";
	private static final String PARAM_PWD = "passwd";
	private static final String AUTH_URI = "https://member.moneta.co.kr/Auth/SmLoginAuth.jsp";
	private static final String WRITE_PAGE = "http://mmini.moneta.co.kr/cashbook/write/pay/write.do";
	private static final String WRITE_ACTION = "http://mmini.moneta.co.kr/cashbook/write/pay/write-action.do";
	private static final int REGISTRATION_TIMEOUT = 30 * 1000;
	
	private DefaultHttpClient httpClient;
	private HttpContext httpContext;
	private HttpResponse response;
	private HttpEntity entity;
	private boolean loggedIn = false;
	private String custid;
	private String passwd;
	
	public MonetaInteract(String c, String p) {
		custid = c;
		passwd = p;
		httpContext = new BasicHttpContext();
		try {
			login();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void login() throws Exception {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(PARAM_ID, custid));
		nvps.add(new BasicNameValuePair(PARAM_PWD, passwd));
		nvps.add(new BasicNameValuePair("returnURL",
				"http://mmini.moneta.co.kr/cashbook/write/pay/list.do"));
		nvps.add(new BasicNameValuePair("event_cd", ""));
		nvps.add(new BasicNameValuePair("majority", "N"));
		nvps.add(new BasicNameValuePair("style", "mMiga"));
		nvps.add(new BasicNameValuePair("ipOnOff", "ipOff"));

		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
		} catch(final UnsupportedEncodingException e) {throw new AssertionError(e);}
		HttpPost httpPost = new HttpPost(AUTH_URI);
		httpPost.setHeader(entity.getContentType());
		httpPost.setEntity(entity);

		httpClient = new DefaultHttpClient();
		final HttpParams mParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(mParams,REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(mParams, REGISTRATION_TIMEOUT);
		ConnManagerParams.setTimeout(mParams, REGISTRATION_TIMEOUT);
		
		try {		
			response = httpClient.execute(httpPost, httpContext);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
	            entity.consumeContent();
	            
	            if(httpClient.getCookieStore().getCookies().size() < 6) loggedIn = true;
				else loggedIn = false;
	        } else {
	        	loggedIn = false;
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Log.v("MONETA","getAuthtoken completing");
		}
	}
	
	public void connectToMonetaWritePage() throws Exception {
		HttpPost httpPost = new HttpPost(WRITE_ACTION);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("regDate", "20110101"));  //일자
		nvps.add(new BasicNameValuePair("cashClsfy", "2")); // ??
		nvps.add(new BasicNameValuePair("goodsCd", ""));  //종류
		nvps.add(new BasicNameValuePair("remark", "this is for a test")); //내역
		nvps.add(new BasicNameValuePair("amount", "19800")); //금액
		nvps.add(new BasicNameValuePair("itemCode", "10013")); //분류
		nvps.add(new BasicNameValuePair("note", "hello")); //비고

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		response = httpClient.execute(httpPost, httpContext);
		entity = response.getEntity();
		if (entity != null) {
            entity.consumeContent();
        }
	}

	public void logout() throws Exception {
		HttpGet httpget = new HttpGet(
				"http://member.moneta.co.kr/Auth/logout.jsp?"
						+ "style=mMiga&returlURL=http://mmini.moneta.co.kr/cashbook/intro.do");

		response = httpClient.execute(httpget, httpContext);
		entity = response.getEntity();
		if (entity != null) {
            entity.consumeContent();
        }
		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpClient.getConnectionManager().shutdown();
	}
	
	public void getInfoFromMonetaPage() throws Exception {
		HttpGet httpget = new HttpGet(WRITE_PAGE);
		response = httpClient.execute(httpget,httpContext);
		entity = response.getEntity();
		
		Document docs = Jsoup.parse(entity.getContent(), "UTF-8", WRITE_PAGE);
		Elements elements = docs.select("#cashCardSelect option");
		
		for(Element e : elements) {
			//TODO: store this variable as well as category data.
			e.text(); e.val();
		}
	}
}
