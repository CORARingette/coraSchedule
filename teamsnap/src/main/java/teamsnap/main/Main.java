package teamsnap.main;

import teamsnap.entities.League;

public class Main {

	public static void main(String[] args) {

		try {
			// HttpResponse<JsonNode> eventListResponse =
			// Unirest.get("https://api.teamsnap.com/v3/divisions/search")
			// .header("Authorization", "Bearer " +
			// Authorization.instance().getToken())
			// .queryString("user_id", Constants.ME)
			// .asJson();
			//
			// System.err.println(eventListResponse.getBody().toString());

			// HttpResponse<JsonNode> eventListResponse =
			// Unirest.get("https://api.teamsnap.com/v3/division_events/search")
			// .header("Authorization", "Bearer " +
			// Authorization.instance().getToken())
			// .queryString("division_id", 219320).asJson();
			// System.err.println(eventListResponse.getBody().toString());
			//
			// DivisionEvent de = new
			// DivisionEvent(Integer.valueOf(Constants.ADMIN_DIVISION),
			// Integer.valueOf(Constants.TEST_TEAM_ID),
			// Integer.valueOf(Constants.BREWER_ID),
			// "Home Game vs Montreal Mission v1", new Date(), true);
			// de.create();
			//
			// de.delete();
			// DivisionLocation dl = new DivisionLocation(Constants.MY_LEAGUE,
			// "Test1", "75 Kimberwick Cres",
			// "http://google.ca");
			// dl.create();
			//
			// TeamEvent e = new TeamEvent();
			// e.addAttribute("start_date", new Date());
			// e.addAttribute("location_name", "Brewer");
			// e.addAttribute("location_id", 28042942);
			// e.addAttribute("team_id", new Integer(3239865));
			// e.addAttribute("name", "Power skating");
			// e.addAttribute("is_game", Boolean.FALSE);
			// e.create();
			League league = new League();
			System.err.println(league);
			//
			// new Event().getCreateFieldMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
