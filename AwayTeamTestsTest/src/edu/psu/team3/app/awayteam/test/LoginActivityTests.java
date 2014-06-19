package edu.psu.team3.app.awayteam.test;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.psu.team3.app.awayteam.*;
import edu.psu.team3.app.awayteam.R;

/**
 * JUnit Test class for the Login Activity
 * 
 */
public class LoginActivityTests extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	private LoginActivity mTestActivity;

	// Test login data
	String testPassword = "123testxyzpassword";
	String testBadPassword = "123testxyzpasswordIncorrect";
	String testUsername = "testuser1";
	String testLoginMsg = "testuser1:123testxyzpassword"; // initial test
															// message - TODO:
															// pass test
															// credentials
															// though a JSON
															// parser and get
															// actual package

	// UI Elements that will be tested
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private Button mLoginButton;
	private Button mCreateButton;

	public LoginActivityTests() {
		super(LoginActivity.class);
	}

	/**
	 * Use setUp to initialize all the UI components, instantiate the activity
	 * to test, and initialize variables
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mTestActivity = getActivity();
		// assign UI elements to variables
		mUsernameView = (EditText) mTestActivity
				.findViewById(R.id.username_entry);
		mPasswordView = (EditText) mTestActivity
				.findViewById(R.id.password_entry);
		mLoginFormView = mTestActivity.findViewById(R.id.login_form);
		mLoginStatusView = mTestActivity.findViewById(R.id.login_status);
		mLoginButton = (Button) mTestActivity.findViewById(R.id.sign_in_button);
		mCreateButton = (Button) mTestActivity
				.findViewById(R.id.create_account_button);
	}

	/**
	 * Ensure that the preconditions will lead to a successful test
	 */
	public void testPreconditions() {
		assertNotNull(mTestActivity);
	}

	/**
	 * Use this section to destroy data for the next test
	 */
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * ------------------------------------------------------------------------
	 * Test functions below:
	 */

	// Test that empty usernames are rejected
	public void testUsernameView_validation() {
		// set text
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText("");
			}
		});
		TouchUtils.clickView(this, mLoginButton);
		String textViewErrMsg = mUsernameView.getError().toString();
		assertEquals(
				textViewErrMsg,
				mTestActivity.getResources().getString(
						R.string.error_field_required));
	}

	// Test that empty and short passwords are rejected
	public void testPasswordView_validation() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPasswordView.setText("");
			}
		});

		TouchUtils.clickView(this, mLoginButton);
		String textViewErrMsg = mPasswordView.getError().toString();
		assertEquals(
				textViewErrMsg,
				mTestActivity.getResources().getString(
						R.string.error_field_required));

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPasswordView.setText("1");
			}
		});
		TouchUtils.clickView(this, mLoginButton);
		textViewErrMsg = mPasswordView.getError().toString();
		assertEquals(
				textViewErrMsg,
				mTestActivity.getResources().getString(
						R.string.error_invalid_password));
	}

	// // Ensure that before login, status spinner is invisible
	public void testLoginFormView_visibility() throws InterruptedException {
		assertEquals(mLoginFormView.getVisibility(), View.VISIBLE);
		assertEquals(mLoginStatusView.getVisibility(), View.GONE);
	}

	//
	// // Test for login data being sent
	// public void testZAttemptLogin_valuesPassed() {
	// mUsernameView.setText(testUsername);
	// mPasswordView.setText(testPassword);
	// mTestActivity.attemptLogin();
	// // AssertEquals(mTestActivity.loginMsg, testLoginMsg);
	// assertFalse(true);
	// }
	//
	// Test for correct and incorrect logins
	public void testZAttemptCreate_createAccount() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				CreateAccountActivity.class.getName(), null, false);
		TouchUtils.clickView(this, mCreateButton);
		CreateAccountActivity createActivity = (CreateAccountActivity) monitor.waitForActivityWithTimeout(2000);
		assertNotNull(createActivity);
		createActivity.finish();
	}

	// Test for correct and incorrect logins
	public void testZAttemptLogin_loginResults() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText(testUsername);
				mPasswordView.setText(testBadPassword);
			}
		});
		TouchUtils.clickView(this, mLoginButton);
		String textViewErrMsg = mPasswordView.getError().toString();
		assertEquals(
				textViewErrMsg,
				mTestActivity.getResources().getString(
						R.string.error_invalid_password));

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText(testUsername);
				mPasswordView.setText(testPassword);
			}
		});
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				DisplayActivity.class.getName(), null, true);
		TouchUtils.clickView(this, mLoginButton);
		DisplayActivity displayActivity = (DisplayActivity) monitor.waitForActivityWithTimeout(5000);
		assertNotNull(displayActivity);
		displayActivity.finish();
		
	}

}
