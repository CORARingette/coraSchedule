package teamsnap.entities;

import java.util.ArrayList;
import java.util.List;

import teamsnap.apiClient.Item;
import teamsnap.apiClient.Query;

public class Team extends BaseEntity {

	private final List<TeamEvent> events = new ArrayList<TeamEvent>();

	public Team(Item item) {
		super("https://api.teamsnap.com/v3/teams");

		makeFromResponse(item);

		Query query = new Query("https://api.teamsnap.com/v3/events/search");
		for (Item eventItem : query.execute("team_id", getTeamId()).getItems()) {
			events.add(new TeamEvent(eventItem));
		}
	}

	public String getName() {
		return (String) attributes.get("name");
	}

	public String getTeamId() {
		return (String) attributes.get("id");
	}
}
