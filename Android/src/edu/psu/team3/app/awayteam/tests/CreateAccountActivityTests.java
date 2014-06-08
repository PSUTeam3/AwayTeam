package edu.psu.team3.app.awayteam.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.psu.team3.app.awayteam.*;

/**
 * JUnit Test class for the Create Account Activity
 * 
 */
public class CreateAccountActivityTests extends
		ActivityInstrumentationTestCase2<CreateAccountActivity> {
	private CreateAccountActivity mTestActivity;

	// Test Account data
	String testCreateMsg = "testuser2:123testxyzpassword:John:Doe:1234567890:j@doe.com:1234567890"; // test
																									// message
																									// to
																									// send
																									// to
																									// server
																									// TODO:
																									// run
																									// parameters
																									// through
																									// JSON
																									// Parser
																									// and
																									// test
																									// results
																									// instead
	String testPassword = "123testxyzpassword";
	String testBadPassword = "123testxyzpasswordIncorrect";
	String testUsername = "testuser2";
	String testTakenUsername = "testuser1";
	String testName = "John Doe";
	String testLongName = "John A Doe";
	String testShortName = "Johnny";
	String testEmail = "j@doe.com";
	String testBadEmail = "johndoe";
	String testPhone = "1234567890";
	String testBadPhone = "123";

	// UI Elements that will be tested
	private EditText mUsernameView;
	private EditText mPassword1View;
	private EditText mPassword2View;
	private EditText mNameView;
	private EditText mPhoneView;
	private EditText mEmailView;
	private EditText mEPhoneView;
	private View mCreateFormView;
	private View mCreateStatusView;

	public CreateAccountActivityTests() {
		super(CreateAccountActivity.class);
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
				.findViewById(R.id.new_username_input);
		mPassword1View = (EditText) mTestActivity
				.findViewById(R.id.password1_input);
		mPassword2View = (EditText) mTestActivity
				.findViewById(R.id.password2_input);
		mNameView = (EditText) mTestActivity.findViewById(R.id.name_input);
		mPhoneView = (EditText) mTestActivity.findViewById(R.id.phone_input);
		mEmailView = (EditText) mTestActivity.findViewById(R.id.email_input);
		mEPhoneView = (EditText) mTestActivity
				.findViewById(R.id.emer_phone_input);
		mCreateFormView = mTestActivity.findViewById(R.id.create_form);
		mCreateStatusView = mTestActivity.findViewById(R.id.create_status);

		// clear out any values
		mUsernameView.setText("");
		mPassword1View.setText("");
		mPassword2View.setText("");
		mNameView.setText("");
		;
		mPhoneView.setText("");
		mEPhoneView.setText("");
		mEmailView.setText("");
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

	// test all the errors that should be shown for invalid input
	public void testInputViews_validation() {
		// required fields
		mTestActivity.attemptCreate();
		assertEquals(mUsernameView.getError(), R.string.error_field_required);
		assertEquals(mPassword1View.getError(), R.string.error_field_required);
		assertEquals(mPassword2View.getError(), R.string.error_field_required);
		assertEquals(mNameView.getError(), R.string.error_field_required);
		assertEquals(mEmailView.getError(), R.string.error_field_required);
		assertEquals(mPhoneView.getError(), R.string.error_field_required);
		// Valid usernames
		mUsernameView.setText(testTakenUsername);
		mTestActivity.attemptCreate();
		assertEquals(mUsernameView.getError(), "Username Taken");// TOOD: code
																	// this into
																	// a string
																	// resource
		// invalid password
		mPassword1View.setText("1");
		assertEquals(mPassword1View.getError(), R.string.error_invalid_password);// TOOD:
																					// code
																					// this
																					// into
																					// a
																					// string
																					// resource
		// non-matching passwords
		mPassword1View.setText(testPassword);
		mPassword2View.setText(testBadPassword);
		mTestActivity.attemptCreate();
		assertEquals(mPassword2View.getError(), "Paswords do not match");// TODO:
																			// place
																			// this
																			// in
																			// string
																			// resource
		// invalid phone
		mPhoneView.setText(testBadPhone);
		mTestActivity.attemptCreate();
		assertEquals(mPhoneView.getError(), "Invalid phone number"); // TODO:
																		// place
																		// into
																		// string
																		// resource
		// invalid email
		mEmailView.setText(testBadEmail);
		mTestActivity.attemptCreate();
		assertEquals(mEmailView.getError(), R.string.error_invalid_email);
	}

	// Tests for correct name interpretation
	public void testNameView_nameInterpretation(){
		mNameView.setText(testName);
		mTestActivity.parseName();  //TODO: build in access to name parsing
		assertEquals(mTestActivity.mLastName,"Doe");
		assertEquals(mTestActivity.mFirstName,"John");
		mNameView.setText(testLongName);
		assertEquals(mTestActivity.mLastName,"Doe");
		assertEquals(mTestActivity.mFirstName,"John");
		mNameView.setText(testShortName);
		assertEquals(mTestActivity.mLastName,"");
		assertEquals(mTestActivity.mFirstName,"Johnny");
	}

	// Ensure that before login, status spinner is invisible
	public void testLoginFormView_visibility() {
		assertEquals(mCreateFormView.getVisibility(), View.VISIBLE);
		assertEquals(mCreateStatusView.getVisibility(), View.INVISIBLE);
		mUsernameView.setText(testTakenUsername);
		mPassword1View.setText(testPassword);
		mPassword2View.setText(testPassword);
		mNameView.setText(testName);
		mEmailView.setText(testEmail);
		mPhoneView.setText(testPhone);
		mEPhoneView.setText(testPhone);
		mTestActivity.attemptCreate();
		assertEquals(mCreateFormView.getVisibility(), View.INVISIBLE);
		assertEquals(mCreateStatusView.getVisibility(), View.VISIBLE);
	}
	
	// Test for correct account data being sent
	public void testAttemptCreate_valuesPassed() {
		mUsernameView.setText(testUsername);
		mPassword1View.setText(testPassword);
		mPassword2View.setText(testPassword);
		mNameView.setText(testName);
		mEmailView.setText(testEmail);
		mPhoneView.setText(testPhone);
		mEPhoneView.setText(testPhone);
		mTestActivity.attemptCreate();
		AssertEquals(mTestActivity.createMsg, testCreateMsg);
	}
	
	// Test for correct and incorrect logins
		public void testAttemptCreate_loginResults(){
			mUsernameView.setText(testTakenUsername);
			mPassword1View.setText(testPassword);
			mPassword2View.setText(testPassword);
			mNameView.setText(testName);
			mEmailView.setText(testEmail);
			mPhoneView.setText(testPhone);
			mEPhoneView.setText(testPhone);
			mTestActivity.attemptCreate();		
			assertEquals(mTestActivity.accountSuccess,"false");
			assertEquals(mUsernameView.getError(), "Username Taken");
			mUsernameView.setText(testUsername);
			mTestActivity.attemptCreate();
			assertEquals(mTestActivity.AccountSuccess,"true");
		}
}
