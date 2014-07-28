package edu.psu.team3.app.awayteam;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
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
import android.widget.ToggleButton;

public class DisplayActivity extends Activity implements ActionBar.TabListener {
	private GetTeamTask mGetTeam = null;
	private RefreshSpinnerTask mRefreshList = null;
	private LeaveTeamTask mLeaveTeam = null;

	SectionsPagerAdapter mSectionsPagerAdapter;
	Spinner spinnerView;
	ViewPager mViewPager;
	private Menu optionsMenu;

	// For testing
	public DialogFragment currentDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the activity that will host fragments
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
		spinnerView
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// Collect team ID from teamList and save to current
						// TeamID for reference
						UserSession s = UserSession
								.getInstance(getBaseContext());
						int teamID = (int) s.teamList.get(position)[0];
						s.updateCurrentTeam(teamID);

						// load a new team based on selection
						if (mGetTeam == null) {
							try {
								mGetTeam = new GetTeamTask();
								mGetTeam.execute(teamID);
								setRefreshActionButtonState(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		// Create the adapter that will return a fragment for each of the
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

		// Request the team information
		refreshTeam(UserSession.getInstance(this).currentTeamID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.optionsMenu = menu;
		getMenuInflater().inflate(R.menu.display, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Configure menus to reflect user role

		menu.clear();
		getMenuInflater().inflate(R.menu.display, menu);
		if (UserSession.getInstance(getBaseContext()) != null
				&& UserSession.getInstance(getBaseContext()).activeTeam != null) {
			if (UserSession.getInstance(getBaseContext()).activeTeam.userManager) {
				menu.clear();
				getMenuInflater().inflate(R.menu.display_manager, menu);
			}
		}
		this.optionsMenu = menu;
		UserSession s = UserSession.getInstance(this);
		if (s.activeTeam == null && s.currentTeamID >= 0) {
			setRefreshActionButtonState(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			refreshTeam(UserSession.getInstance(getBaseContext()).currentTeamID);
			return true;
		}
		if (id == R.id.action_leave) {
			if (mLeaveTeam == null) {
				mLeaveTeam = new LeaveTeamTask();
				mLeaveTeam.execute(this);
			}
		}
		if (id == R.id.action_edit_team) {
			DialogFragment newFragment = new EditTeamDialog();
			newFragment.show(getFragmentManager(), null);
			return true;
		}
		if (id == R.id.action_create_team) {
			DialogFragment newFragment = new CreateTeamDialog();
			newFragment.show(getFragmentManager(), null);
			currentDialog = newFragment;
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
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			// check for active team before displaying anything besides
			// placeholder
			if (UserSession.getInstance(getBaseContext()).activeTeam != null) {
				String[] titles = getResources().getStringArray(
						R.array.tab_titles);
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
				case "Expenses":
					return new ExpenseFragment();
				}
			}
			return new PlaceholderFragment();
		}

		@Override
		public int getCount() {
			// count the number of titles in the string file
			return getResources().getStringArray(R.array.tab_titles).length;
		}

		@Override
		public int getItemPosition(Object item) {
			return POSITION_NONE;
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
		// spin the refresh button to indicate progress
		setRefreshActionButtonState(true);

		// run background task to update spinner
		// TeamID will already be saved as the UserSession team ID, but just
		// double check
		UserSession.getInstance(this).updateCurrentTeam(teamID);
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
		}if(teamID==-1){
			//temporarily show placeholder fragment
			mSectionsPagerAdapter.notifyDataSetChanged();
			mViewPager.invalidate();
			setRefreshActionButtonState(false);
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

	public void setRefreshActionButtonState(final boolean refreshing) {
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu
					.findItem(R.id.action_refresh);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem
							.setActionView(R.layout.actionbar_indeterminate_progress);
				} else {
					refreshItem.setActionView(null);
				}
			}
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
			Integer secondary = CommUtil.GetExpenses(getBaseContext(),
					s.getUsername(), (int) params[0]);
			Log.v("Background", "returned from commutil.  result = " + result);

			return result;
		}


		@Override
		protected void onPostExecute(final Integer result) {
			mGetTeam = null;

			switch (result) {
			case 1: // success!
				mSectionsPagerAdapter.notifyDataSetChanged();
				mViewPager.invalidate();
				break;
			case -1:
				Toast.makeText(getBaseContext(), "Denied Access to Team",
						Toast.LENGTH_SHORT).show();
				mSectionsPagerAdapter.notifyDataSetChanged();
				mViewPager.invalidate();
				break;
			default: // some other error occured

				Toast.makeText(getBaseContext(),
						"Unable to get Team information", Toast.LENGTH_SHORT)
						.show();
			}
			setRefreshActionButtonState(false);

		}

		@Override
		protected void onCancelled() {
			mGetTeam = null;
			setRefreshActionButtonState(false);
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

			switch (result) {
			case 1: // success!
				// refresh the list
				refreshTeamSpinner();
				break;
			case -1: // no teams available, refresh spinner to clear old data
				refreshTeamSpinner();
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

	// Dispatch request to leave the team
	// INPUT: first parameter will be the activity context,
	// second will be boolean value about whether this is a
	// confirmed request
	public class LeaveTeamTask extends AsyncTask<Object, Void, Integer> {

		Context activity = null;

		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance(getBaseContext());
			boolean confirmed = false;
			activity = (Context) params[0];
			if (params.length > 1) {
				confirmed = (boolean) params[1];
			}
			// dispatch the collection method
			Integer result = 0;
			result = CommUtil.LeaveTeam(getBaseContext(), s.getUsername(),
					s.currentTeamID, confirmed);
			Log.v("Background", "returned from commutil.  result = " + result);

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			switch (result) {
			case 1: // success!
				UserSession.getInstance(getBaseContext()).activeTeam = null;
				refreshTeam(-1);
				break;
			case -1:
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Are You Sure?");
				builder.setMessage(
						"If you leave this team, it will be deleted.")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										mLeaveTeam = null;
										mLeaveTeam = new LeaveTeamTask();
										mLeaveTeam.execute(activity, true);
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// User cancelled the dialog
									}
								});
				// Create the AlertDialog object and return it
				builder.create().show();
				break;
			default: // some other error occured

				Toast.makeText(getBaseContext(), "Unable to complete Request",
						Toast.LENGTH_SHORT).show();
			}
			mLeaveTeam = null;
		}

		@Override
		protected void onCancelled() {
			mLeaveTeam = null;
		}

	}
}
