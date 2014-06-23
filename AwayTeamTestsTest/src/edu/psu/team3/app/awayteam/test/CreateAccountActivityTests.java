package edu.psu.team3.app.awayteam.test;

import java.util.Random;

import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.psu.team3.app.awayteam.*;
import edu.psu.team3.app.awayteam.R;

/**
 * JUnit Test class for the Create Account Activity
 * 
 */
public class CreateAccountActivityTests extends
		ActivityInstrumentationTestCase2<CreateAccountActivity> {
	private CreateAccountActivity mTestActivity;

	// Test Account data
	String testPassword = "123testxyzpassword";
	String testBadPassword = "123testxyzpasswordIncorrect";
	String testUsername = "testuser2";
	String testTakenUsername = "testuser1";
	String testName = "John Doe";
	String testLongName = "John A Doe";
	String testShortName = "Johnny";
	String testReverseName = "Doe, John A";
	String testTakenEmail = "j@doe.com";
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
	private Button mCreateButton;
	private Button mCheckButton;

	// intent
	Intent mIntent;

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
		mCreateButton = (Button) mTestActivity
				.findViewById(R.id.account_submit_button);
		mCheckButton = (Button) mTestActivity
				.findViewById(R.id.username_check_button);
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
	 * 
	 * @throws InterruptedException
	 */

	// test all the errors that should be shown for invalid input
	public void testInputViews_validation() throws InterruptedException {
		// clear out any values
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText("");
				mPassword1View.setText("");
				mPassword2View.setText("");
				mNameView.setText("");
				mPhoneView.setText("");
				mEPhoneView.setText("");
				mEmailView.setText("");
			}
		});

		// required fields
		TouchUtils.clickView(this, mCreateButton);
		Thread.sleep(1000);
		assertEquals(mUsernameView.getError(), mTestActivity.getResources()
				.getString(R.string.error_field_required));
		assertEquals(mPassword1View.getError(), mTestActivity.getResources()
				.getString(R.string.error_field_required));
		assertEquals(mPassword2View.getError(), mTestActivity.getResources()
				.getString(R.string.error_field_required));
		assertEquals(mNameView.getError(), mTestActivity.getResources()
				.getString(R.string.error_field_required));
		assertEquals(mEmailView.getError(), mTestActivity.getResources()
				.getString(R.string.error_field_required));
		assertEquals(mPhoneView.getError(), mTestActivity.getResources()
				.getString(R.string.error_field_required));
		// Valid usernames
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText(testTakenUsername);

			}
		});
		TouchUtils.clickView(this, mCheckButton);
		Thread.sleep(2000);
		assertEquals(mUsernameView.getError(), "Username Taken");// TODO: code
																	// this into
																	// a string
																	// resource
		// invalid password
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPassword1View.setText("1");
			}
		});
		TouchUtils.clickView(this, mCreateButton);
		assertEquals(mPassword1View.getError(), mTestActivity.getResources()
				.getString(R.string.error_invalid_password));// TODO:
		// code
		// this
		// into
		// a
		// string
		// resource
		// non-matching passwords
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPassword1View.setText(testPassword);
				mPassword2View.setText(testBadPassword);
			}
		});
		TouchUtils.clickView(this, mCreateButton);
		assertEquals(mPassword2View.getError(), "Paswords do not match");// TODO:
																			// place
																			// this
																			// in
																			// string
																			// resource
		// invalid phone
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPhoneView.setText(testBadPhone);
			}
		});
		TouchUtils.clickView(this, mCreateButton);
		assertEquals(mPhoneView.getError(), "Invalid phone number"); // TODO:
																		// place
																		// into
																		// string
																		// resource
		// invalid email
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mEmailView.setText(testBadEmail);
			}
		});
		TouchUtils.clickView(this, mCreateButton);
		assertEquals(mEmailView.getError(), mTestActivity.getResources()
				.getString(R.string.error_invalid_email));
	}

	// Tests for correct name interpretation
	public void testNameView_nameInterpretation1() throws InterruptedException {
		// clear out any values
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText(testUsername);
				mPassword1View.setText(testPassword);
				mPassword2View.setText(testPassword);
				mNameView.setText(testName);
				mPhoneView.setText(testPhone);
				mEPhoneView.setText(testPhone);
				mEmailView.setText(testTakenEmail);
			}
		});

		TouchUtils.clickView(this, mCreateButton);
		Thread.sleep(2000);
		String last = mTestActivity.getLastName();
		String first = mTestActivity.getFirstName();

		assertEquals(last, "Doe");
		assertEquals(first, "John");
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mNameView.setText(testLongName);
			}
		});
		TouchUtils.clickView(this, mCreateButton);
		// Thread.sleep(2000);
		last = mTestActivity.getLastName();
		first = mTestActivity.getFirstName();
		assertEquals(last, "Doe");
		assertEquals(first, "John");
	}

	public void testNameView_nameInterpretation2() throws InterruptedException {
		// clear out any values
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPhoneView.setText(testPhone);
				mEPhoneView.setText(testPhone);
				mEmailView.setText(testTakenEmail);
				mUsernameView.setText(testUsername);
				mPassword1View.setText(testPassword);
				mPassword2View.setText(testPassword);
				mNameView.setText(testLongName);
			}
		});

		TouchUtils.clickView(this, mCreateButton);
		Thread.sleep(2000);
		assertEquals(mTestActivity.getLastName(), "Doe");
		assertEquals(mTestActivity.getFirstName(), "John");
	}

	public void testNameView_nameInterpretation3() throws InterruptedException {
		// clear out any values
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPhoneView.setText(testPhone);
				mEPhoneView.setText(testPhone);
				mEmailView.setText(testTakenEmail);
				mUsernameView.setText(testUsername);
				mPassword1View.setText(testPassword);
				mPassword2View.setText(testPassword);
				mNameView.setText(testReverseName);
			}
		});

		TouchUtils.clickView(this, mCreateButton);
		Thread.sleep(2000);
		assertEquals(mTestActivity.getLastName(), "Doe");
		assertEquals(mTestActivity.getFirstName(), "John");
	}

	public void testNameView_nameInterpretation4() throws InterruptedException {
		// clear out any values
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPhoneView.setText(testPhone);
				mEPhoneView.setText(testPhone);
				mEmailView.setText(testTakenEmail);
				mUsernameView.setText(testUsername);
				mPassword1View.setText(testPassword);
				mPassword2View.setText(testPassword);
				mNameView.setText(testShortName);
			}
		});

		TouchUtils.clickView(this, mCreateButton);
		Thread.sleep(2000);
		assertEquals(mTestActivity.getLastName(), "");
		assertEquals(mTestActivity.getFirstName(), "Johnny");
	}

	// Ensure that before login, status spinner is invisible
	public void testLoginFormView_visibility() {
		assertEquals(mCreateFormView.getVisibility(), View.VISIBLE);
		assertEquals(mCreateStatusView.getVisibility(), View.GONE);
	}

	// Test for incorrect logins
	public void testZAttemptCreate_incorrect() throws InterruptedException {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText(testTakenUsername);
				mPassword1View.setText(testPassword);
				mPassword2View.setText(testPassword);
				mNameView.setText(testName);
				mEmailView.setText(testTakenEmail);
				mPhoneView.setText(testPhone);
				mEPhoneView.setText(testPhone);
			}
		});
		TouchUtils.clickView(this, mCreateButton);
		Thread.sleep(2000);
		assertEquals(mUsernameView.getError(), "Username Taken");

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText(testUsername);
				mPassword1View.setText(testPassword);
				mPassword2View.setText(testPassword);
				mNameView.setText(testName);
				mEmailView.setText(testTakenEmail);
				mPhoneView.setText(testPhone);
				mEPhoneView.setText(testPhone);
			}
		});
		TouchUtils.clickView(this, mCreateButton);
		Thread.sleep(2000);
		assertEquals(mEmailView.getError(), "Email already registered");
	}

	// test for correct account creation
	public void testZAttemptCreate_correct() {
		Random randomGenerator = new Random();
		int rand = randomGenerator.nextInt(1000);
		final String newUsername = testUsername + String.valueOf(rand);
		final String newEmail = testTakenEmail + String.valueOf(rand);
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUsernameView.setText(newUsername);
				mPassword1View.setText(testPassword);
				mPassword2View.setText(testPassword);
				mNameView.setText(testName);
				mEmailView.setText(newEmail);
				mPhoneView.setText(testPhone);
				mEPhoneView.setText(testPhone);
			}
		});
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				DisplayActivity.class.getName(), null, false);
		TouchUtils.clickView(this, mCreateButton);
		DisplayActivity displayActivity = (DisplayActivity) monitor
				.waitForActivityWithTimeout(5000);
		assertNotNull(displayActivity);
		displayActivity.finish();
	}

}
