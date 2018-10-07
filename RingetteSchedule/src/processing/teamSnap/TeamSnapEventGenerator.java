package processing.teamSnap;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

import model.Event;
import processing.AbstractTeamEventProcessor;
import schedule.ArenaMapper;
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

	// Events that are the existing schedule, pulled from TS model
	private Set<EventKey> existingScheduleEvents = new HashSet<EventKey>();

	// Events that have been pulled from new schedule
	private Set<EventKey> updatedScheduleEvents = new HashSet<EventKey>();

	// Events we need to add to TS
	Set<EventKey> eventsToAdd = Collections.emptySet();

	// Events to remove from TS
	Set<EventKey> eventsToRemove = Collections.emptySet();

	public TeamSnapEventGenerator() {
		teamSnap = new League();
		allEvents = teamSnap.getAllEvents();
		for (DivisionEvent divisionEvent : allEvents) {
			LOGGER.info(">>> TE: " + divisionEvent.getTeam().getName() + ":"
					+ divisionEvent.getCalendarStartDate().getTime() + ":" + divisionEvent.getLocation().getName());
			if (Config.getInstance().GetConfig(divisionEvent.getTeam().getName()) != null
					&& Config.getInstance().GetConfig(divisionEvent.getTeam().getName()).isActive()) {
				existingScheduleEvents.add(new EventKey(divisionEvent));
			}
		}
	}

	private void calculateDifference() {
		LOGGER.info("Calculating schedule difference");

		LOGGER.info("Calculating events to add");
		eventsToAdd = new HashSet<EventKey>(updatedScheduleEvents);
		eventsToAdd.removeAll(existingScheduleEvents);

		LOGGER.info("Calculating events to delete");
		eventsToRemove = new HashSet<EventKey>(existingScheduleEvents);
		eventsToRemove.removeAll(updatedScheduleEvents);

	}

	private void validateChanges() {
		if (checkForMissingData(eventsToAdd)) {
			System.err.println("Errors exist, exiting");
			System.exit(-1);
		}

		if (!isOKToDelete()) {
			System.err.println("Errors exist, exiting");
			System.exit(-1);
		}
	}

	private void listChanges() {
		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			LOGGER.info("Adding new event::" + event.getTeam() + ":" + event.getLocation() + ":"
					+ event.getFullDateTime() + ":" + event.isGame() + ":" + event.getSummary());
		}

		for (EventKey eventToRemove : eventsToRemove) {
			DivisionEvent divisionEvent = (DivisionEvent) eventToRemove.getSource();
			LOGGER.info("Deleting existing event: " + divisionEvent.getName() + ":" + divisionEvent.getLocationId()
					+ ":" + divisionEvent.getCalendarStartDate().getTime());
		}
	}
	
	private boolean confirmApplyChanges()
	{
		System.out.println("Confirm that you want to apply changes (enter YES)");
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine().trim().equals("YES");
	}

	private void applyChanges() {

		LOGGER.info("Adding events");

		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			String divison = Config.getInstance().GetConfig(event.getTeam()).getDivision();
			Division division = teamSnap.getDivisionByName(divison);
			Team team = division.getTeamByName(event.getTeam());
			DivisionLocation location = teamSnap.getLocationByName(event.getLocation());
			DivisionEvent divisionEvent = new DivisionEvent(Integer.valueOf(division.getDivisionId()),
					Integer.valueOf(team.getTeamId()), Integer.valueOf(location.getLocationId()), event.getSummary(),
					event.getFullDateTime(), event.isGame());
			divisionEvent.create();
		}

		LOGGER.info("Done adding events");

		LOGGER.info("Deleting events");

		for (EventKey eventToRemove : eventsToRemove) {
			DivisionEvent divisionEvent = (DivisionEvent) eventToRemove.getSource();

			if (Config.getInstance().GetConfig(divisionEvent.getTeam().getName()).isActive()) {
				divisionEvent.delete();
			}
		}

		LOGGER.info("Done deleting events");

	}

	public void reconcile() {
		if (ArenaMapper.getInstance().hasErrors()) {
			ArenaMapper.getInstance().dumpErrors();
			System.exit(-1);
		}

		calculateDifference();

		validateChanges();

		listChanges();
		
		if (confirmApplyChanges())
		{
			System.out.println("Applying changes...");
			applyChanges();
		}

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
