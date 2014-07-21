package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CalendarListAdapter extends ArrayAdapter<TeamEvent> {
	Context mContext;
	List<TeamEvent> eventList;

	public CalendarListAdapter(Context context, int resource,
			List<TeamEvent> objects) {
		super(context, R.layout.calendar_agenda_entry, objects);
		mContext = context;
		eventList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowV = inflater.inflate(R.layout.calendar_agenda_entry, parent,
				false);
		if (eventList.get(position) != null) {
			// find views in the row
			TextView title = (TextView) rowV.findViewById(R.id.title_text);
			TextView date = (TextView) rowV.findViewById(R.id.time_text);

			title.setText(eventList.get(position).title);
			date.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT,
					DateFormat.SHORT).format(eventList.get(position).startTime)
					+" - "+
					DateFormat.getDateTimeInstance(DateFormat.SHORT,
							DateFormat.SHORT).format(eventList.get(position).endTime));
		}
		return rowV;

	}
}
