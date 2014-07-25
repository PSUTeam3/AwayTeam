package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ExpenseFragment extends Fragment{

	TextView expenseTotalView;
	ImageButton addExpenseButton;
	ExpandableListView expenseListView;
	ExpenseListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_expense, container,
				false);
		// identify UI elements
		expenseTotalView = (TextView) rootView.findViewById(R.id.expenseText);
		addExpenseButton = (ImageButton) rootView
				.findViewById(R.id.add_expense_button);
		expenseListView = (ExpandableListView) rootView.findViewById(R.id.expenseListView);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// ensure list of events is sorted
		UserSession s = UserSession.getInstance(getActivity());
		Collections.sort(s.activeTeam.teamExpenses, TeamExpense.DateComparator);
		// fill list
		adapter = new ExpenseListAdapter(getActivity(),
				s.activeTeam.teamExpenses);
		// Attach the adapter to a ListView
		expenseListView.setAdapter(adapter);
		//Assign listener to event long clicks?
		

		// Assign actions to buttons
		addExpenseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO: implement
				Toast.makeText(getActivity(), "adding expenses not enabled yet", Toast.LENGTH_SHORT).show();
			}
		});
	}
}

