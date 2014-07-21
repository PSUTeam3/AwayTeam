package edu.psu.team3.app.awayteam;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

public class TaskFragment extends Fragment {

	Button mExpandButton;
	ImageButton mAddButton;
	ExpandableListView mTaskListView;
	TaskListAdapter adapter;
	boolean expand = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tasks, container,
				false);

		// register UI
		mExpandButton = (Button) rootView
				.findViewById(R.id.expand_toggle_button);
		mAddButton = (ImageButton) rootView.findViewById(R.id.add_task_button);
		mTaskListView = (ExpandableListView) rootView
				.findViewById(R.id.taskListView);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// Fill in listview
		adapter = new TaskListAdapter(getActivity(),
				UserSession.getInstance(getActivity()).activeTeam.teamTasks);
		// Attach the adapter to a ListView
		mTaskListView.setAdapter(adapter);

		// Assign tasks to buttons
		mExpandButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (expand) {
					for (int i = 0; i < adapter.getGroupCount(); i++) {
						mTaskListView.expandGroup(i);
					}
					mExpandButton.setText("Collapse All");
				} else {
					for (int i = 0; i < adapter.getGroupCount(); i++) {
						mTaskListView.collapseGroup(i);
					}
					mExpandButton.setText("Expand All");
				}
				expand = !expand;

			}
		});
		//TODO: implement add button
	}
}
