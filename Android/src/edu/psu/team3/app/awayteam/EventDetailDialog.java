package edu.psu.team3.app.awayteam;

import java.text.DateFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class EventDetailDialog extends DialogFragment {
	TeamEvent event;

	TextView titleV;
	TextView startV;
	TextView endV;
	TextView locationV;
	TextView descriptionV;
	Button editButton;
	ImageButton deleteButton;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// inflate custom view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_event_detail, null))
		// Add action buttons
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						EventDetailDialog.this.getDialog().cancel();
					}
				});

		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog d = getDialog();
		// get target team event
		event = UserSession.getInstance(getActivity()).activeTeam.teamEvents
				.get(getArguments().getInt("position"));

		// assing UI elements
		titleV = (TextView) d.findViewById(R.id.event_title_text);
		startV = (TextView) d.findViewById(R.id.calendar_start_time_text);
		endV = (TextView) d.findViewById(R.id.calendar_end_time_text);
		locationV = (TextView) d.findViewById(R.id.calendar_location_text);
		descriptionV = (TextView) d
				.findViewById(R.id.calendar_description_text);
		editButton = (Button) d.findViewById(R.id.calendar_edit_button);
		deleteButton = (ImageButton) d
				.findViewById(R.id.calendar_delete_button);

		// fill data
		titleV.setText(event.title);
		startV.setText("Start: "
				+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.SHORT).format(event.startTime));
		endV.setText("  End: "+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.SHORT).format(event.endTime));
		locationV.setText(event.location);
		descriptionV.setText(event.description);

		// Assign tasks to buttons
		// TODO: add button actions

	}
}
