package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import edu.psu.team3.app.awayteam.CreateTeamDialog.CreateTeamTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class JoinTeamDialog extends DialogFragment {
	private GetListTask mGetListTask = null;

	private List<Object[]> searchList = new ArrayList<Object[]>();

	private ListView mTeamListView;
	private SearchView mSearchView;

	// list for holding values from server
	// format is team ID,teamName,teamLocation,managed
	private List<Object[]> allTeamsList = new ArrayList<Object[]>();

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// inflate custom view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setTitle("Join A Team");
		builder.setIcon(getResources().getDrawable(
				R.drawable.ic_action_add_person));
		builder.setView(inflater.inflate(R.layout.dialog_join_team, null))
		// Add action button
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								JoinTeamDialog.this.getDialog().cancel();
							}
						});

		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		mTeamListView = (ListView) getDialog().findViewById(R.id.teamListView);
		mSearchView = (SearchView) getDialog()
				.findViewById(R.id.teamSearchView);
		// Collect info on all teams
		try {
			mGetListTask = new GetListTask();
			mGetListTask.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO: create a test array so you can practice searching
		// Move this functionality to the background task to update UI
		allTeamsList.clear();
		allTeamsList.add(new Object[] { 0, "Team1", "Chicago", false });
		allTeamsList.add(new Object[] { 0, "Team2", "Denver", false });
		allTeamsList.add(new Object[] { 0, "Team3", "Philadelphia", false });
		allTeamsList.add(new Object[] { 0, "Team4", "New York", true });

		refreshList(allTeamsList);

		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				search(query);
				return true;
			}

		});

		mSearchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				refreshList(allTeamsList);
				return false;
			}

		});

		// Setup listener for list selections
		mTeamListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View targetView,
					int position, long rowID) {
				Object[] selection = (Object[]) adapter
						.getItemAtPosition(position);
				Toast.makeText(getActivity(),
						selection[1].toString() + " selected",
						Toast.LENGTH_SHORT).show();
				//Take action based on whether the team is managed
				if ((boolean) selection[3]) {
					Toast.makeText(
							getActivity(),
							"Team information will become visible when a Team Manager approves membership",
							Toast.LENGTH_LONG).show();
					//indicate that view does not change yet

				}else{
					//Public team selected - pass back team id so it can be loaded
					((DisplayActivity) getActivity()).teamDialogReturn((int) selection[0]);
				}
				getDialog().dismiss();
			}

		});
	}

	// Search using list
	private void search(String query) {
		searchList = new ArrayList<Object[]>();
		for (Object[] row : allTeamsList) {
			if (((String) row[1]).toLowerCase().contains(query.toLowerCase())
					|| ((String) row[2]).toLowerCase().contains(
							query.toLowerCase())) {
				searchList.add(row);
			}
		}
		// update listView with search results
		refreshList(searchList);
	}

	// Updates the listview with provided list of teams
	private void refreshList(List<Object[]> teamList) {
		// set up listView adapter and populate
		TeamListAdapter adapter = new TeamListAdapter(getActivity(), 0,
				teamList);
		// Attach the adapter to a ListView
		mTeamListView.setAdapter(adapter);
	}

	public class GetListTask extends AsyncTask<Object, Void, List<Object[]>> {

		@Override
		protected List<Object[]> doInBackground(Object... params) {
			// dispatch the login method
			List<Object[]> result = null;
			result = CommUtil.GetAllTeamsList(getActivity());

			return result;
		}

		@Override
		protected void onPostExecute(final List<Object[]> result) {
			mGetListTask = null;

			if (result != null) {
				allTeamsList = result;
				refreshList(allTeamsList);

			} else {// some error occured
				Toast.makeText(getActivity().getBaseContext(),
						"Unable to collect Team List", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		protected void onCancelled() {
			mGetListTask = null;
		}
	}

}
