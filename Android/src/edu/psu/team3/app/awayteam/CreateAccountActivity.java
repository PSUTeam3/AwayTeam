package edu.psu.team3.app.awayteam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.team3.app.awayteam.LoginActivity.UserLoginTask;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccountActivity extends Activity {

	private UserAccountTask mAuthTask = null;
	private IDCheckTask mIDCheck = null;

	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	// Form fields
	private String mUsername;
	private String mPassword1;
	private String mPassword2;
	private String mFirstName;
	private String mLastName;
	private String mPhone;
	private String mEmail;
	private String mEPhone;

	// UI references
	private EditText mUsernameView;
	private EditText mPassword1View;
	private EditText mPassword2View;
	private EditText mNameView;
	private EditText mPhoneView;
	private EditText mEmailView;
	private EditText mEPhoneView;
	private View mCreateFormView;
	private View mCreateStatusView;
	private TextView mStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);

		// Assign UI Elements
		mUsernameView = (EditText) findViewById(R.id.new_username_input);
		mPassword1View = (EditText) findViewById(R.id.password1_input);
		mPassword2View = (EditText) findViewById(R.id.password2_input);
		mNameView = (EditText) findViewById(R.id.name_input);
		mPhoneView = (EditText) findViewById(R.id.phone_input);
		mEmailView = (EditText) findViewById(R.id.email_input);
		mEPhoneView = (EditText) findViewById(R.id.emer_phone_input);
		mCreateFormView = findViewById(R.id.create_form);
		mCreateStatusView = findViewById(R.id.create_status);
		mStatusMessageView = (TextView) findViewById(R.id.create_status_message);

		// Assign back button
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Assign buttons
		findViewById(R.id.account_submit_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptCreate();
					}
				});
		findViewById(R.id.username_check_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mUsernameView.getText() != null) {
							mUsername = mUsernameView.getText().toString();
							Log.d("AT", "Unsername = " + mUsername);
							checkUsername(mUsername);
						} else {
							mUsernameView
									.setError(getString(R.string.error_field_required));
						}
					}
				});
	}

	// checks for a unique username
	// returns True for unique and False for taken
	private boolean checkUsername(String username) {
		Log.v("NameCheck", "entered check process");
		// TODO: try out reformatted version
		// int answer = CommUtil.LoginIDExist(username);
		// Log.v("Answer","checkUsername got this back from the CommUtil: "+answer);
		if (mIDCheck != null) {
			return false;
		}
		if (username.isEmpty()) {
			mUsernameView.setError(getResources().getString(
					R.string.error_field_required));
			return false;
		}

		mIDCheck = new IDCheckTask();

		try {
			mIDCheck.execute(mUsername);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO: unjumble this mess
		Integer answer = 0;
		try {
			answer = mIDCheck.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (answer == 1) {
			return true;
		} else {
			return false;
		}

	}

	public void attemptCreate() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPassword1View.setError(null);
		mPassword2View.setError(null);
		mEmailView.setError(null);
		mPhoneView.setError(null);
		mEPhoneView.setError(null);

		// Store values at the time of the account creation attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword1 = mPassword1View.getText().toString();
		mPassword2 = mPassword2View.getText().toString();
		mEmail = mEmailView.getText().toString();
		mPhone = mPhoneView.getText().toString();
		mEPhone = mEPhoneView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// check valid phones
		if (!TextUtils.isEmpty(mEPhone) && mEPhone.length() < 7) {
			mEPhoneView.setError("Check Format");
			focusView = mEPhoneView;
			cancel = true;
		}
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (mPhone.length() < 7) {
			mPhoneView.setError("Check Format");
			focusView = mPhoneView;
			cancel = true;
		}

		// check valid email
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError("Check Format");
			focusView = mEmailView;
			cancel = true;
		}

		// check valid name and split
		if (TextUtils.isEmpty(mNameView.getText())) {
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		} else if (mNameView.getText().toString().contains(" ")) {
			// name has more than one part, split it
			String[] nameParts = mNameView.getText().toString().split(" ");
			if (nameParts[0].contains(",")) {
				// format is Last, First X
				mLastName = nameParts[0].replace(",", "");
				mFirstName = nameParts[1];
			} else {
				// First name first
				mFirstName = nameParts[0];
				if (nameParts.length < 3) {
					mLastName = nameParts[1];
				} else {
					mLastName = nameParts[2];
				}
			}

		} else {
			mFirstName = mNameView.getText().toString();
			mLastName = "";
		}

		// Check valid password
		if (TextUtils.isEmpty(mPassword1)) {
			mPassword1View.setError(getString(R.string.error_field_required));
			focusView = mPassword1View;
			cancel = true;
		} else if (mPassword1.length() < 4) {
			mPassword1View.setError(getString(R.string.error_invalid_password));
			focusView = mPassword1View;
			cancel = true;
		}
		// check that password entries match
		if (TextUtils.isEmpty(mPassword2)) {
			mPassword2View.setError(getString(R.string.error_field_required));
			focusView = mPassword2View;
			cancel = true;
		} else if (!mPassword1.equals(mPassword2)) {
			mPassword2View.setError("Passwords must match");
			focusView = mPassword2View;
			cancel = true;
		}

		// Check for a valid username
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user account creation attempt.
			mStatusMessageView.setText("Creating New Account");
			showProgress(true);
			mAuthTask = new UserAccountTask();

			try {
				// htr =
				// makeRequest("https://api.awayteam.redshrt.com/user/AuthenticatePassword",
				// authStuff);
				// mLoginStatusMessageView.setText(htr.toString());

				java.util.logging.Logger.getLogger("httpclient.wire.header")
						.setLevel(java.util.logging.Level.FINEST);
				java.util.logging.Logger.getLogger("httpclient.wire.content")
						.setLevel(java.util.logging.Level.FINEST);

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("loginId", mUsername));
				pairs.add(new BasicNameValuePair("password", mPassword1));
				pairs.add(new BasicNameValuePair("firstName", mFirstName));
				pairs.add(new BasicNameValuePair("lastName", mLastName));
				pairs.add(new BasicNameValuePair("email", mEmail));
				pairs.add(new BasicNameValuePair("cellPhone", mPhone));
				if (mEPhone == null) {
					pairs.add(new BasicNameValuePair("emergencyPhone", ""));
				} else {
					pairs.add(new BasicNameValuePair("emergencyPhone", mEPhone));
				}

				String url = "https://api.awayteam.redshrt.com/user/CreateUser";

				Log.v("Pairs", pairs.toString());

				mAuthTask.execute(url, pairs);

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String response = mAuthTask.get().getString("response");
				Log.v("Decision", "Response from AsyncTask = " + response);
				if (response.equals("success")) {
					startActivity(new Intent(this, DisplayActivity.class));
				} else {
					// TODO: handle failure codes
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mCreateStatusView.setVisibility(View.VISIBLE);
			mCreateStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCreateStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mCreateFormView.setVisibility(View.VISIBLE);
			mCreateFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCreateFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mCreateStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mCreateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public String getLastName() {
		return mLastName;
	}

	public String getFirstName() {
		return mFirstName;
	}

	/**
	 * An asynchronous registration task used to authenticate the user.
	 */
	public class UserAccountTask extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String url = (String) params[0];
			@SuppressWarnings("unchecked")
			List<NameValuePair> pairs = (List<NameValuePair>) params[1];

			if (url.contains("https://")) {
				// all this is required to accept a HTTP SSL Certificate
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				DefaultHttpClient client = new DefaultHttpClient();
				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory
						.getSocketFactory();
				socketFactory
						.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("https", socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(
						client.getParams(), registry);
				DefaultHttpClient httpClient = new DefaultHttpClient(mgr,
						client.getParams());
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			}

			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);

			try {
				post.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse response = client.execute(post);

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
					Log.v("Getter", "Your data: " + builder.toString()); // response
																			// data
				} else {
					Log.e("Getter", "Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject js = null;

			try {
				js = new JSONObject(builder.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return js;
		}

		@Override
		protected void onPostExecute(final JSONObject success) {
			mAuthTask = null;
			showProgress(false);

			Log.v("postEx", "determine success using: " + success.toString());

			String response = null;
			try {
				response = success.getString("response");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.v("Response", "success response = " + response);

			if (response.equals("success")) {
				long mstime = System.currentTimeMillis();
				long seconds = mstime / 1000;
				String time = String.valueOf(seconds);
				Toast.makeText(
						getBaseContext(),
						"userIdentifer: \""
								+ success.optString("userIdentifier")
								+ "\" userSecret: \""
								+ success.optString("userSecret") + "\"",
						Toast.LENGTH_LONG).show();
				Toast.makeText(getBaseContext(),
						"unixTimeStamp: " + time.toString(), Toast.LENGTH_LONG)
						.show();

			} else if (response.equals("failure")) {
				// TODO: move error reporting to the activity section

				Toast.makeText(getBaseContext(), success.optString("message"),
						Toast.LENGTH_LONG).show();
			}

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	// New, shorter AsyncTask
	public class IDCheckTask extends AsyncTask<String, Void, Integer> {
		// dispatch the check to the background
		@Override
		protected Integer doInBackground(String... username) {
			Log.v("Background", "executing in background.  Input = "
					+ username[0]);
			Integer result = CommUtil.LoginIDExist(username[0]);
			Log.v("Background", "returned from commutil.  result = " + result);
			return result;
		}

		// TODO:update interface with the correct information

		@Override
		protected void onPostExecute(final Integer result) {
			switch (result) {
			case 0:
				Toast.makeText(getBaseContext(),
						"Error contacting name server", Toast.LENGTH_SHORT)
						.show();
				break;
			case 1:
				Drawable icon = getResources().getDrawable(
						R.drawable.green_check);
				icon.setBounds(new Rect(0, 0, 75, 75));
				mUsernameView.setError("Username Available!", icon);
				mUsernameView.requestFocus();
				break;
			default:
				mUsernameView.setError("Username is already taken");
				mUsernameView.requestFocus();
			}
			
			mIDCheck = null;
		}

		@Override
		protected void onCancelled() {
			mIDCheck = null;
		}
	}

}
