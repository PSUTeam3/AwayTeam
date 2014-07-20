package edu.psu.team3.app.awayteam;

import java.util.Comparator;

public class TeamMember {

	public String firstName = "";
	public String lastName = "";
	public String email = "";
	public String phone = "";
	public int lat, lon = 0;
	public boolean manager = false;

	public TeamMember(String mFirstName, String mLastName, String mEmail,
			String mPhone, int mLat, int mLon, boolean mManager) {
		firstName = mFirstName;
		lastName = mLastName;
		email = mEmail;
		phone = mPhone;
		lat = mLat;
		lon = mLon;
		manager = mManager;
	}

	public TeamMember() {

	}

	public static Comparator<TeamMember> FirstNameComparator = new Comparator<TeamMember>() {

		@Override
		public int compare(TeamMember a, TeamMember b) {
			return a.firstName.compareTo(b.firstName);
		}

	};
	public static Comparator<TeamMember> LastNameComparator = new Comparator<TeamMember>() {

		@Override
		public int compare(TeamMember a, TeamMember b) {
			return a.lastName.compareTo(b.lastName);
		}

	};
}