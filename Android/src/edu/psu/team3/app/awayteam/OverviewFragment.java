package edu.psu.team3.app.awayteam;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

public class OverviewFragment extends Fragment {

	TextView mTeamNameView;
	TextView mTeamLocView;
	TextView mDescriptionView;
	View mPendingDivView;
	TextView mPendingLabelView;
	TableLayout mPendingTableView;
	View mManagerDivView;
	TextView mManagerLabelView;
	TableLayout mManagerTableView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_overview,
				container, false);
	
		return rootView;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		//assign views
		 mTeamNameView=(TextView) getView().findViewById(R.id.team_name_text);
		 mTeamLocView=(TextView) getView().findViewById(R.id.team_location_text);
		 mDescriptionView=(TextView) getView().findViewById(R.id.team_description_text);
		 mPendingDivView=(View) getView().findViewById(R.id.pending_div);
		 mPendingLabelView=(TextView) getView().findViewById(R.id.pending_label);
		 mPendingTableView=(TableLayout) getView().findViewById(R.id.pending_table);
		 mManagerDivView=(View) getView().findViewById(R.id.manager_div);
		 mManagerLabelView=(TextView) getView().findViewById(R.id.manager_label);
		 mManagerTableView=(TableLayout) getView().findViewById(R.id.manager_table);
		 
		//load data into views
		 UserSession s = UserSession.getInstance(getActivity());
		 mTeamNameView.setText(s.activeTeam.name);
		 mTeamLocView.setText(s.activeTeam.location);
		 mDescriptionView.setText(s.activeTeam.description);
		 if(s.activeTeam.managed){
			 mManagerDivView.setVisibility(View.VISIBLE);
			 mManagerLabelView.setVisibility(View.VISIBLE);
			 mManagerTableView.setVisibility(View.VISIBLE);
			 //TODO: fill table
			 if(s.activeTeam.userManager){
				 //TODO: check for pending members
				 mPendingDivView.setVisibility(View.VISIBLE);
				 mPendingLabelView.setVisibility(View.VISIBLE);
				 mPendingTableView.setVisibility(View.VISIBLE);
			 }
		 }
	}
}
