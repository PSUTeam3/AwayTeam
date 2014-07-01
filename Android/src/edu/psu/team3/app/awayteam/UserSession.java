package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Base64;
import android.util.Log;

public final class UserSession {
	private static volatile UserSession INSTANCE = new UserSession();

	private String username = null; // user's username entered for login
	private String password = null; // user's password entered for login
	private String loginID = null; // user identification received from server
	private String loginSecret = null; // secret for hash from server
	private boolean rememberLogin = false; // true if we should remember the
											// user credentials and
											// automatically login again when
											// the app is restarted
	
	public Team activeTeam = new Team();

	// empty constructor to ensure only one copy of the class exists
	// - all variables will be filled in after successful authentication
	// TODO: check shared prefs for user info to give login form for immediate
	// login
	private UserSession() {

	}

	public static UserSession getInstance() {
		return INSTANCE;
	}

	// After the user successfully logs in, provide the following information to
	// the user session so that it can be used to track and authenticate the
	// user
	// INPUTS: userLoginName: the login name the user typed into the form
	// userPassword: the password the user entered to login
	// userLoginID: identification provided by the server
	// userSecret: authentication secret provided by the server
	// rememberMe: TRUE if the user has selected the option to remain logged in
	public void setUp(String userLoginName, String userPassword,
			String userLoginID, String userSecret, boolean rememberMe) {
		username = userLoginName;
		password = userPassword;
		loginID = userLoginID;
		loginSecret = userSecret;
		rememberLogin = rememberMe;

		if (rememberMe) {
			// TODO: save immediately to shared prefs
		}
	}

	// Shorter version of setUp for use when the user has saved their
	// credentials with the app
	// only the authentication user identifier and session secret from the
	// server need to be passed again to update for this session
	// all other data is already stored in the shared prefs
	public void setUp(String userLoginID, String userSecret) {
		loginID = userLoginID;
		loginSecret = userSecret;
	}

	// check if the user is remembered by the app
	public boolean remembered() {
		return rememberLogin;
	}

	// get the username for the purpose of populating login
	public String getUsername() {
		return username;
	}

	// get the password for the purpose of populating login
	public String getPassword() {
		return password;
	}

	// End the session and delete all stored data on the user
	// call this when the user selects "logout"
	public void terminateSession() {
		// TODO: clear out all the shared prefs
	}

	// Create a hash to authenticate a session
	public List<NameValuePair> createHash() {
		byte[] hash = null;
		List<NameValuePair> secret = new ArrayList<NameValuePair>();
		long mstime = System.currentTimeMillis();
		long seconds = mstime / 1000;
		String time = String.valueOf(seconds);
		String token = time + username.toLowerCase() + loginID;// combine the parts of the
													// token

		// Log.v("secret", "concatenated text: " + token);

		// hash function
		try {
			SecretKeySpec secretKey = new SecretKeySpec(loginSecret.getBytes(),
					"HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKey);
			hash = mac.doFinal(token.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Log.v("secret", "hashed secret: " + hexify(hash));
		
		secret.add(new BasicNameValuePair("AWT_AUTH", loginID));
		secret.add(new BasicNameValuePair("AWT_AUTH_CHALLENGE", hexify(hash)));

		// Log.v("secret", secret.toString());

		return secret;
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	private static String hexify(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		return new String(hexChars).toLowerCase();
	}

}
