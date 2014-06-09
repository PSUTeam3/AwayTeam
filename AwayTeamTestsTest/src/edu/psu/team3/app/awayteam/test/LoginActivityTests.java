package edu.psu.team3.app.awayteam.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
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
	}

	/**
	 * Ensure that the preconditions will lead to a successful test
	 */
	public void testPreconditions() {
		assertNotNull("mTestActivity is null", mTestActivity);
	}

	/**
	 * Use this section to destroy data for the next test
	 */
	public void tearDown() throws Exception {
		// no tear down activities yet
	}

	/**
	 * ------------------------------------------------------------------------
	 * Test functions below:
	 */

	// Test that empty usernames are rejected
	public void testUsernameView_validation() {
		mUsernameView.setText("");
		mTestActivity.attemptLogin();
		String textViewErrMsg = mUsernameView.getError().toString();
		assertEquals(textViewErrMsg, R.string.error_field_required);
	}

	// Test that empty and short passwords are rejected
	public void testPasswordView_validation() {
		mPasswordView.setText("");
		mTestActivity.attemptLogin();
		String textViewErrMsg = mPasswordView.getError().toString();
		assertEquals(textViewErrMsg, R.string.error_field_required);

		mPasswordView.setText("1");
		mTestActivity.attemptLogin();
		textViewErrMsg = mPasswordView.getError().toString();
		assertEquals(textViewErrMsg, R.string.error_invalid_password);
	}

	// Ensure that before login, status spinner is invisible
	public void testLoginFormView_visibility() {
		assertEquals(mLoginFormView.getVisibility(), View.VISIBLE);
		assertEquals(mLoginStatusView.getVisibility(), View.INVISIBLE);
		mUsernameView.setText(testUsername);
		mPasswordView.setText(testPassword);
		mTestActivity.attemptLogin();
		assertEquals(mLoginFormView.getVisibility(), View.INVISIBLE);
		assertEquals(mLoginStatusView.getVisibility(), View.VISIBLE);
	}

	// Test for login data being sent
	public void testAttemptLogin_valuesPassed() {
		mUsernameView.setText(testUsername);
		mPasswordView.setText(testPassword);
		mTestActivity.attemptLogin();
//		AssertEquals(mTestActivity.loginMsg, testLoginMsg);
		assertFalse(true);
	}
	
	// Test for correct and incorrect logins
	public void testAttemptLogin_loginResults(){
		mUsernameView.setText(testUsername);
		mPasswordView.setText(testBadPassword);
		mTestActivity.attemptLogin();		
//		assertEquals(mTestActivity.loginSuccess,"false");
		String textViewErrMsg = mPasswordView.getError().toString();
		assertEquals(textViewErrMsg, R.string.error_invalid_password);
		
		mUsernameView.setText(testUsername);
		mPasswordView.setText(testPassword);
		mTestActivity.attemptLogin();
//		assertEquals(mTestActivity.loginSuccess,"true");
		assertFalse(true);
	}

}
