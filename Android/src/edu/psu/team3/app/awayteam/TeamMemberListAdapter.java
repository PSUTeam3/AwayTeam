package edu.psu.team3.app.awayteam;

import java.util.List;

import edu.psu.team3.app.awayteam.R.id;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TeamMemberListAdapter extends ArrayAdapter<Object[]> {
	Context mContext;
	List<Object[]> teamList; // [id,name,pending]

	public TeamMemberListAdapter(Context context, int resource,
			List<Object[]> objects) {
		super(context, R.layout.team_spinner_item, objects);
		mContext = context;
		teamList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowV = inflater.inflate(R.layout.team_spinner_dropdown, parent,
				false);

		TextView nameV = (TextView) rowV.findViewById(R.id.dropdownItem);

		nameV.setText((String) teamList.get(position)[1]);
		if ((boolean) teamList.get(position)[2]) {
			rowV.setEnabled(false);
		}

		return rowV;

	}

}
