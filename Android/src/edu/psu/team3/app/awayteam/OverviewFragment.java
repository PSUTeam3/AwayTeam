package edu.psu.team3.app.awayteam;

import java.util.List;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
			 // build managers table
			 mManagerTableView.removeAllViews();
			 List<TeamMember> managers = s.activeTeam.getManagers();
			 for(final TeamMember manager:managers){
				 TableRow row = (TableRow) LayoutInflater.from(getActivity()).inflate(
							R.layout.member_entry, null);
				 TextView name = (TextView) row.findViewById(R.id.member_name);
				 name.setText(manager.firstName + " "+manager.lastName);
				 row.findViewById(R.id.role_image).setVisibility(View.VISIBLE);
				 row.setClickable(true);
				 row.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						// take action to display selected user's detail
						DialogFragment newFragment = new MemberDetailDialog();
						Bundle args = new Bundle();
					    args.putString("userName",manager.userName);
					    newFragment.setArguments(args);
						newFragment.show(getFragmentManager(), null);
					}
				});
				 mManagerTableView.addView(row);
			 }
			 
			 if(s.activeTeam.userManager){
				 //TODO: check for pending members
				 mPendingDivView.setVisibility(View.VISIBLE);
				 mPendingLabelView.setVisibility(View.VISIBLE);
				 mPendingTableView.setVisibility(View.VISIBLE);
				 //TODO: dispatch background task to collect pending members
				 
				 //IN BACKGROUND TASK:
				 //TODO: build pending members table				 
				 //TODO: if no pending members, add an entry that states this
			 }
		 }
	}
}
