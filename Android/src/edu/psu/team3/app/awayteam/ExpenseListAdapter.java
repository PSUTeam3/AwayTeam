package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpenseListAdapter extends BaseExpandableListAdapter {
	List<TeamExpense> expenseList;
	private Context mContext;

	public ExpenseListAdapter(Context context, List<TeamExpense> objects) {
		expenseList = objects;
		mContext = context;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowV = inflater.inflate(R.layout.expense_entry, parent, false);
		TextView dateView = (TextView) rowV.findViewById(R.id.expenseDate);
		dateView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(
				expenseList.get(groupPosition).date));
		TextView catView = (TextView) rowV.findViewById(R.id.expenseCategory);
		catView.setText(expenseList.get(groupPosition).category.toString());
		TextView amountView = (TextView) rowV.findViewById(R.id.expenseAmount);
		String formattedAmount = String.format("%1$,.2f", expenseList.get(groupPosition).amount);
		amountView.setText("$"+formattedAmount);
		return rowV;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowV = inflater.inflate(R.layout.task_detail, null);

		TextView descriptionText = (TextView) rowV
				.findViewById(R.id.task_description);
		descriptionText.setText(expenseList.get(groupPosition).description);
		return rowV;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// each expense has 1 description child
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return expenseList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
