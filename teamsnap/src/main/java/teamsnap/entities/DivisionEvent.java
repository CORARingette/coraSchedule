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

	public DivisionEvent(int divisionId, int teamId, int locationId, String name, Date gmtStartDate, boolean isGame) {
		this();
		addAttribute("name", name);
		addAttribute("event_type", EventType.PRACTICE.getDescription());
		addAttribute("start_date", gmtStartDate);
		addAttribute("division_id", divisionId);
		addAttribute("location_id", locationId);
		addAttribute("team_id", teamId);
		addAttribute("icon_color", isGame ? "red" : "green");
		addAttribute("label", isGame ? "GAME" : "PRACTICE");
		addAttribute("duration_in_minutes", 60);
		addAttribute("minutes_to_arrive_early", isGame ? 60 : 30);
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Integer getTeamId() {
		return (Integer) attributes.get("team_id");
	}

	public String getName() {
		return (String) attributes.get("name");
	}

	public DivisionLocation getLocation() {
		return location;
	}

	public void setLocation(DivisionLocation location) {
		this.location = location;
	}

	public Integer getLocationId() {
		return (Integer) attributes.get("division_location_id");
	}
	
	public EventType getEventType()
	{
		// kludge for now
		return ((String) attributes.get("label")).equals("GAME")? EventType.GAME: EventType.PRACTICE;
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
