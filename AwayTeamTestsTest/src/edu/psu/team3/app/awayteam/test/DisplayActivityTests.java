package edu.psu.team3.app.awayteam.test;

import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import edu.psu.team3.app.awayteam.*;
import edu.psu.team3.app.awayteam.R;

public class DisplayActivityTests extends
		ActivityInstrumentationTestCase2<DisplayActivity> {
	private DisplayActivity mTestActivity;

	// Test data

	// UI Elements that will be tested
	ViewPager mViewPager;

	public DisplayActivityTests() {
		super(DisplayActivity.class);
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
	public void testPager_navigation() {
		PagerAdapter mPagerAdapter = mViewPager.getAdapter();
		int titleCount = mTestActivity.getResources().getStringArray(
				R.array.tab_titles).length;
		assertEquals(mPagerAdapter.getCount(), titleCount);
		assertEquals(mTestActivity.getActionBar().getTabCount(), titleCount);
		
		
	}

}
