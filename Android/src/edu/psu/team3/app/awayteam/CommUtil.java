package edu.psu.team3.app.awayteam;

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
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import edu.psu.team3.app.awayteam.NetworkTasks;

//TODO: build this up with all the comm functions in one place.
//make sure comm functions are divorced from the interface
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

		try{
			//TODO: write in errors
			result = NetworkTasks.RequestData(true, url, pairs);
			Log.v("COMM", "Create results: " + result.toString());
			if (result.getString("response").equals("success")) {
				return 1;  //TODO: expand on these possibilities
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return 0;
	}

}
