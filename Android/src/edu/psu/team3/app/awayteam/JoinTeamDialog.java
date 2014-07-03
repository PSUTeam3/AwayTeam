package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class JoinTeamDialog extends DialogFragment {
	
	private MatrixCursor searchCursor = null;
	private List<Object[]> searchList = new ArrayList<Object[]>();
	
	private ListView mTeamListView;
	private SearchView mSearchView;
	
	//list for holding values from server
	//format is team ID,teamName,teamLocation,managed
	private String[] columns = new String[]{"id","name","location","managed"};
	private List<Object[]> allTeamsList = new ArrayList<Object[]>();
	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// inflate custom view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setTitle("Join A Team");
		builder.setIcon(getResources().getDrawable(R.drawable.ic_action_add_person));
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
		mSearchView = (SearchView) getDialog().findViewById(R.id.teamSearchView);
		//TODO: Collect info on all teams
		
		//TODO: create a test array so you can practice searching
		allTeamsList.add(new Object[]{0,"Team1","Chicago",false});		
		allTeamsList.add(new Object[]{0,"Team2","Denver",false});
		allTeamsList.add(new Object[]{0,"Team3","Philadelphia",false});
		allTeamsList.add(new Object[]{0,"Team4","New York",true});
	
		
		//TODO: build and inflate listview
		refreshList(allTeamsList);
		
		//TODO: implement search stuffs
		mSearchView.setOnQueryTextListener(new OnQueryTextListener(){

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Do search for "+query, Toast.LENGTH_SHORT).show();
				search(query);
				return true;
			}
			
		});
		
		mSearchView.setOnCloseListener(new OnCloseListener(){

			@Override
			public boolean onClose() {
				//TODO: clear search results - show all teams
				Toast.makeText(getActivity(), "Search Closed", Toast.LENGTH_SHORT).show();				
				return false;
			}
			
		});

	}
	
	//TODO: collect callbacks from search
	private void search(String query){
		for(Object[] row: allTeamsList){
			if(((String)row[2]).contains(query) || ((String)row[3]).contains(query)){
				//TODO: add to search cursor (or to a search list - whichever ends up working easiest)
			}
		}
		//TODO: update listView with search results
	}
	
	//Updates the listview with provided list of teams
	private void refreshList(List<Object[]> teamList){
		//TODO: set up listView adapter and populate
	}
	
}
