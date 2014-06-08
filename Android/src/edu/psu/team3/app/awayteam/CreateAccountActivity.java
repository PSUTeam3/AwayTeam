package edu.psu.team3.app.awayteam;


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

	private boolean toggle = true; // value to toggle for testing

	private boolean checkUsername(String username) {
		Log.d("AT", "entered check process");
		// TODO: send username to server
		boolean unique = toggle;
		toggle = !toggle;

		if (unique) {
			Drawable icn = getResources().getDrawable(R.drawable.green_check);
			icn.setBounds(new Rect(0, 0, 50, 50));
			mUsernameView.setError("Username Available!", icn);
			return true;
		} else {
			mUsernameView.setError("Username Taken");
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

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword1 = mPassword1View.getText().toString();
		mPassword2 = mPassword2View.getText().toString();

		boolean cancel = false;
		View focusView = null;

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
		if (!mPassword1.equals(mPassword2)) {
			mPassword2View.setError("Passwords must match");
			focusView = mPassword2View;
			cancel = true;
		}

		// Check for a valid username
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		} else if (!checkUsername(mUsername)) {
			mUsernameView.setError("Username is already taken");
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
			mStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserAccountTask();
			mAuthTask.execute((Void) null);
			// Show interface since we don't know how to log in yet
			// TODO: implement login
			startActivity(new Intent(this, DisplayActivity.class));
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

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserAccountTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mUsername)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword1);
				}
			}

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPassword1View
						.setError(getString(R.string.error_incorrect_password));
				mPassword1View.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

}
