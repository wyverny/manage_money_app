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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
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

import com.lcm.data.ParsedData;
import com.lcm.data.control.ParsedDataManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class NewMonetaInteract {
	private static final String TAG = "NewMonetaInteract";
	
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
	
	ArrayList<ParsedData> parsedDatas;
	
	private Context context;
	
	public NewMonetaInteract(Context context,ArrayList<ParsedData> parsedDatas) {
		httpContext = new BasicHttpContext();
		this.context = context;
		this.parsedDatas = parsedDatas;
	}

	public void uploadParsedDatas(String id, String pw) throws Exception {
		LoginTask loginTask = new LoginTask();
		loginTask.execute(id,pw);
	}
	
	private class LoginTask extends AsyncTask<String, Void, Boolean> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean isLoggedIn = false;
			ParsedDataManager pdm = ParsedDataManager.getParsedDataManager(context);
			try {
				isLoggedIn = login(params[0],params[1]);
//				if(!isLoggedIn)
//					return false;
				
				for (ParsedData parsedData : parsedDatas) {
					if(parsedData.isFlag()) {
						if(connectToMonetaWritePage(parsedData)) {
							Log.e(TAG,"Uploaded:" + parsedData.toString());
							parsedData.setUploaded(1);
							pdm.updateParsedData(parsedData);
						}
						else
							return false;
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				logout();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
//			Log.e("NewMonetaInteract","onPostExcute comes here");
			if(result)
				Toast.makeText(context, "업로드를 완료하였습니다.", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(context, "업로드 중 문제가 발생하였습니다.\n연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
			context.sendBroadcast(new Intent(WebUpdateListActivity.ACTION_UPDATE_WEBLIST)); // to update WebUpdateListActivity
		}

		public boolean login(String id, String pw) throws Exception {

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair(PARAM_ID, id));
			nvps.add(new BasicNameValuePair(PARAM_PWD, pw));
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
			mParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
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
			return loggedIn;
		}
	
		public boolean connectToMonetaWritePage(ParsedData parsedData) throws Exception {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			MonetaUtil monetaUtil = new MonetaUtil(context);
			nvps.add(new BasicNameValuePair("regDate", DateFormat.format("yyyyMMdd",parsedData.getDate()).toString()));  //일자
			nvps.add(new BasicNameValuePair("cashClsfy", "2")); // ??
			nvps.add(new BasicNameValuePair("goodsCd", "cash"));  //종류
			nvps.add(new BasicNameValuePair("remark", parsedData.getDetail())); //내역
			nvps.add(new BasicNameValuePair("amount", ""+parsedData.getSpent())); //금액
			nvps.add(new BasicNameValuePair("itemCode", monetaUtil.getCategoryValue(parsedData.getCategory()))); //분류
			nvps.add(new BasicNameValuePair("note", "Money Tracker")); //비고

			HttpEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
			} catch (final UnsupportedEncodingException e) {throw new AssertionError(e);}
			HttpPost httpPost = new HttpPost(WRITE_ACTION);
			httpPost.setHeader(entity.getContentType());
			httpPost.setEntity(entity);
			
//			httpClient.setRedirectHandler(new RedirectHandler() {
//				@Override
//				public boolean isRedirectRequested(HttpResponse arg0, HttpContext arg1) {
//					return false;
//				}
//				@Override
//				public URI getLocationURI(HttpResponse arg0, HttpContext arg1)
//						throws ProtocolException {
//					return URI.create("");
//				}
//			});
			
			response = httpClient.execute(httpPost, httpContext);
			entity = response.getEntity();
			
			if (entity != null) {
	            entity.consumeContent();
	        }
			
			if(response.getStatusLine().getStatusCode()==200)
				return true;
			return false;
		}
		
		public void logout() {
			HttpGet httpget = new HttpGet(
					"http://member.moneta.co.kr/Auth/logout.jsp?"
							+ "style=mMiga&returlURL=http://mmini.moneta.co.kr/cashbook/intro.do");

			try {
				response = httpClient.execute(httpget, httpContext);
				entity = response.getEntity();
				if (entity != null) {
		            entity.consumeContent();
		        }
			} catch(Exception e) {
				e.printStackTrace();
			}
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpClient.getConnectionManager().shutdown();
		}
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
