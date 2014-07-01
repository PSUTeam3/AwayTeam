package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.psu.team3.app.awayteam.LoginActivity.UserLoginTask;
import edu.psu.team3.app.awayteam.R.string;
import edu.psu.team3.app.awayteam.ViewGroupUtils;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayActivity extends Activity implements ActionBar.TabListener {
	private PassChangeTask mPassTask = null;

	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// display overview for the most recent team viewed or first team in the
		// list

		// Show default fragment when no teams exist yet
		setContentView(R.layout.activity_display);

		// Set up the action bar. For tabs
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Add dropdown to action bar
		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");
		View titleView = findViewById(titleId);
		// attach listener for handling spinner selection change
		Spinner spinnerView = (Spinner) getLayoutInflater().inflate(
				R.layout.team_spinner_layout, null);
		// swap out title for spinner
		ViewGroupUtils.replaceView(titleView, spinnerView);

		// TODO: fill spinner with useful data
		List<String> teamList = new ArrayList<String>();
		teamList.add("Test Team 1");
		teamList.add("Test Team 2");
		teamList.add("Test Team 3");
		// Add team actions to the end of the spinner list
		String[] actionsArray = getResources().getStringArray(
				R.array.team_spinner_actions);
		for (int i = 0; i < actionsArray.length; i++) {
			teamList.add(teamList.size(), actionsArray[i]);
		}
		// update spinner entries
		// TODO: move this to a spinner util class
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, teamList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerView.setAdapter(adapter);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_change_pass) {
			if (mPassTask == null) {
				try {
					mPassTask = new PassChangeTask();
					mPassTask.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(getBaseContext(), "Changing Password to 'test'", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		if (id == R.id.action_logout) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			// TODO: change the fragment based on the title
			String[] titles = getResources().getStringArray(R.array.tab_titles);
			switch (titles[position]) {
			case "Overview":
				return new OverviewFragment();
			case "Member List":
				return new MembersFragment();
			case "Team Calendar":
				return new CalendarFragment();
			case "Team Tasks":
				return new TaskFragment();
			case "Map":
				return new MapFragment();
			}
			return new PlaceholderFragment();
		}

		@Override
		public int getCount() {
			// count the number of titles in the string file
			return getResources().getStringArray(R.array.tab_titles).length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getResources().getStringArray(R.array.tab_titles)[position];
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_display,
					container, false);
			return rootView;
		}
	}

	public class PassChangeTask extends AsyncTask<Object, Void, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance();
			// dispatch the login method
			Integer result = 0;
			result = CommUtil.ChangePassword(s.getUsername(), "test", getBaseContext());
			
			Log.v("Background", "returned from commutil.  result = " + result);

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mPassTask = null;

			switch (result) {
			case 1: // success!
				Toast.makeText(getBaseContext(), "Password Changed",
						Toast.LENGTH_SHORT).show();
				break;
			default: // some other error occured
				Toast.makeText(getBaseContext(), "Unable to Change Password",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mPassTask = null;
		}
	}

}
