package edu.psu.team3.app.awayteam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_USERNAME = "com.example.android.authenticatordemo.extra.USERNAME";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
		mUsernameView = (EditText) findViewById(R.id.username_entry);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password_entry);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		// Initialize Buttons
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		findViewById(R.id.create_account_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(LoginActivity.this,
								CreateAccountActivity.class));
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_forgot_password) {
			// TODO:implement
			Toast.makeText(LoginActivity.this,
					"A Temporary Password has been sent to your Email",
					Toast.LENGTH_LONG).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
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
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			// Create Login Request
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
				pairs.add(new BasicNameValuePair("password", mPassword));

				String url = "https://api.awayteam.redshrt.com/user/AuthenticatePassword";

				Log.v("Pairs", pairs.toString());

				mAuthTask.execute(url, pairs);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
				String response = mAuthTask.get().getString("response");
				Log.v("Decision","Response from AsyncTask = "+response);
				if(response.equals("success")){
					startActivity(new Intent(this, DisplayActivity.class));
				}else{
					//TODO: move failure handling here
					
				}
			}catch(Exception e) {
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

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * An asynchronous login/registration task used to authenticate the user.
	 */
	public class UserLoginTask extends AsyncTask<Object, Void, JSONObject> {

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

				// Make success
			} else if (response.equals("failure")) {
				if (success.optString("message").equals("bad password")) {
					mPasswordView
							.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
				} else {
					mUsernameView.setError("Username Not Found");
					mUsernameView.requestFocus();
				}

				Toast.makeText(getBaseContext(), success.optString("message"),
						Toast.LENGTH_LONG).show();
				// fail
			}
			//finish();

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
