package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.psu.team3.app.awayteam.CreateTeamDialog.CreateTeamTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseCreateDialog extends DialogFragment {
	private CreateExpenseTask mCreateTask = null;

	private Date date;
	private double amount = 0;
	private int category = 0;
	private String description = null;

	TextView dateView;
	Spinner catSpinner;
	TextView amountView;
	TextView descView;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// inflate custom view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setTitle("Create New Expense");
		builder.setIcon(getResources().getDrawable(R.drawable.ic_action_new));
		builder.setView(inflater.inflate(R.layout.dialog_expense_edit, null))
				// Add action buttons
				.setPositiveButton("Create",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ExpenseCreateDialog.this.getDialog().cancel();
							}
						});

		return builder.create();

	}

	@Override
	public void onStart() {
		super.onStart(); // super.onStart() is where dialog.show() is actually
							// called on the underlying dialog, so we have to do
							// it after this point
		AlertDialog d = (AlertDialog) getDialog();
		if (d != null) {
			// override create button action to prevent closing immediately
			Button positiveButton = (Button) d
					.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					attemptCreateExpense();
				}
			});
			// init UI elements
			Date now = new Date();
			dateView = (TextView) d.findViewById(R.id.expenseEditDate);
			dateView.setText(DateFormat.getDateInstance(DateFormat.SHORT)
					.format(now));
			catSpinner = (Spinner) d.findViewById(R.id.expenseEditCategory);
			catSpinner.setSelection(4);
			amountView = (TextView) d.findViewById(R.id.expenseEditAmount);
			amountView.setText("0.00");
			descView = (TextView) d.findViewById(R.id.expenseEditDescription);
		}
	}

	private void attemptCreateExpense() {
		boolean cancel = false;
		View focusView = null;
		amount = Double.parseDouble(amountView.getText().toString());
		description = descView.getText().toString();
		category = catSpinner.getSelectedItemPosition() + 1;
		if (description == null) {
			description = "";
		}
		if (amount < 0) {
			cancel = true;
			amountView.setError("Check valid amount");
			focusView = amountView;
		}
		DateFormat formatter = new SimpleDateFormat("M/d/y");
		try {
			date = formatter.parse(dateView.getText().toString());

			Date now = new Date();
			if (date.compareTo(now) > 0) {
				cancel = true;
				dateView.setError("Expense date cannot be in the future");
				focusView = dateView;
			}
		} catch (ParseException e) {
			cancel = true;
			dateView.setError("Check date format: MM/DD/YYYY");
			focusView = dateView;
		}

		if (cancel) {
			focusView.requestFocus();
		} else if (mCreateTask == null) {
			mCreateTask = new CreateExpenseTask();
			mCreateTask.execute();
		}

	}

	public class CreateExpenseTask extends AsyncTask<Object, Void, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance(getActivity());
			Integer result = 0;
			result = CommUtil.CreateExpense(getActivity(), s.getUsername(),
					s.currentTeamID, date, amount, category, description);

			Log.v("Background", "returned from commutil.  result = " + result);

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mCreateTask = null;
			if (result == 1) {// success!
				Toast.makeText(getActivity().getBaseContext(),
						"New Expense Created", Toast.LENGTH_SHORT).show();
				// callback the team id
				((DisplayActivity) getActivity()).refreshTeam(result);
				getDialog().dismiss();
			} else {// some error occured
				Toast.makeText(getActivity().getBaseContext(),
						"Unable to Create Expense", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			mCreateTask = null;
		}
	}

}
