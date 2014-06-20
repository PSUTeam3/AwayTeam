package edu.psu.team3.app.awayteam;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.json.JSONObject;

import android.util.Log;
import edu.psu.team3.app.awayteam.NetworkTasks;

//TODO: build this up with all the comm functions in one place.
//make sure comm functions are divorced from the interface
public class CommUtil {

	// Check if username is already being used
	// Returns: 0 = error
	// 1 = available
	// 2 = NOT available
	public static int LoginIDExist(String username) {
		String url = "https://api.awayteam.redshrt.com/user/LoginIDExist?loginId="
				+ username;

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

}
