package teamsnap.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import teamsnap.apiClient.Item;
import teamsnap.apiClient.Query;
import teamsnap.main.Constants;

public class League extends BaseEntity {

	protected static final Logger LOGGER = Logger.getLogger(League.class.getName());

	private List<Division> divisions = new ArrayList<Division>();
	private List<DivisionLocation> locations = new ArrayList<DivisionLocation>();

	public League() {
		super("https://api.teamsnap.com/v3/divisions");

		Query query = new Query("https://api.teamsnap.com/v3/division_locations/search");
		for (Item item : query.execute("division_id", Constants.MY_LEAGUE).getItems()) {
			locations.add(new DivisionLocation(item));
		}

		query = new Query("https://api.teamsnap.com/v3/divisions/search");
		for (Item item : query.execute("parent_id", Constants.MY_LEAGUE).getItems()) {
			Division division = new Division(this, item);
			division.dump();
			if (!division.isArchived()) {
				divisions.add(division);
			}
		}

	}

	public DivisionLocation getLocationByName(String name) {
		return locations.stream().filter(d -> d.getName().equals(name)).findFirst().orElse(null);
	}

	public Division getDivisionByName(String name) {
		return divisions.stream().filter(d -> d.getName().equals(name)).findFirst().orElse(null);
	}

	public List<DivisionEvent> getAllEvents() {
		List<DivisionEvent> events = new ArrayList<DivisionEvent>();

		for (Division division : divisions) {
			events.addAll(division.getEvents());
		}

		return events;
	}

	public List<DivisionLocation> getLocations() {
		return locations;
	}

	public List<Division> getAllDivisions() {
		return divisions;
	}
}
