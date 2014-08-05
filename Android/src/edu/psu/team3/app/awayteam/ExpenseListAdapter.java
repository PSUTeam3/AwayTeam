package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ExpenseListAdapter extends ArrayAdapter<TeamExpense> {
	List<TeamExpense> expenseList;
	List<TeamExpense> selectedList = new ArrayList<TeamExpense>();
	private Context mContext;

	public ExpenseListAdapter(Context context, List<TeamExpense> objects) {
		super(context, R.layout.expense_entry, objects);
		expenseList = objects;
		mContext = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowV = inflater.inflate(R.layout.expense_entry, parent, false);
		TextView dateView = (TextView) rowV.findViewById(R.id.expenseDate);
		dateView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(
				expenseList.get(position).date));
		TextView catView = (TextView) rowV.findViewById(R.id.expenseCategory);
		catView.setText(expenseList.get(position).category.toString());
		TextView descView = (TextView) rowV
				.findViewById(R.id.expenseDescription);
		descView.setText(expenseList.get(position).description);
		TextView amountView = (TextView) rowV.findViewById(R.id.expenseAmount);
		String formattedAmount = String.format("%1$,.2f",
				expenseList.get(position).amount);
		amountView.setText("$" + formattedAmount);

		if (expenseList.get(position).hasReceipt) {
			Button receiptButton = (Button) rowV
					.findViewById(R.id.receiptButton);
			receiptButton.setVisibility(View.VISIBLE);
			receiptButton.setContentDescription(Integer.toString(expenseList.get(position).id));
			receiptButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Download receipt and open it

				}
			});
		}

		return rowV;
	}

	public void addSelection(int position) {
		selectedList.add(expenseList.get(position));
	}

	public void removeSelection(int postion) {
		selectedList.remove(expenseList.get(postion));
	}

	public void clearSelection() {
		selectedList = new ArrayList<TeamExpense>();
	}

	public List<TeamExpense> getSelection() {
		return selectedList;
	}

}
