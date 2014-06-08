package edu.psu.team3.app.awayteam.tests;

import android.test.ActivityInstrumentationTestCase2;
import edu.psu.team3.app.awayteam.*;

/**
 * JUnit Test class for the Login Activity
 *
 */
public class LoginActivityTests extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	private LoginActivity mTestActivity;

	// UI Elements that will be tested

	public LoginActivityTests() {
		super(LoginActivity.class);
	}

	/**
	 * Use setUp to initialize all the UI components, instantiate the activity to test,
	 * and initialize variables
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mTestActivity = getActivity();
		// assign UI elements to variables

	}
	
	/**
	 * Ensure that the preconditions will lead to a successful test
	 */
	public void testPreconditions() {
	    assertNotNull(“mFirstTestActivity is null”, mFirstTestActivity);
	    assertNotNull(“mFirstTestText is null”, mFirstTestText);
	}
}
