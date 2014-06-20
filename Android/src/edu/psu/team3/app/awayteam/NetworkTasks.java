package edu.psu.team3.app.awayteam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class NetworkTasks {

	//check for network availability
	public static boolean NetworkAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			// TODO: look into possibility of a toast here
			Toast.makeText(context, "No Network Connection", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public static JSONObject RequestData(boolean actionPost, String url,
			List<NameValuePair> pairs) {
		if (url.contains("https://")) {
			doHTTPS();
		}
		// Create variables to hold content
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		// determine if the action is a get or post
		HttpPost post = null;
		HttpGet get = null;
		HttpResponse response = null;
		if (actionPost) {
			post = new HttpPost(url);
		} else {
			get = new HttpGet(url);
		}

		try {
			// build&send request based on role
			if (actionPost) {
				post.setEntity(new UrlEncodedFormEntity(pairs));
				Log.v("POST", post.getURI().toString());
				response = client.execute(post);
			} else {
				Log.v("GET", get.getURI().toString());
				response = client.execute(get);
			}

			// collect reply
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200 || statusCode == 401) {
				// FYI 200 is good - auth passed
				// 401 is bad - auth failed
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				Log.v("NetTask", "Returned Message: " + builder.toString()); // response
				// data
			} else {
				Log.e("NetTask", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Create JSON object to return
		JSONObject js = null;
		try {
			js = new JSONObject(builder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return js;
	}

	// all this is required to accept a HTTP SSL Certificate
	private static void doHTTPS() {
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		DefaultHttpClient client = new DefaultHttpClient();
		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory
				.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		SingleClientConnManager mgr = new SingleClientConnManager(
				client.getParams(), registry);
		DefaultHttpClient httpClient = new DefaultHttpClient(mgr,
				client.getParams());
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	}

}
