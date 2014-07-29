package edu.psu.team3.app.awayteam;

import java.util.Collections;
import java.util.List;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseFragment extends Fragment {
	private DeleteExpenseTask mDeleteTask = null;

	TextView expenseTotalView;
	TextView expenseLabel;
	ImageButton addExpenseButton;
	ListView expenseListView;
	ExpenseListAdapter adapter;

	boolean delete = false;

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
		expenseListView = (ListView) rootView
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
		// expenseListView.setOnGroupClickListener(new OnGroupClickListener() {
		//
		// @Override
		// public boolean onGroupClick(ExpandableListView parent, View v,
		// int groupPosition, long id) {
		// if (selectionMode
		// && !expenseListView.isItemChecked(groupPosition)) {
		// expenseListView.setItemChecked(groupPosition, true);
		// selectedExpenses.add((TeamExpense) adapter
		// .getGroup(groupPosition));
		// return true;
		// } else if (selectionMode
		// && expenseListView.isItemChecked(groupPosition)) {
		// expenseListView.setItemChecked(groupPosition, false);
		// selectedExpenses.remove((TeamExpense) adapter
		// .getGroup(groupPosition));
		// return true;
		// }
		// return false;
		// }
		// });

		expenseListView
				.setMultiChoiceModeListener(new MultiChoiceModeListener() {
					Menu selectMenu;

					@Override
					public boolean onPrepareActionMode(ActionMode mode,
							Menu menu) {
						return false;
					}

					@Override
					public void onDestroyActionMode(ActionMode mode) {
						if (!delete) {
							adapter.clearSelection();
						}
						// otherwise, hold on to the selection so the background
						// task can use it
					}

					@Override
					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						MenuInflater inflater = getActivity().getMenuInflater();
						inflater.inflate(R.menu.multi_select, menu);
						selectMenu = menu;
						delete = false;
						return true;
					}

					@Override
					public boolean onActionItemClicked(ActionMode mode,
							MenuItem item) {
						switch (item.getItemId()) {
						case R.id.action_selected_delete:
							delete = true;
							mDeleteTask = new DeleteExpenseTask();
							mDeleteTask.execute();
							mode.finish();
							break;
						case R.id.action_selected_edit:
							// create dialog and pass id
							DialogFragment newFragment = new ExpenseEditDialog();
							Bundle bundle = new Bundle();
							bundle.putInt("expenseID", adapter.getSelection()
									.get(0).id);
							newFragment.setArguments(bundle);
							newFragment.show(getFragmentManager(), null);
							mode.finish();
							break;
						}
						return true;
					}

					@Override
					public void onItemCheckedStateChanged(ActionMode mode,
							int position, long id, boolean checked) {

						// update selected list
						if (checked) {
							adapter.addSelection(position);
						} else {
							adapter.removeSelection(position);
						}

						final int checkedCount = expenseListView
								.getCheckedItemCount();
						switch (checkedCount) {
						case 0:
							mode.setSubtitle(null);
							break;
						case 1:
							selectMenu.setGroupVisible(R.id.menu_group_edit,
									true);
							break;
						default:
							selectMenu.setGroupVisible(R.id.menu_group_edit,
									false);
							break;
						}

						// calculate for subtotal
						double subtotal = 0;
						for (TeamExpense expense : adapter.getSelection()) {
							subtotal += expense.amount;
						}
						String formattedAmount = String.format("%1$,.2f",
								subtotal);
						mode.setTitle("Subtotal: $" + formattedAmount);
					}
				});

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

	//background task to delete the selected expenses
	public class DeleteExpenseTask extends AsyncTask<Object, Void, Integer> {
		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance(getActivity());
			Integer result = 0;
			for (TeamExpense expense : adapter.getSelection()) {
				result = CommUtil.DeleteExpense(getActivity(), s.getUsername(),
						s.currentTeamID, expense.id);
				Log.v("Background", "returned from commutil.  result = "
						+ result);
			}

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mDeleteTask = null;
			delete = false;
			if (result == 1) {// success!
				if (adapter.getSelection().size() == 1) {
					Toast.makeText(getActivity().getBaseContext(),
							"Expense Deleted", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							getActivity().getBaseContext(),
							adapter.getSelection().size() + " Expenses Deleted",
							Toast.LENGTH_SHORT).show();
				}
				adapter.clearSelection();
				((DisplayActivity) getActivity()).refreshTeam(UserSession
						.getInstance(getActivity()).currentTeamID);
			} else {// some error occured
				Toast.makeText(getActivity().getBaseContext(),
						"Unable to Delete Expense", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			mDeleteTask = null;
		}
	}
}
