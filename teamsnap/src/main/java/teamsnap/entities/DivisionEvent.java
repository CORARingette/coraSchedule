package teamsnap.entities;

import java.util.Calendar;
import java.util.Date;

import teamsnap.apiClient.Item;
import teamsnap.main.Constants;

public class DivisionEvent extends BaseEntity {


	private Team team;
	private DivisionLocation location;

	private DivisionEvent() {
		super("https://api.teamsnap.com/v3/division_events");
	}

	public DivisionEvent(Item item) {
		this();
		makeFromResponse(item);
	}

	public DivisionEvent(int divisionId, int teamId, int locationId, String name, EventType eventType, Date gmtStartDate) {
		this();
		addAttribute("name", name);
		addAttribute("event_type", eventType.getDescription());
		addAttribute("start_date", gmtStartDate);
		addAttribute("division_id", divisionId);
		addAttribute("location_id", locationId);
		addAttribute("team_id", teamId);
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public String getTeamId() {
		return (String) attributes.get("team_id");
	}

	public DivisionLocation getLocation() {
		return location;
	}

	public void setLocation(DivisionLocation location) {
		this.location = location;
	}

	public String getLocationId() {
		return (String) attributes.get("division_location_id");
	}

	public Calendar getCalendarStartDate() {
		Calendar result = null;
		try {
			String calendarStr = (String) attributes.get("start_date");
			Date startDate = Constants.DATE_FORMAT.parse(calendarStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(startDate.getTime());
			result = calendar;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
