package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Team {

	public String name = "Team Name!";
	public String description = "";
	public String location = "";
	public int lat, lon = 0;

	public List<TeamMember> teamMembers = new ArrayList<TeamMember>();
	public List<TeamEvent> teamEvents = new ArrayList<TeamEvent>();
	public List<TeamTask> teamTasks = new ArrayList<TeamTask>();

	//Create a team from a JSON Object
	public void importTeam(JSONObject teamObject){
		//collect team info
		
	}


	// return a list of managers
	public List<TeamMember> getManagers() {
		List<TeamMember> managers = new ArrayList<TeamMember>();
		for (TeamMember member : teamMembers) {
			if (member.manager) {
				managers.add(member);
			}
		}
		return managers;
	}
	
	// remove completed tasks
	public void removeCompletedTasks(){
		for (TeamTask task:teamTasks){
			if(task.complete){
				teamTasks.remove(task);
			}
		}
	}

}
