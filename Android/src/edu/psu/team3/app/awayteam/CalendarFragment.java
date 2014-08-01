package edu.psu.team3.app.awayteam;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class CalendarFragment extends Fragment {

	Button todayButton;
	ImageButton addEventButton;
	ListView eventsListView;
	CalendarListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_calendar, container,
				false);
		// identify UI elements
		todayButton = (Button) rootView.findViewById(R.id.today_button);
		addEventButton = (ImageButton) rootView
				.findViewById(R.id.add_event_button);
		eventsListView = (ListView) rootView.findViewById(R.id.agenda_list);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// ensure list of events is sorted
		UserSession s = UserSession.getInstance(getActivity());
		Collections.sort(s.activeTeam.teamEvents, TeamEvent.StartComparator);
		// fill list
		adapter = new CalendarListAdapter(getActivity(), 0,
				s.activeTeam.teamEvents);
		// Attach the adapter to a ListView
		eventsListView.setAdapter(adapter);

		// position view so today is in sight
		ScrollToToday(false);
		// Assign listener to event clicks
		eventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View targetView,
					int position, long rowID) {

				// Create dialog to show details
				DialogFragment newFragment = new EventDetailDialog();
				Bundle args = new Bundle();
				args.putInt("position", position);
				newFragment.setArguments(args);
				newFragment.show(getFragmentManager(), null);
			}
		});

		// Assign actions to buttons
		todayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// find the first entry that is today or later
				ScrollToToday(true);
			}
		});

		addEventButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new EventCreateDialog();
				newFragment.show(getFragmentManager(), null);
			}
		});
	}

	private void ScrollToToday(boolean loud) {
		for (int i = 0; i < adapter.getCount(); i++) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Date today = cal.getTime();
			if (!adapter.getItem(i).startTime.before(today)) {

				
				if (loud) {
					eventsListView.setSelection(i);
					eventsListView.smoothScrollToPosition(i);
					
				}else{
					eventsListView.setSelectionFromTop(i, 0);
				}

				return;
			}
		}
		if (loud) {
			Toast.makeText(getActivity(), "No Events found after Today",
					Toast.LENGTH_SHORT).show();
		}
	}
}
