package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.psu.team3.app.awayteam.CreateTeamDialog.CreateTeamTask;
import edu.psu.team3.app.awayteam.ExpenseCreateDialog.UploadReceiptTask;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ExpenseEditDialog extends DialogFragment {
	private EditExpenseTask mEditTask = null;
	private UploadReceiptTask mReceiptTask = null;

	Uri receiptURI = null;

	private Date date;
	private double amount = 0;
	private int category = 0;
	private String description = null;

	Button dateView;
	Spinner catSpinner;
	EditText amountView;
	EditText descView;

	Button addReceipt;
	ImageView receiptPreView;

	TeamExpense expense = null;

	// requires "expenseID" for expense to be edited
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// get the passed expenseID
		Bundle args = getArguments();
		expense = UserSession.getInstance(getActivity()).activeTeam
				.getExpense(args.getInt("expenseID"));
		// inflate custom view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setTitle("Edit Expense");
		builder.setIcon(getResources().getDrawable(R.drawable.ic_action_edit));
		builder.setView(inflater.inflate(R.layout.dialog_expense_edit, null))
				// Add action buttons
				.setPositiveButton("Apply",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ExpenseEditDialog.this.getDialog().cancel();
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
					attemptEditExpense();
				}
			});
			// init UI elements
			date = expense.date;
			dateView = (Button) d.findViewById(R.id.expenseEditDate);
			dateView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM)
					.format(date));
			catSpinner = (Spinner) d.findViewById(R.id.expenseEditCategory);
			catSpinner.setSelection(expense.category.getValue() - 1);
			amountView = (EditText) d.findViewById(R.id.expenseEditAmount);
			String formattedAmount = String.format("%1$,.2f", expense.amount);
			amountView.setText(formattedAmount);
			descView = (EditText) d.findViewById(R.id.expenseEditDescription);
			descView.setText(expense.description);
			// implement picker
			dateView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					DatePickerDialog dateDialog = new DatePickerDialog(
							getActivity(),
							new DatePickerDialog.OnDateSetListener() {

								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									Calendar cal = Calendar.getInstance();
									cal.set(year, monthOfYear, dayOfMonth);
									date = cal.getTime();
									dateView.setText(DateFormat
											.getDateInstance(DateFormat.MEDIUM)
											.format(date));
								}
							}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
							cal.get(Calendar.DAY_OF_MONTH));
					dateDialog.show();
				}
			});
			// init receipt UI
			addReceipt = (Button) d.findViewById(R.id.expenseAddReceipt);
			receiptPreView = (ImageView) d
					.findViewById(R.id.expenseReceiptThumb);
			if (expense.hasReceipt) {
				addReceipt.setText("Replace Receipt");
			}
			addReceipt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Try to take a pic
					Intent takePictureIntent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);

					// Continue only if camera is available
					if (takePictureIntent.resolveActivity(getActivity()
							.getPackageManager()) != null) {
						startActivityForResult(takePictureIntent, 1);
					}
				}
			});
		}
	}

	// listen for the dialog to be closed to reinit actionbar as required
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		((DisplayActivity) getActivity()).initActionBar();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("RCPT", "Result Code: " + resultCode);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			Log.v("RESULT", data.getDataString());
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			receiptPreView.setImageBitmap(imageBitmap);
			receiptPreView.setVisibility(View.VISIBLE);
			addReceipt.setText("Replace Receipt");
			receiptURI = data.getData();
		}
	}

	private void attemptEditExpense() {
		boolean cancel = false;
		amount = Double.parseDouble(amountView.getText().toString());
		description = descView.getText().toString();
		category = catSpinner.getSelectedItemPosition() + 1;
		if (description.isEmpty()) {
			description = "No Description";
		}
		if (amount <= 0) {
			cancel = true;
			amountView.setError("Check valid amount. Must be more than 0");
			amountView.requestFocus();
		}

		Date now = new Date();
		if (date.compareTo(now) > 0) {
			cancel = true;
			Toast.makeText(getActivity(),
					"Expense date cannot be in the future", Toast.LENGTH_SHORT)
					.show();
		}

		if (!cancel && mEditTask == null) {
			mEditTask = new EditExpenseTask();
			mEditTask.execute();
		}

	}

	public class EditExpenseTask extends AsyncTask<Object, Void, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance(getActivity());
			Integer result = 0;
			result = CommUtil.EditExpense(getActivity(), s.getUsername(),
					s.currentTeamID, expense.id, date, amount, category,
					description);

			Log.v("Background", "returned from commutil.  result = " + result);

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mEditTask = null;
			if (result == 1) {// success!
				// try to upload image
				if (receiptURI != null && mReceiptTask == null) {
					mReceiptTask = new UploadReceiptTask();
					mReceiptTask.execute(expense.id, receiptURI);
				}
				// report good news
				Toast.makeText(getActivity().getBaseContext(),
						"Expense Updated", Toast.LENGTH_SHORT).show();
				// callback the team id
				((DisplayActivity) getActivity()).refreshTeam(UserSession
						.getInstance(getActivity()).currentTeamID);
				getDialog().dismiss();
			} else {// some error occured
				Toast.makeText(getActivity().getBaseContext(),
						"Unable to Create Expense", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			mEditTask = null;
		}
	}

	// uploads receipt image - requires 2 parameters: expense ID #, receipt URI
	public class UploadReceiptTask extends AsyncTask<Object, Void, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance(getActivity());
			int id = (int) params[0];
			Uri receiptPath = (Uri) params[1];
			Integer result = 0;
			if (id > 0 && receiptURI != null) {
				// try to upload the receipt if rest of receipt is uploaded to
				// attach to
				result = CommUtil.UploadReceipt(getActivity(), s.getUsername(),
						s.currentTeamID, id, receiptPath);
			}

			Log.v("Receipt", "returned from commutil.  result = " + result);

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mReceiptTask = null;
			if (result == 0) {
				Toast.makeText(getActivity().getBaseContext(),
						"Unable to Upload Receipt", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			mReceiptTask = null;
		}
	}

}
