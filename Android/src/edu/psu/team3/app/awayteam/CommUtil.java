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
	public static int CreateNewUser(Context context, String username,
			String password, String firstName, String lastName, String email,
			String phone, String ePhone) {
		String url = "https://api.awayteam.redshrt.com/user/CreateUser";

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("loginId", username));
		pairs.add(new BasicNameValuePair("password", password));
		pairs.add(new BasicNameValuePair("firstName", firstName));
		pairs.add(new BasicNameValuePair("lastName", lastName));
		pairs.add(new BasicNameValuePair("email", email));
		pairs.add(new BasicNameValuePair("cellPhone", phone));
		if (ePhone == null) {
			pairs.add(new BasicNameValuePair("emergencyPhone", ""));
		} else {
			pairs.add(new BasicNameValuePair("emergencyPhone", ePhone));
		}

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
					// Authenticate user to store secret info from server

					int authSuccess = LoginUser(context, username, password,
							true);
					if (authSuccess == 1) {
						return 1;
					} else {
						return 0;
					}
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

	// Attempts to authenticate user - for use when user session is already
	// stored
	// INPUT: context, username, password
	// Returns int code based on success:
	// 1 = success! User logged in
	// 0 = unknown error or connection error
	// -1 = username not found
	// -2 = password incorrect
	public static int AuthenticateUser(Context context, String username,
			String password) {
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
				// collect secret to store with session data
				String userID = result.get("userIdentifier").toString();
				String userSecret = result.getString("userSecret").toString();
				Log.v("login", "User Identifier from Server: " + userID);
				Log.v("login", "User Secret from Server: " + userSecret);

				// save values to user session
				UserSession.getInstance(context).setUp(userID, userSecret);

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

	// Attempts to log in user
	// INPUT: context, username, password, and the value of "remember me"
	// Returns int code based on success:
	// 1 = success! User logged in
	// 0 = unknown error or connection error
	// -1 = username not found
	// -2 = password incorrect
	public static int LoginUser(Context context, String username,
			String password, boolean remember) {
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
				// collect secret to store with session data
				String userID = result.get("userIdentifier").toString();
				String userSecret = result.getString("userSecret").toString();
				Log.v("login", "User Identifier from Server: " + userID);
				Log.v("login", "User Secret from Server: " + userSecret);

				// save all values to user session
				UserSession.getInstance(context).setUp(username, password,
						userID, userSecret, remember);

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

	// Changes the password of the selected user
	// INPUTS: username - user readable login ID
	// newPass - new password
	// RETURN: 1 = success, password updated
	// 0 = unknown error or connection failure
	// -1 = username not found
	// -2 = username missing
	public static int ChangePassword(String username, String newPass,
			Context context) {
		String url = "https://api.awayteam.redshrt.com/user/changepassword";
		List<NameValuePair> pairs = null;
		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		pairs = UserSession.getInstance(context).createHash();
		pairs.add(new BasicNameValuePair("loginId", username));
		pairs.add(new BasicNameValuePair("newPassword", newPass));
		JSONObject result = null;
		Log.v("COMM", "sending request to network " + url);
		for (NameValuePair pair : pairs) {
			Log.v("COMM", "  pair contents: " + pair.toString());
		}

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			Log.v("COMM", "Password results: " + result.toString());
			if (result.getString("response").equals("success")) {
				// Update user session password
				UserSession s = UserSession.getInstance(context);
				s.changePassword(newPass);
				// reauthenticate with new credentials
				int authSuccess = AuthenticateUser(context, s.getUsername(),
						s.getPassword());
				if (authSuccess == 1) {
					return 1;
				} else {
					return 0;
				}
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
	
	//Create a new team
	//INPUTS: team name, description, location name, lat and lon, managed
	//RETURN: 1 = success, team created
	// 0 = unknown error or connection failure
	// -1 = team name already used
	public static int CreateTeam(Context context, String teamName,
			String locationName, String description, int lat, int lon, boolean managed) {
		//TODO: update with correct URI
		String url = "https://api.awayteam.redshrt.com/team/CreateTeam";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		List<NameValuePair> pairs = UserSession.getInstance(context).createHash();
		pairs.add(new BasicNameValuePair("loginId", UserSession.getInstance(context).getUsername()));
		pairs.add(new BasicNameValuePair("teamName", teamName));
		pairs.add(new BasicNameValuePair("description", description));
		pairs.add(new BasicNameValuePair("locationName", locationName));
		pairs.add(new BasicNameValuePair("locationLat", Integer.toString(lat)));
		pairs.add(new BasicNameValuePair("locationLon", Integer.toString(lon)));
		pairs.add(new BasicNameValuePair("managed", Boolean.toString(managed)));

		JSONObject result = null;
		Log.v("COMM", "sending request to network " + url);

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			Log.v("COMM", "Create Team results: " + result.toString());
			if (result.getString("response").equals("success")) {
				// TODO: collect team id and name - update user session

				return 1;
			} else if (result.getString("response").equals("failure")) {
				switch (result.getString("message")) {
				case "Team Name Already Used":
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
}
