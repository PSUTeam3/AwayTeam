package edu.psu.team3.app.awayteam;

import java.util.Comparator;

public class TeamTask {

	public String title = "title";
	public String description = "";
	public boolean complete = false;

	public TeamTask(String nTitle, String nDescription, boolean nComplete) {
		title = nTitle;
		description = nDescription;
		complete = nComplete;
	}
	
	public TeamTask(){
		
	}
	
	// compare tasks alphabetically based on title
	public static Comparator<TeamTask> AlphaComparator = new Comparator<TeamTask>() {

		@Override
		public int compare(TeamTask a, TeamTask b) {
			return a.title.compareTo(b.title);
		}

	};
	
	//Compare completed to incomplete tasks, placing completed tasks as "greater" so stacked to the bottom
	public static Comparator<TeamTask> CompletedComparator = new Comparator<TeamTask>() {

		@Override
		public int compare(TeamTask a, TeamTask b) {
			if (a.complete==b.complete){
				return 0;
			}else if(a.complete){
				return 1;
			}else{
				return -1;
			}
		}

	};
}
