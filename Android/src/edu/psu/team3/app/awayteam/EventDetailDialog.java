package edu.psu.team3.app.awayteam;

import java.text.DateFormat;

import edu.psu.team3.app.awayteam.EventCreateDialog.CreateEventTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetailDialog extends DialogFragment {
	private DeleteEventTask mDeleteTask = null;

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
		endV.setText("  End: "
				+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.SHORT).format(event.endTime));
		locationV.setText(event.location);
		descriptionV.setText(event.description);

		// Assign tasks to buttons
		editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new EventEditDialog();
				Bundle args = new Bundle();
			    args.putInt("id",event.id);
			    newFragment.setArguments(args);
				newFragment.show(getFragmentManager(), null);
				getDialog().dismiss();
			}
		});
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDeleteTask == null) {
					mDeleteTask = new DeleteEventTask();
					mDeleteTask.execute();
				}

			}
		});

	}
	
	public class DeleteEventTask extends AsyncTask<Object, Void, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {
			UserSession s = UserSession.getInstance(getActivity());
			Integer result = 0;
			result = CommUtil.DeleteEvent(getActivity(), s.getUsername(),
					s.currentTeamID, event.id);

			Log.v("Background", "returned from commutil.  result = " + result);

			return result;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mDeleteTask = null;
			if (result == 1) {// success!
				Toast.makeText(getActivity().getBaseContext(),
						"Event Deleted", Toast.LENGTH_SHORT).show();
				// callback the team id
				
				((DisplayActivity) getActivity()).refreshTeam(UserSession
						.getInstance(getActivity()).currentTeamID);
				getDialog().dismiss();
				
			} else {// some error occured
				Toast.makeText(getActivity().getBaseContext(),
						"Unable to Delete Event", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			mDeleteTask = null;
		}
	}
}
