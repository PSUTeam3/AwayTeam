package edu.psu.team3.app.awayteam;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberListAdapter extends ArrayAdapter<TeamMember> {
	Context mContext;
	List<TeamMember> memberList;
	
	public MemberListAdapter(Context context, int resource, List<TeamMember> objects) {
		super(context, R.layout.team_list_item, objects);
		mContext = context;
		memberList = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowV = inflater.inflate(R.layout.member_entry, parent, false);
		if (memberList.get(position) != null) {
			// find views in the row
			TextView nameV = (TextView) rowV.findViewById(R.id.member_name);
			ImageView manageV = (ImageView) rowV
					.findViewById(R.id.role_image);

			nameV.setText(memberList.get(position).firstName + " "+ memberList.get(position).lastName);
			if (memberList.get(position).manager) {
				manageV.setVisibility(View.VISIBLE);
			}
		}
		return rowV;

	}

}
