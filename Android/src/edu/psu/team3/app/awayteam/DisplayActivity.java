package edu.psu.team3.app.awayteam;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class DisplayActivity extends Activity implements ActionBar.TabListener {
	private GetTeamTask mGetTeam = null;
	private RefreshSpinnerTask mRefreshList = null;

	SectionsPagerAdapter mSectionsPagerAdapter;

	private Spinner spinnerView;

	/**
	 * The {ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		spinnerView = (Spinner) getLayoutInflater().inflate(
				R.layout.team_spinner_layout, null);

		// swap out title for spinner
		ViewGroupUtils.replaceView(titleView, spinnerView);
		// update spinner entries and team info
		refreshTeam(UserSession.getInstance(this).currentTeamID);
		spinnerView
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						Toast.makeText(
								parent.getContext(),
								(String) parent.getItemAtPosition(position)
										+ " selected", Toast.LENGTH_SHORT)
								.show();
						// Collect team ID from teamList and save to current
						// TeamID for reference
						UserSession s = UserSession
								.getInstance(getBaseContext());
						int teamID = (int) s.teamList.get(position)[0]; // position
																		// of
																		// item
																		// in
																		// teamlist
																		// should
																		// match
																		// position
																		// in
																		// spinner
																		// since
																		// it
																		// was
																		// built
																		// off
																		// the
																		// list
						s.currentTeamID = teamID;

						// load a new team based on selection
						if (mGetTeam == null) {
							try {
								mGetTeam = new GetTeamTask();
								mGetTeam.execute(teamID);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

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
		if (id == R.id.action_create_team) {
			DialogFragment newFragment = new CreateTeamDialog();
			newFragment.show(getFragmentManager(), null);
			return true;
		}
		if (id == R.id.action_join_team) {
			DialogFragment newFragment = new JoinTeamDialog();
			newFragment.show(getFragmentManager(), null);
			return true;
		}
		if (id == R.id.action_modify_account) {
			DialogFragment newFragment = new EditAccountDialog();
			newFragment.show(getFragmentManager(), null);
			return true;
		}
		if (id == R.id.action_logout) {
			UserSession.getInstance(getBaseContext()).terminateSession();
			finish();
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
			// TODO: (once get team is able to populate the team structure)
			// check for active team before displaying anything besides
			// placeholder
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
				return new PlaceholderFragment(); // TODO: swap to map fragment
													// when map API installed
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
			// button listeners:
			rootView.findViewById(R.id.create_button).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							DialogFragment newFragment = new CreateTeamDialog();
							newFragment.show(getFragmentManager(), null);
						}
					});
			rootView.findViewById(R.id.join_button).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							DialogFragment newFragment = new JoinTeamDialog();
							newFragment.show(getFragmentManager(), null);
						}
					});
			return rootView;
		}
	}

	// Collects the new team ID from a dialog
	public void refreshTeam(int teamID) {
		Log.v("DISPLAY", "received team selection: " + teamID);
		UserSession.getInstance(getBaseContext()).currentTeamID = teamID;
		// run background task to update spinner
		// TeamID will already be saved as the UserSession team ID, but just
		// double check
		UserSession.getInstance(this).currentTeamID = teamID;
		if (mRefreshList == null) {
			try {
				mRefreshList = new RefreshSpinnerTask();
				mRefreshList.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// run background task to get team info if team exists
		if (teamID != -1 && mGetTeam == null) {
			try {
				mGetTeam = new GetTeamTask();
				mGetTeam.execute(teamID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void refreshTeamSpinner() {
		// reload the spinner data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.team_spinner_item, UserSession.getInstance(
						getBaseContext()).getTeamListNames());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerView.setAdapter(adapter);
		// set the spinner to the correct location
		UserSession s = UserSession.getInstance(this);
		if (s.currentTeamID > 0) {
			spinnerView.setSelection(s.getTeamListPosition(s.currentTeamID));
		}
	}

	// Collect a team from the server
	// requires a team ID passed as only parameter
	public class GetTeamTask extends AsyncTask<Object, Void, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance(getBaseContext());
			// dispatch the collection method
			Integer result = 0;
			result = CommUtil.GetTeam(getBaseContext(), (int) params[0],
					s.getUsername());

			Log.v("Background", "returned from commutil.  result = " + result);

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mGetTeam = null;
			UserSession s = UserSession.getInstance(getBaseContext());

			switch (result) {
			case 1: // success!
				// TODO: reload current fragment to show team data

				break;
			default: // some other error occured

				Toast.makeText(getBaseContext(),
						"Unable to get Team information", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		protected void onCancelled() {
			mGetTeam = null;
		}

	}

	// Update Team Spinner
	public class RefreshSpinnerTask extends AsyncTask<Object, Void, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance(getBaseContext());
			Integer result = 0;
			result = CommUtil.GetMemberTeamsList(getBaseContext(),
					s.getUsername());

			Log.v("Background", "returned from commutil.  result = " + result);

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mRefreshList = null;
			UserSession s = UserSession.getInstance(getBaseContext());

			switch (result) {
			case 1: // success!
				// refresh the list
				refreshTeamSpinner();
				break;
			default: // some other error occured

				Toast.makeText(getBaseContext(),
						"Unable to get list of your teams", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		protected void onCancelled() {
			mRefreshList = null;
		}

	}
}
