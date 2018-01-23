package processing.teamSnap;

import java.util.Date;

import model.Event;
import teamsnap.entities.DivisionEvent;
import utils.DateTimeUtils;

public class EventKey {

	enum EventType {
		Game, Practice
	}

	private final String team;
	private final String location;
	private final Date eventDate;
	private final EventType eventType;
	private final Object source;

	public EventKey(Event iceEvent) {
		this.team = iceEvent.getTeam();
		this.location = iceEvent.getLocation();
		this.eventDate = DateTimeUtils.makeFullDateFromDateAndTime(iceEvent.getDate(), iceEvent.getTime());
		this.eventType = iceEvent.isGame() ? EventType.Game : EventType.Practice;
		source = iceEvent;
	}

	public EventKey(DivisionEvent divisionEvent) {
		this.team = divisionEvent.getTeam().getName();
		this.location = divisionEvent.getLocation().getName();
		this.eventDate = divisionEvent.getCalendarStartDate().getTime();
		this.eventType = divisionEvent.getEventType() == teamsnap.entities.EventType.GAME? EventType.Game : EventType.Practice;
		source = divisionEvent;
	}

	public String getKey() {
		return this.team + "|" + this.location + "|" + this.eventDate.getTime() + "|" + this.eventType;
	}

	public Object getSource() {
		return source;
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EventKey)) {
			return false;
		} else {
			EventKey eventKey = (EventKey) obj;
			return eventKey.getKey().equals(getKey());
		}
	}

}
