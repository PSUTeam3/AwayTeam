package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
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

			// store dates in more flexible types for manipulation and
			// comparison
			title.setText(eventList.get(position).title);
			Calendar start = Calendar.getInstance();
			start.setTime(eventList.get(position).startTime);
			Calendar end = Calendar.getInstance();
			end.setTime(eventList.get(position).endTime);
			Calendar previous = Calendar.getInstance();
			boolean header = false;

			// Format for the dates and times
			//find out if date header is needed for new day
			if (position == 0) {
				// fill in the header
				header = true;
			} else {
				// not first, safe to get previous date
				previous.setTime(eventList.get(position - 1).startTime);
			}
			if (start.get(Calendar.DAY_OF_YEAR) != previous
					.get(Calendar.DAY_OF_YEAR)
					|| start.get(Calendar.YEAR) != previous.get(Calendar.YEAR)) {
				header = true;
			}

			//fill in date header
			if (header) {
				TextView dateV = (TextView) rowV.findViewById(R.id.date_text);
				dateV.setVisibility(View.VISIBLE);
				dateV.setText(DateFormat.getDateInstance(DateFormat.FULL)
						.format(eventList.get(position).startTime));
			}

			//fill in event times for entry
			if (start.get(Calendar.DAY_OF_YEAR) == end
					.get(Calendar.DAY_OF_YEAR)
					&& start.get(Calendar.YEAR) == end.get(Calendar.YEAR)) {
				// Start and end on the same day
				date.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
						.format(eventList.get(position).startTime)
						+ " - "
						+ DateFormat.getTimeInstance(DateFormat.SHORT).format(
								eventList.get(position).endTime));
			} else {
				// Start and end on different days
				date.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
						.format(eventList.get(position).startTime)
						+ " - "
						+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
								DateFormat.SHORT).format(
								eventList.get(position).endTime));
			}
			// grey out background if the event is before now
			if (eventList.get(position).endTime.before(new Date())) {
				rowV.findViewById(R.id.calendarEntryBack).setBackgroundColor(
						this.getContext().getResources()
								.getColor(android.R.color.darker_gray));
			}
		}
		return rowV;

	}
}
