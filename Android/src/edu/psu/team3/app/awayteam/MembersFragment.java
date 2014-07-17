package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import edu.psu.team3.app.awayteam.JoinTeamDialog.JoinTask;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MembersFragment extends Fragment {

	// UI references
	private Spinner mGroupSpinner;
	private Spinner mManagerSpinner;
	private ListView mMemberListView;
	private Button mContactManagersButton;
	private Button mContactMembersButton;

	// list for spinners
	List<String> modeList = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_members, container,
				false);

		// register UI
		mGroupSpinner = (Spinner) rootView
				.findViewById(R.id.group_contact_mode_spinner);
		mManagerSpinner = (Spinner) rootView
				.findViewById(R.id.manager_contact_mode_spinner);
		mMemberListView = (ListView) rootView.findViewById(R.id.memberListView);
		mContactManagersButton = (Button) rootView
				.findViewById(R.id.manager_contact_button);
		mContactMembersButton = (Button) rootView
				.findViewById(R.id.group_contact_button);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		UserSession s = UserSession.getInstance(getActivity());
		// assign actions to contact buttons and hide manager display if
		// required
		if (s.activeTeam.managed) {
			mContactManagersButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int mode = mManagerSpinner.getSelectedItemPosition();
					List<TeamMember> contacts = UserSession
							.getInstance(getActivity()).activeTeam
							.getManagers();
					contact(mode, contacts);
				}

			});
		} else {
			// not managed, hide unused UI elements
			mContactManagersButton.setVisibility(View.GONE);
			mManagerSpinner.setVisibility(View.GONE);
		}
		mContactMembersButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int mode = mGroupSpinner.getSelectedItemPosition();
				List<TeamMember> contacts = UserSession
						.getInstance(getActivity()).activeTeam.teamMembers;
				contact(mode, contacts);
			}

		});

		// build list of members
		MemberListAdapter adapter = new MemberListAdapter(getActivity(), 0,
				s.activeTeam.teamMembers);
		// Attach the adapter to a ListView
		mMemberListView.setAdapter(adapter);
		
		
		// Setup listener for list selections
		mMemberListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View targetView,
					int position, long rowID) {
				String userName = ((TeamMember)adapter.getItemAtPosition(position)).userName;
				// take action to display selected user's detail
				DialogFragment newFragment = new MemberDetailDialog();
				Bundle args = new Bundle();
			    args.putString("userName",userName);
			    newFragment.setArguments(args);
				newFragment.show(getFragmentManager(), null);

			}

		});

	}

	// dispatches contact command to system handler
	@SuppressLint("NewApi")
	private void contact(int mode, List<TeamMember> contacts) {
		// figure out the mode of contact
		String contactMode = getResources().getStringArray(
				R.array.contact_options)[mode];

		switch (contactMode) {
		case "Email":
			// collect array of email addresses
			StringBuilder emails = new StringBuilder("mailto:");
			for (TeamMember member : contacts) {
				emails.append(member.email + ";");
			}
			// create intent
			Intent emailIntent = new Intent();
			emailIntent.setData(Uri.parse(emails.toString()));
			emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			emailIntent.putExtra(Intent.EXTRA_SUBJECT,
					UserSession.getInstance(getActivity()).activeTeam.name);
			// launch intent
			try {
				startActivity(emailIntent);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(getActivity(),
						"There is no email client installed.",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case "SMS":
			StringBuilder numbers = new StringBuilder("smsto:");
			for (TeamMember member : contacts) {
				numbers.append(member.phone + ";");
			}
			// create intent
			Intent smsIntent = new Intent();
			smsIntent.setData(Uri.parse(numbers.toString()));
			smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// launch intent
			try {
				startActivity(smsIntent);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(getActivity(),
						"There is no messaging client installed.",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case "Call":
			final StringBuilder phone = new StringBuilder("tel:");
			// can only call one number, so ensure that only one is selected
			if (contacts.size() > 1) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Select a Number to Call");
				final ArrayAdapter<String> adapter = new ArrayAdapter<>(
						getActivity(),
						android.R.layout.select_dialog_singlechoice);
				for (TeamMember member : contacts) {
					adapter.add(member.phone);
				}
				// builder.setNegativeButton("cancel",
				// new DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				// }
				// });

				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int selected) {
								phone.append(adapter.getItem(selected));
							}
						});
				builder.show();

			} else {
				phone.append(contacts.get(0).phone);
			}
			// create intent
			Intent dialIntent = new Intent(Intent.ACTION_DIAL);
			dialIntent.setData(Uri.parse(phone.toString()));
			dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// launch intent
			try {
				startActivity(dialIntent);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(getActivity(),
						"There is no phone client installed.",
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			Toast.makeText(getActivity(), "Unable to find contact mode",
					Toast.LENGTH_SHORT).show();
			return;
		}

	}

}
