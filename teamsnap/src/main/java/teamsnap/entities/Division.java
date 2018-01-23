package teamsnap.entities;

import java.util.ArrayList;
import java.util.List;

import teamsnap.apiClient.Item;
import teamsnap.apiClient.Query;

public class Division extends BaseEntity {

	private final List<Team> teams = new ArrayList<Team>();
	private final List<DivisionEvent> events = new ArrayList<DivisionEvent>();

	public Division(League league, Item item) {
		super("https://api.teamsnap.com/v3/divisions");

		makeFromResponse(item);

		if (!isArchived()) {

			// load teams
			Query query = new Query("https://api.teamsnap.com/v3/teams/search");
			for (Item teamItem : query.execute("division_id", getDivisionId()).getItems()) {
				teams.add(new Team(teamItem));
			}

			// load events
			query = new Query("https://api.teamsnap.com/v3/division_events/search");
			for (Item eventItem : query.execute("division_id", getDivisionId()).getItems()) {
				DivisionEvent event = new DivisionEvent(eventItem);
				Team team = teams.stream().filter(t -> t.getTeamId().equals(event.getTeamId())).findAny().orElse(null);
				event.setTeam(team);
				DivisionLocation location = league.getLocations().stream()
						.filter(l -> l.getLocationId().equals(event.getLocationId())).findAny().orElse(null);
				event.setLocation(location);
				events.add(event);
			}
		}
	}

	public Integer getDivisionId() {
		return (Integer) attributes.get("id");
	}

	public String getName() {
		return (String) attributes.get("name");
	}

	public boolean isArchived() {
		return attributes.get("is_archived") != null && attributes.get("is_archived").equals("true");
	}

	public List<Team> getTeams() {
		return teams;
	}

	public List<DivisionEvent> getEvents() {
		return events;
	}

	public Team getTeamByName(String name) {
		return teams.stream().filter(t -> t.getName().equals(name)).findAny().orElse(null);
	}
}
