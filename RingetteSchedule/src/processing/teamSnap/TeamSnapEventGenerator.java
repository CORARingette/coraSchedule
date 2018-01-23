package processing.teamSnap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import model.Event;
import processing.AbstractTeamEventProcessor;
import schedule.ArenaMapper;
import schedule.Context;
import schedule.IceSpreadsheet;
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
			LOGGER.info(">>> TE: " + divisionEvent.getTeam().getName() + ":"
					+ divisionEvent.getCalendarStartDate().getTime() + ":" + divisionEvent.getLocation().getName());
			if (Config.getInstance().GetConfig(divisionEvent.getTeam().getName()) != null && Config.getInstance().GetConfig(divisionEvent.getTeam().getName()).isActive()) {
				existingScheduleEvents.add(new EventKey(divisionEvent));
			}
		}
	}

	public void reconcile() {
		if (ArenaMapper.getInstance().hasErrors()) {
			ArenaMapper.getInstance().dumpErrors();
			System.exit(-1);
		}

		LOGGER.info("Calculating schedule difference");
		Set<EventKey> eventsToAdd = new HashSet<EventKey>(updatedScheduleEvents);
		eventsToAdd.removeAll(existingScheduleEvents);

		boolean dataMissing = checkForMissingData(eventsToAdd);

		if (dataMissing) {
			System.err.println("Errors exist, exiting");
			System.exit(-1);
		}

		LOGGER.info("Adding events");
		// add the events
		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			String divison = Config.getInstance().GetConfig(event.getTeam()).getDivision();
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

		LOGGER.info("Done adding events");

		if (!isOKToDelete()) {
			System.err.println("Errors exist, exiting");
			System.exit(-1);
		}

		LOGGER.info("Calculating events to delete");

		Set<EventKey> eventsToRemove = new HashSet<EventKey>(existingScheduleEvents);
		eventsToRemove.removeAll(updatedScheduleEvents);

		LOGGER.info("Deleting events");
		// remove events
		for (EventKey eventToRemove : eventsToRemove) {
			DivisionEvent divisionEvent = (DivisionEvent) eventToRemove.getSource();

			if (Config.getInstance().GetConfig(divisionEvent.getTeam().getName()).isActive()) {
				if (!Context.dryRun) {
					divisionEvent.delete();
				} else {
					LOGGER.info("Deleting existing event: " + divisionEvent.getName() + ":"
							+ divisionEvent.getLocationId() + ":" + divisionEvent.getCalendarStartDate().getTime());
				}
			}
		}
		LOGGER.info("Done deleting events");

	}

	private boolean checkForMissingData(Set<EventKey> eventsToAdd) {
		boolean dataMissing = false;
		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			if (teamSnap.getLocationByName(event.getLocation()) == null) {
				LOGGER.severe("Location does not exist in TeamSnap: " + event.getLocation());
				dataMissing = true;
			}
			String divisonName = Config.getInstance().GetConfig(event.getTeam()).getDivision();
			Division division = teamSnap.getDivisionByName(divisonName);
			if (division == null) {
				LOGGER.severe("Division does not exist in TeamSnap: " + divisonName);
				dataMissing = true;
			} else {
				Team team = division.getTeamByName(event.getTeam());
				if (team == null) {
					LOGGER.severe("Team does not exist in TeamSnap: " + event.getTeam());
					dataMissing = true;
				}
			}

		}

		return dataMissing;
	}

	private boolean isOKToDelete() {
		boolean ok = true;
		// check we are only removing events for teams we recognize
		for (EventKey existingScheduleEvent : existingScheduleEvents) {
			DivisionEvent de = (DivisionEvent) existingScheduleEvent.getSource();
			if (Config.getInstance().GetConfig(de.getTeam().getName()) == null
					|| !IceSpreadsheet.getInstance().isValidTeam(de.getTeam().getName())) {
				LOGGER.severe("Team not found in configuration: " + de.getTeam().getName());
				ok = false;
				break;
			}
		}
		return ok;
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
		LOGGER.info(">>> IE: " + iceEvent.getTeam() + ":" + iceEvent.getFullDateTime() + ":" + iceEvent.getLocation());
		updatedScheduleEvents.add(eventKey);
	}

}
