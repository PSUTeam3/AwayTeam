package edu.psu.team3.app.awayteam;

import java.util.Comparator;
import java.util.Date;

public class TeamEvent {
	public String title = "title";
	public String description = "";
	public String location = "";
	public int lat, lon = 0;
	public Date startTime = null;
	public Date endTime = null;

	public TeamEvent(String nTitle, String nDesc, String locName, int nLat,
			int nLon, Date start, Date end) {
		title = nTitle;
		description = nDesc;
		location = locName;
		lat = nLat;
		lon = nLon;
		startTime = start;
		endTime = end;
	}

	public TeamEvent() {

	}
	
	public static Comparator<TeamEvent> StartComparator = new Comparator<TeamEvent>() {

		@Override
		public int compare(TeamEvent a, TeamEvent b) {
			return a.startTime.compareTo(b.startTime);
		}

	};
}
