package edu.psu.team3.app.awayteam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MemberDetailDialog extends DialogFragment {

	// UI elements
	TextView nameView;
	TextView managerView;
	TextView phoneView;
	TextView emailView;
	ImageButton smsButton;
	ImageButton callButton;
	ImageButton emailButton;

	TeamMember member;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// inflate custom view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.member_detail, null))
		// Add action buttons
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						MemberDetailDialog.this.getDialog().cancel();
					}
				});

		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog d = getDialog();
		// get target team member
		String userName = getArguments().getString("userName");
		member = UserSession.getInstance(getActivity()).activeTeam
				.getUser(userName);
		if (member == null) {
			Toast.makeText(getActivity(), "Team Member Not Found!",
					Toast.LENGTH_SHORT).show();
			d.dismiss();
		}
		// assign UI elements
		nameView = (TextView) d.findViewById(R.id.member_name_text);
		managerView = (TextView) d.findViewById(R.id.member_manager_text);
		phoneView = (TextView) d.findViewById(R.id.phone_text);
		emailView = (TextView) d.findViewById(R.id.email_text);
		smsButton = (ImageButton) d.findViewById(R.id.contact_text_button);
		callButton = (ImageButton) d.findViewById(R.id.contact_call_button);
		emailButton = (ImageButton) d.findViewById(R.id.contact_email_button);
		// fill data
		nameView.setText(member.firstName + " " + member.lastName);
		if (member.manager) {
			managerView.setVisibility(View.VISIBLE);
		}
		// format phone number if able
		String formattedPhone = "";
		switch (member.phone.length()) {
		case (10):
			formattedPhone = "(" + member.phone.substring(0, 3) + ")"
					+ member.phone.substring(3, 6) + "-"
					+ member.phone.substring(6);
			break;
		case (7):
			formattedPhone = member.phone.substring(0, 3) + "-"
					+ member.phone.substring(3);
			break;
		default:
			formattedPhone = member.phone;
		}
		phoneView.setText(formattedPhone);
		emailView.setText(member.email);
		// setup buttons
		
	}

}
