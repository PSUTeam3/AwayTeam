package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ExpenseFragment extends Fragment {

	TextView expenseTotalView;
	TextView expenseLabel;
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
		expenseLabel = (TextView) rootView.findViewById(R.id.expenseLabel);
		addExpenseButton = (ImageButton) rootView
				.findViewById(R.id.add_expense_button);
		expenseListView = (ExpandableListView) rootView
				.findViewById(R.id.expenseListView);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// ensure list of events is sorted
		UserSession s = UserSession.getInstance(getActivity());
		Collections.sort(s.activeTeam.teamExpenses, TeamExpense.DateComparator);
		calcExpense(s.activeTeam.teamExpenses);
		// fill list
		adapter = new ExpenseListAdapter(getActivity(),
				s.activeTeam.teamExpenses);
		// Attach the adapter to a ListView
		expenseListView.setAdapter(adapter);
		// Assign listener to event long clicks
		//TODO: implement
//		expenseListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
//			
//			@Override
//			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//			
//			@Override
//			public void onDestroyActionMode(ActionMode mode) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//			
//			@Override
//			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//			
//			@Override
//			public void onItemCheckedStateChanged(ActionMode mode, int position,
//					long id, boolean checked) {
//				// TODO Auto-generated method stub
//				
//			}
//		});

		// Assign actions to buttons
		addExpenseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new ExpenseCreateDialog();
				newFragment.show(getFragmentManager(), null);
			}
		});
	}

	// update the expense total field in the window with the total of all items
	// in the list
	private void calcExpense(List<TeamExpense> expenses) {
		double total = 0;
		for (TeamExpense expense : expenses) {
			total += expense.amount;
		}
		String formattedAmount = String.format("%1$,.2f", total);
		expenseTotalView.setText("$" + formattedAmount);
	}
}
