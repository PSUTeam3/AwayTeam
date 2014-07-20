package edu.psu.team3.app.awayteam;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class CommUtil {

	// Check if username is already being used
	// Returns: 0 = error
	// 1 = available
	// 2 = NOT available
	public static int LoginIDExist(Context context, String username) {
		String url = "https://api.awayteam.redshrt.com/user/LoginIDExist?loginId="
				+ username;

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}
		JSONObject result = null;
		// collect the results
		try {
			result = NetworkTasks.RequestData(false, url, null);
			if (result.getString("response").equals("success")) {
				if (result.getString("message").equals("available")) {
					// success & available
					return 1;
				} else {
					// success, but not available
					return 2;
				}
			} else {
				// returned failure, an error occured
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// Attempts to create a new user
	// INPUT: pairs = a name=value pairs list containing values for:
	// "loginId","email","password",
	// "firstName","lastName","cellPhone","emergencyPhone"
	// Returns int code based on success:
	// 1 = success! User created
	// 0 = unknown error or connection error
	// -999 = username already used
	// -998 = email already used
	public static int CreateNewUser(Context context, String username,
			String password, String firstName, String lastName, String email,
			String phone, String ePhone) {
		String url = "https://api.awayteam.redshrt.com/user/CreateUser";

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("loginId", username));
		pairs.add(new BasicNameValuePair("password", password));
		pairs.add(new BasicNameValuePair("firstName", firstName));
		pairs.add(new BasicNameValuePair("lastName", lastName));
		pairs.add(new BasicNameValuePair("email", email));
		pairs.add(new BasicNameValuePair("cellPhone", phone));
		if (ePhone == null) {
			pairs.add(new BasicNameValuePair("emergencyPhone", ""));
		} else {
			pairs.add(new BasicNameValuePair("emergencyPhone", ePhone));
		}

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("response").equals("success")) {
				// connection was good

				if (Integer.parseInt(result.getString("message")) > 0) {
					// User created and database ID returned
					// Authenticate user to store secret info from server

					int authSuccess = LoginUser(context, username, password,
							true);
					if (authSuccess == 1) {
						return 1;
					} else {
						return 0;
					}
				}
			} else if (!result.getString("message").isEmpty()) {
				// Return the error from the server - make sure these error
				// codes don't change because the UI will handle them as
				// expected in the original API
				return Integer.parseInt(result.getString("message"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// Attempts to modify a user account
	// INPUT:
	// "loginId","email",
	// "firstName","lastName","cellPhone","emergencyPhone"
	// Returns int code based on success:
	// 1 = success! User created
	// 0 = unknown error or connection error
	// -999 = username already used
	// -998 = email already used
	public static int ModifyUser(Context context, String username,
			String firstName, String lastName, String email, String phone,
			String ePhone) {
		String url = "https://api.awayteam.redshrt.com/user/modifyuser";

		List<NameValuePair> pairs = UserSession.getInstance(context)
				.createHash();
		pairs.add(new BasicNameValuePair("loginId", username));
		pairs.add(new BasicNameValuePair("firstName", firstName));
		pairs.add(new BasicNameValuePair("lastName", lastName));
		pairs.add(new BasicNameValuePair("email", email));
		pairs.add(new BasicNameValuePair("cellPhone", phone));
		if (ePhone == null) {
			pairs.add(new BasicNameValuePair("emergencyPhone", ""));
		} else {
			pairs.add(new BasicNameValuePair("emergencyPhone", ePhone));
		}

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("response").equals("true")) {
				// succeeded
				return 1;
			} else if (!result.getString("message").isEmpty()) {
				// Return the error from the server - make sure these error
				// codes don't change because the UI will handle them as
				// expected in the original API
				return Integer.parseInt(result.getString("message"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// Attempts to authenticate user - for use when user session is already
	// stored
	// INPUT: context, username, password
	// Returns int code based on success:
	// 1 = success! User logged in
	// 0 = unknown error or connection error
	// -1 = username not found
	// -2 = password incorrect
	public static int AuthenticateUser(Context context, String username,
			String password) {
		String url = "https://api.awayteam.redshrt.com/user/AuthenticatePassword";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("loginId", username));
		pairs.add(new BasicNameValuePair("password", password));
		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("response").equals("success")) {
				// collect secret to store with session data
				String userID = result.get("userIdentifier").toString();
				String userSecret = result.getString("userSecret").toString();
				Log.v("login", "User Identifier from Server: " + userID);
				Log.v("login", "User Secret from Server: " + userSecret);

				// save values to user session
				UserSession.getInstance(context).setUp(userID, userSecret);

				return 1;
			} else if (result.getString("response").equals("failure")) {
				switch (result.getString("message")) {
				case "bad password":
					return -2;
				case "password not submitted":
					return -2;
				case "user not found":
					return -1;
				case "user not submitted":
					return -1;
				default:
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// Attempts to log in user
	// INPUT: context, username, password, and the value of "remember me"
	// Returns int code based on success:
	// 1 = success! User logged in
	// 0 = unknown error or connection error
	// -1 = username not found
	// -2 = password incorrect
	public static int LoginUser(Context context, String username,
			String password, boolean remember) {
		String url = "https://api.awayteam.redshrt.com/user/AuthenticatePassword";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("loginId", username));
		pairs.add(new BasicNameValuePair("password", password));
		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("response").equals("success")) {
				// collect secret to store with session data
				String userID = result.get("userIdentifier").toString();
				String userSecret = result.getString("userSecret").toString();
				Log.v("login", "User Identifier from Server: " + userID);
				Log.v("login", "User Secret from Server: " + userSecret);

				// save all values to user session
				UserSession.getInstance(context).setUp(username, password,
						userID, userSecret, remember);

				return 1;
			} else if (result.getString("response").equals("failure")) {
				switch (result.getString("message")) {
				case "bad password":
					return -2;
				case "password not submitted":
					return -2;
				case "user not found":
					return -1;
				case "user not submitted":
					return -1;
				default:
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// Changes the password of the selected user
	// INPUTS: username - user readable login ID
	// newPass - new password
	// RETURN: 1 = success, password updated
	// 0 = unknown error or connection failure
	// -1 = username not found
	// -2 = username missing
	public static int ChangePassword(String username, String newPass,
			Context context) {
		String url = "https://api.awayteam.redshrt.com/user/changepassword";
		List<NameValuePair> pairs = null;
		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		pairs = UserSession.getInstance(context).createHash();
		pairs.add(new BasicNameValuePair("loginId", username));
		pairs.add(new BasicNameValuePair("newPassword", newPass));
		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("response").equals("success")) {
				// Update user session password
				UserSession s = UserSession.getInstance(context);
				s.changePassword(newPass);
				// reauthenticate with new credentials
				int authSuccess = AuthenticateUser(context, s.getUsername(),
						s.getPassword());
				if (authSuccess == 1) {
					return 1;
				} else {
					return 0;
				}
			} else if (result.getString("response").equals("failure")) {
				switch (result.getString("message")) {
				case "user not found":
					return -1;
				case "user not submitted":
					return -2;
				default:
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// Create a new team
	// INPUTS: team name, description, location name, lat and lon, managed
	// RETURN: 1 = success, team created
	// 0 = unknown error or connection failure
	// -1 = team name already used
	public static int CreateTeam(Context context, String teamName,
			String locationName, String description, boolean managed) {
		String url = "https://api.awayteam.redshrt.com/team/createteam";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		List<NameValuePair> pairs = UserSession.getInstance(context)
				.createHash();
		pairs.add(new BasicNameValuePair("loginId", UserSession.getInstance(
				context).getUsername()));
		pairs.add(new BasicNameValuePair("teamName", teamName));
		pairs.add(new BasicNameValuePair("teamDescription", description));
		pairs.add(new BasicNameValuePair("teamLocationName", locationName));
		pairs.add(new BasicNameValuePair("teamManaged", Boolean
				.toString(managed)));

		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("status").equals("success")) {
				return result.getJSONObject("response").getInt("teamId");
			} else if (result.getString("status").equals("failure")) {
				switch (result.getString("response")) {
				case "Team Name Already Used":
					return -1;
				default:
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// Joins a team selected by the user
	// INPUTS: team id, username
	// RETURN: 1 = success! user added to team or added to pending for managed
	// teams
	// 0 = unknown error or connection failure
	// -1 = team does not exist
	public static int JoinTeam(Context context, int teamID, String userName) {
		String url = "https://api.awayteam.redshrt.com/teammember/jointeam";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		List<NameValuePair> pairs = UserSession.getInstance(context)
				.createHash();
		pairs.add(new BasicNameValuePair("loginId", UserSession.getInstance(
				context).getUsername()));
		pairs.add(new BasicNameValuePair("teamId", Integer.toString(teamID)));

		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("status").equals("success")) {
				return 1;
			} else if (result.getString("status").equals("failure")) {
				if (result.getString("response") == "team id does not exist") {
					return -1;
				} else {
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// Collect the list of all teams on the system
	// RETURN: list of all the teams formatted as: id,teamname,location,managed
	public static List<Object[]> GetAllTeamsList(Context context) {
		String url = "https://api.awayteam.redshrt.com/team/getallteams";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return null;
		}

		List<NameValuePair> pairs = UserSession.getInstance(context)
				.createHash();

		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("status").equals("success")) {
				// collect data and pass array to display list
				List<Object[]> teamList = new ArrayList<Object[]>();
				JSONArray response = result.getJSONArray("response");
				for (int i = 0; i < response.length(); i++) {
					int id = response.getJSONObject(i).getInt("teamId");
					String name = response.getJSONObject(i).getString(
							"teamName");
					String location = response.getJSONObject(i).getString(
							"teamLocationName");
					if (location.equals("null")) {
						location = "";
					}
					boolean managed = response.getJSONObject(i)
							.getString("teamManaged").equals("1"); // this is
																	// kind of a
																	// hack -
																	// should be
																	// fixed
					teamList.add(new Object[] { id, name, location, managed });
				}
				return teamList;
			} else if (result.getString("status").equals("failure")) {
				// only error is no teams available
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Get all the information about a team
	// This call will update the user session and build an Active Team
	// INPUTS: team name, description, location name, lat and lon, managed
	// RETURN: 1 = success, team created
	// 0 = unknown error or connection failure
	// -1 = error is a permission problem (user is not a member of the
	// team), where a different message should be given
	public static int GetTeam(Context context, int teamID, String userName) {
		String url = "https://api.awayteam.redshrt.com/team/getteam";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		List<NameValuePair> pairs = UserSession.getInstance(context)
				.createHash();
		pairs.add(new BasicNameValuePair("teamId", String.valueOf(teamID)));
		pairs.add(new BasicNameValuePair("loginId", userName));

		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("status").equals("success")) {
				UserSession.getInstance(context).loadTeam(
						result.getJSONObject("response"));
				return 1;
			} else if (result.getString("status").equals("failure")) {
				if (result.getString("message").equals("Access Denied")) {
					// user should not access this team
					return -1;
				}
				// some other error occurred
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// Collect the list of teams the user is a member of
	// RETURN: 1 if successful, 0 if error
	// ACTIONS: updates the teamList in the UserSession if successful
	public static int GetMemberTeamsList(Context context, String userName) {
		String url = "https://api.awayteam.redshrt.com/team/getteamlist";

		if (!NetworkTasks.NetworkAvailable(context)) {
			return 0;
		}

		List<NameValuePair> pairs = UserSession.getInstance(context)
				.createHash();
		pairs.add(new BasicNameValuePair("loginId", userName));

		JSONObject result = null;

		try {
			result = NetworkTasks.RequestData(true, url, pairs);
			if (result.getString("status").equals("success")) {
				// collect data and pass array to UserSession
				List<Object[]> teamList = new ArrayList<Object[]>();
				JSONArray response = result.getJSONArray("response");
				for (int i = 0; i < response.length(); i++) {
					int id = response.getJSONObject(i).getInt("teamId");
					String name = response.getJSONObject(i).getString(
							"teamName");
					teamList.add(new Object[] { id, name });
				}
				UserSession.getInstance(context).teamList = teamList;
				return 1;
			} else if (result.getString("status").equals("failure")) {
				// only error is no teams available
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// Get all the information about a user
	// INPUTS: username
	// RETURN: a list of strings in order:
	// FirstName,LastName,Email,Phone,EmerPhone
	// null = error encountered
	public static List<String> GetUser(Context context, String userName) {
		String url = "https://api.awayteam.redshrt.com/user/getuser"
				+ "?loginId=" + userName;

		if (!NetworkTasks.NetworkAvailable(context)) {
			return null;
		}

		JSONObject result = null;
		List<String> user = new ArrayList<String>();

		try {
			result = NetworkTasks.RequestData(false, url, null);
			if (result.getString("response").equals("success")) {
				JSONObject message = result.getJSONObject("message");
				user.add(message.getString("firstName"));
				user.add(message.getString("lastName"));
				user.add(message.getString("email"));
				user.add(message.getString("cellPhone"));
				user.add(message.getString("emergencyPhone"));
				return user;
			} else if (result.getString("response").equals("failure")) {
				// an error occurred
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
