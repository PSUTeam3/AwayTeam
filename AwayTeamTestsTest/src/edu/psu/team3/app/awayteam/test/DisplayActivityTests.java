package edu.psu.team3.app.awayteam.test;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Instrumentation.ActivityMonitor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.EditText;
import edu.psu.team3.app.awayteam.CommUtil;
import edu.psu.team3.app.awayteam.DisplayActivity;
import edu.psu.team3.app.awayteam.R;

public class DisplayActivityTests extends
		ActivityInstrumentationTestCase2<DisplayActivity> {
	private DisplayActivity mTestActivity;

	

	// UI Elements that will be tested
	ViewPager mViewPager;

	public DisplayActivityTests() {
		super(DisplayActivity.class);
	}

	/**
	 * Use setUp to initialize all the UI components, instantiate the activity
	 * to test, and initialize variables
	 * NOTE: This must be run after the CreateAccountActivityTest to get login info instantiated
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mTestActivity = getActivity();
		//attempt to login
		// assign UI elements to variables
		mViewPager = (ViewPager) mTestActivity.findViewById(R.id.pager);
	}

	/**
	 * Ensure that the preconditions will lead to a successful test
	 */
	public void testPreconditions() {
		assertNotNull(mTestActivity);
		assertNotNull(mViewPager);
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

	// test pager setup
	@SmallTest
	public void testPager_navigation() {
		PagerAdapter mPagerAdapter = mViewPager.getAdapter();
		int titleCount = mTestActivity.getResources().getStringArray(
				R.array.tab_titles).length;
		assertEquals(mPagerAdapter.getCount(), titleCount);
		assertEquals(mTestActivity.getActionBar().getTabCount(), titleCount);
		
		
	}
	
	//test create team dialog
	@SmallTest
	public void testCreateTeamDialog() throws InterruptedException {
		mTestActivity.openOptionsMenu();
		getInstrumentation().invokeMenuActionSync(mTestActivity, R.id.action_create_team, 0);
		Thread.sleep(1000);
		assertTrue(getActivity().currentDialog.isAdded());
		((EditText)getActivity().findViewById(R.id.teamNameInput)).setText("JUnit Test Team");
		((EditText)getActivity().findViewById(R.id.teamLocationInput)).setText("Test Only");
		Thread.sleep(5000);
		
	}
	
	//test join team dialog
	
	
	//test edit account dialog
	
		//		test change password dialog
	
	
	//test team spinner switching
	
	
	//test logout!
	

}
