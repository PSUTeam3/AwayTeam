package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MembersFragment extends Fragment {

	//UI references
	private Spinner mGroupSpinner;
	private Spinner mManagerSpinner;
	
	//list for spinners
	List<String> modeList = new ArrayList<String>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_members,
				container, false);
		

		//setup dropdown elements
		mGroupSpinner = (Spinner) rootView.findViewById(R.id.group_contact_mode_spinner);
		mManagerSpinner = (Spinner) rootView.findViewById(R.id.manager_contact_mode_spinner);
		
		
		return rootView;
	}
}
