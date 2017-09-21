package processing.teamSnap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import model.Event;
import processing.AbstractTeamEventProcessor;
import processing.Utils;
import schedule.ArenaMapper;
import schedule.Context;
import teamsnap.entities.Division;
import teamsnap.entities.DivisionEvent;
import teamsnap.entities.DivisionLocation;
import teamsnap.entities.League;
import teamsnap.entities.Team;
import utils.Config;

public class TeamSnapEventGenerator extends AbstractTeamEventProcessor {
	private static final Logger LOGGER = Logger.getLogger(TeamSnapEventGenerator.class.getName());

	private List<DivisionEvent> allEvents;
	private League teamSnap;
	private Set<EventKey> existingScheduleEvents = new HashSet<EventKey>();
	private Set<EventKey> updatedScheduleEvents = new HashSet<EventKey>();

	public TeamSnapEventGenerator() {
		teamSnap = new League();
		allEvents = teamSnap.getAllEvents();
		for (DivisionEvent divisionEvent : allEvents) {
			existingScheduleEvents.add(new EventKey(divisionEvent));
		}
	}

	public void reconcile() {
		if (ArenaMapper.getInstance().hasErrors()) {
			ArenaMapper.getInstance().dumpErrors();
			System.exit(-1);
		}
		Set<EventKey> eventsToAdd = new HashSet<EventKey>(updatedScheduleEvents);
		eventsToAdd.removeAll(existingScheduleEvents);

		boolean dataMissing = checkForMissingData(eventsToAdd);

		if (dataMissing) {
			System.err.println("Errors exist, exiting");
			System.exit(-1);
		}

		// add the events
		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			String divison = Config.instance.GetConfig(event.getTeam()).getDivision();
			Division division = teamSnap.getDivisionByName(divison);
			Team team = division.getTeamByName(event.getTeam());
			DivisionLocation location = teamSnap.getLocationByName(event.getLocation());
			DivisionEvent divisionEvent = new DivisionEvent(Integer.valueOf(division.getDivisionId()),
					Integer.valueOf(team.getTeamId()), Integer.valueOf(location.getLocationId()), event.getSummary(),
					event.getFullDateTime(), event.isGame());
			if (!Context.dryRun) {
				divisionEvent.create();
			} else {
				LOGGER.info("Adding new event: " + division.getName() + ":" + team.getName() + ":" + location.getName()
						+ ":" + event.getFullDateTime() + ":" + event.isGame() + ":" + event.getSummary());
			}
		}

		Set<EventKey> eventsToRemove = new HashSet<EventKey>(existingScheduleEvents);
		eventsToRemove.removeAll(updatedScheduleEvents);

		// remove events
		for (EventKey eventToRemove : eventsToRemove) {
			DivisionEvent divisionEvent = (DivisionEvent) eventToRemove.getSource();
			if (!Context.dryRun) {
				divisionEvent.delete();
			} else {
				LOGGER.info("Deleting existing event: " + divisionEvent.getTeamId() + ":"
						+ divisionEvent.getLocationId() + ":" + divisionEvent.getCalendarStartDate());
			}
		}

	}

	private boolean checkForMissingData(Set<EventKey> eventsToAdd) {
		boolean dataMissing = false;
		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			if (teamSnap.getLocationByName(event.getLocation()) == null) {
				System.err.println("Location does not exist in TeamSnap: " + event.getLocation());
				dataMissing = true;
			}
			String divisonName = Config.instance.GetConfig(event.getTeam()).getDivision();
			Division division = teamSnap.getDivisionByName(divisonName);
			if (division == null) {
				System.err.println("Division does not exist in TeamSnap: " + divisonName);
				dataMissing = true;
			} else {
				Team team = division.getTeamByName(event.getTeam());
				if (team == null) {
					System.err.println("Team does not exist in TeamSnap: " + event.getTeam());
					dataMissing = true;
				}
			}

		}
		return dataMissing;
	}

	@Override
	protected void preProcess(String team) {

	}

	@Override
	protected void postProcess(String team) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void process(Event iceEvent) {
		EventKey eventKey = new EventKey(iceEvent);
		updatedScheduleEvents.add(eventKey);
	}

}
