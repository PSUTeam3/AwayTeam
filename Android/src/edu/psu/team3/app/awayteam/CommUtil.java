package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import edu.psu.team3.app.awayteam.NetworkTasks;

public class CommUtil {

	// Check if username is already being used
	// Returns: 0 = error
	// 1 = available
	// 2 = NOT available
	public static int LoginIDExist(Context context, String username) {
		String url = "https://api.awayteam.redshrt.com/user/LoginIDExist?loginId="
				+ username;

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}
		JSONObject result = null;
		Log.v("COMM", "sending request to network " + url);
		// collect the results
		try {
			result = NetworkTasks.RequestData(false, url, null);
			Log.v("COMM", "Exists results: " + result.toString());
			if (result.getString("response").equals("success")) {
				if (result.getString("message").equals("available")) {
					// success & available
					return 1;
				} else {
					// success, but not available
					return 2;
				}
			} else {
				// returned failure, an error occured
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// Attempts to create a new user
	// INPUT: pairs = a name=value pairs list containing values for:
	// "loginId","email","password",
	// "firstName","lastName","cellPhone","emergencyPhone"
	// Returns int code based on success:
	// 1 = success! User created
	// 0 = unknown error or connection error
	// -999 = username already used
	// -998 = email already used
	public static int CreateNewUser(Context context, List<NameValuePair> pairs) {
		String url = "https://api.awayteam.redshrt.com/user/CreateUser";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		JSONObject result = null;
		Log.v("COMM", "sending request to network " + url);

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			Log.v("COMM", "Create results: " + result.toString());
			if (result.getString("response").equals("success")) {
				// connection was good
				Log.v("Comm",
						"result string interpreted as: "
								+ Integer.parseInt(result.getString("message")));

				if (Integer.parseInt(result.getString("message")) > 0) {
					// User created and database ID returned
					// TODO: collect user secret and store it with the user data
					return 1;
				}
			} else if (!result.getString("message").isEmpty()) {
				// Return the error from the server - make sure these error
				// codes don't change because the UI will handle them as
				// expected in the original API
				return Integer.parseInt(result.getString("message"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// Attempts to log in user
	// INPUT: context, username, password, and the value of "remember me"
	// Returns int code based on success:
	// 1 = success! User logged in
	// 0 = unknown error or connection error
	// -1 = username not found
	// -2 = password incorrect
	public static int AuthenticateUser(Context context,
			String username, String password, boolean remember) {
		String url = "https://api.awayteam.redshrt.com/user/AuthenticatePassword";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("loginId", username));
		pairs.add(new BasicNameValuePair("password", password));
		JSONObject result = null;
		Log.v("COMM", "sending request to network " + url);

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			Log.v("COMM", "Login results: " + result.toString());
			if (result.getString("response").equals("success")) {
				//collect secret to store with session data
				String userID = result.get("userIdentifier").toString();
				String userSecret = result.getString("userSecret").toString();
				Log.v("login","User Identifier from Server: "+userID);
				Log.v("login","User Secret from Server: "+userSecret);
				
				UserSession.getInstance().setUp(username, password, userID, userSecret, remember);
				
				return 1;
			} else if (result.getString("response").equals("failure")) {
				switch (result.getString("message")) {
				case "bad password":
					return -2;
				case "password not submitted":
					return -2;
				case "user not found":
					return -1;
				case "user not submitted":
					return -1;
				default:
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	//Changes the password of the selected user
	//INPUTS: 	username - user readable login ID
	//			newPass - new password
	//RETURN: 	1 = success, password updated
	//			0 = unknown error or connection failure
	//			-1 = username not found
	//			-2 = username missing
	public static int ChangePassword(String username, String newPass, Context context){
		String url = "https://api.awayteam.redshrt.com/user/changepassword";
		List<NameValuePair> pairs = null;
		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}
		
		pairs = UserSession.getInstance().createHash();
		pairs.add(new BasicNameValuePair("loginId",username));
		pairs.add(new BasicNameValuePair("newPassword",newPass));
		JSONObject result = null;
		Log.v("COMM", "sending request to network " + url);
		for (NameValuePair pair :pairs){
			Log.v("COMM", "  pair contents: "+pair.toString());
		}

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			Log.v("COMM", "Password results: " + result.toString());
			if (result.getString("response").equals("success")) {
				return 1;
			} else if (result.getString("response").equals("failure")) {
				switch (result.getString("message")) {
				case "user not found":
					return -1;
				case "user not submitted":
					return -2;
				default:
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
}
