package processing.teamSnap;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import ice.ArenaMapper;
import ice.IceSpreadsheet;

import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import lombok.Cleanup;
import lombok.extern.java.Log;
import model.Event;
import model.ShareValue;
import processing.AbstractTeamEventProcessor;
import teamsnap.entities.Division;
import teamsnap.entities.DivisionEvent;
import teamsnap.entities.DivisionLocation;
import teamsnap.entities.League;
import teamsnap.entities.Team;
import utils.Config;

@Log
public class TeamSnapEventGenerator extends AbstractTeamEventProcessor {

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

	// Events to remove from TS
	HashMap<Event, DivisionEvent> eventsToUpdate = new HashMap<Event, DivisionEvent>();

	public TeamSnapEventGenerator() {
		Config configInst = Config.getInstance();
		teamSnap = new League();
		allEvents = teamSnap.getAllEvents();
		for (DivisionEvent divisionEvent : allEvents) {
			Team team = divisionEvent.getTeam();
			if ((team != null) 
					&& configInst.GetConfig(team.getName()) != null
					&& configInst.GetConfig(team.getName()).isActive()) {
				existingScheduleEvents.add(new EventKey(divisionEvent));
			}
		}
	}

	private void calculateDifference() {
		log.info("Calculating schedule difference");

		log.info("Calculating events to add");
		eventsToAdd = new HashSet<EventKey>(updatedScheduleEvents);
		eventsToAdd.removeAll(existingScheduleEvents);

		log.info("Calculating events to delete");
		eventsToRemove = new HashSet<EventKey>(existingScheduleEvents);
		eventsToRemove.removeAll(updatedScheduleEvents);

		log.info("Calculating events to update");
		for (EventKey eventKey : updatedScheduleEvents) {
			Optional<EventKey> optional = existingScheduleEvents.stream().filter(e -> e.equals(eventKey)).findFirst();
			if (optional != null && optional.isPresent()) {
				DivisionEvent divisonEvent = (DivisionEvent) optional.get().getSource();
				Event event = (Event) eventKey.getSource();
				if (!divisonEvent.getName().equals(event.getSummary())) {
					eventsToUpdate.put(event, divisonEvent);
				}
			}
		}

	}

	private void validateChanges() {
		if (checkForMissingData(eventsToAdd)) {
			log.severe("Errors exist, exiting");
			System.exit(-1);
		}

		if (!isOKToDelete()) {
			log.severe("Errors exist, exiting");
			System.exit(-1);
		}
	}

	private void listChanges() {
		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			log.info("Adding new event::" + event.getTeam() + ":" + event.getLocation() + ":" + event.getFullDateTime()
					+ ":" + ShareValue.isGame(event.getShareValue()) + ":" + event.getSummary());
		}

		for (EventKey eventToRemove : eventsToRemove) {
			DivisionEvent divisionEvent = (DivisionEvent) eventToRemove.getSource();
			log.info("Deleting existing event: " + divisionEvent.getName() + ":" + divisionEvent.getLocation() + ":"
					+ divisionEvent.getCalendarStartDate().getTime());
		}

		for (Entry<Event, DivisionEvent> mapping : eventsToUpdate.entrySet()) {
			log.info("Summary Updated From:" + mapping.getValue().getName() + " To:" + mapping.getKey().getSummary());
		}
	}

	private boolean confirmApplyChanges() {
		System.out.println("Confirm that you want to apply changes (enter YES)");
		@Cleanup
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine().trim().equals("YES");
	}

	private void applyChanges() {

		log.info("Adding events");

		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			String divisionStr = Config.getInstance().GetConfig(event.getTeam()).getDivision();
			Division division = teamSnap.getDivisionByName(divisionStr);
			Team team = division.getTeamByName(event.getTeam());
			DivisionLocation location = teamSnap.getLocationByName(event.getLocation());
			DivisionEvent divisionEvent = new DivisionEvent(Integer.valueOf(division.getDivisionId()),
					Integer.valueOf(team.getTeamId()), Integer.valueOf(location.getLocationId()), event.getSummary(),
					event.getFullDateTime(), ShareValue.isGame(event.getShareValue()));
			divisionEvent.create();
		}

		log.info("Done adding events");

		log.info("Deleting events");

		for (EventKey eventToRemove : eventsToRemove) {
			DivisionEvent divisionEvent = (DivisionEvent) eventToRemove.getSource();

			if (Config.getInstance().GetConfig(divisionEvent.getTeam().getName()).isActive()) {
				divisionEvent.delete();
			}
		}

		log.info("Done deleting events");

		log.info("Updating summaries");
		for (Entry<Event, DivisionEvent> mapping : eventsToUpdate.entrySet()) {
			mapping.getValue().setSummary(mapping.getKey().getSummary());
		}

		log.info("Done updating summaries");

	}

	public void reconcile() {
		if (ArenaMapper.getInstance().hasErrors()) {
			ArenaMapper.getInstance().dumpErrors();
			System.exit(-1);
		}

		calculateDifference();

		validateChanges();

		listChanges();

		if (confirmApplyChanges()) {
			log.info("Applying changes...");
			applyChanges();
		}

	}

	private boolean checkForMissingData(Set<EventKey> eventsToAdd) {
		boolean dataMissing = false;
		for (EventKey eventToAdd : eventsToAdd) {
			Event event = (Event) eventToAdd.getSource();
			if (teamSnap.getLocationByName(event.getLocation()) == null) {
				log.severe("Location does not exist in TeamSnap: " + event.getLocation());
				dataMissing = true;
			}
			String divisonName = Config.getInstance().GetConfig(event.getTeam()).getDivision();
			Division division = teamSnap.getDivisionByName(divisonName);
			if (division == null) {
				log.severe("Division does not exist in TeamSnap: " + divisonName);
				dataMissing = true;
			} else {
				Team team = division.getTeamByName(event.getTeam());
				if (team == null) {
					log.severe("Team does not exist in TeamSnap: " + event.getTeam());
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
				log.severe("Team '" + de.getTeam().getName() + "' in TeamSnap was not found either in the TeamConfig XML file or in the spreadshhet");
				ok = false;
				break;
			}
		}
		return ok;
	}

	@Override
	protected void preProcess(String team) {
		log.info("Processing: " + team);
	}

	@Override
	protected void postProcess(String team) {
	}

	@Override
	protected void process(Event iceEvent) {
		EventKey eventKey = new EventKey(iceEvent);
		log.fine(">>> IE: " + iceEvent.getTeam() + ":" + iceEvent.getFullDateTime() + ":" + iceEvent.getLocation());
		updatedScheduleEvents.add(eventKey);
	}

}
