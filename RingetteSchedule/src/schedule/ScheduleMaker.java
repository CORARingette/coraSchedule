package schedule;


import java.util.logging.Logger;

import processing.teamSnap.TeamSnapEventGenerator;

public class ScheduleMaker {

	private static final Logger LOGGER = Logger.getLogger(ScheduleMaker.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		for (int i = 1; i < 52; i++) {
			if (IceSpreadsheet.getInstance().getWeekComment(i) == null) {
				Context.getInstance().setProcessingEndWeek(i);
				break;
			}
		}

		ScheduleMaker.LOGGER.info("Scheduling for weeks: 1 to " + Context.getInstance().getProcessingEndWeek());


		// create the event generator and process
		TeamSnapEventGenerator generator = new TeamSnapEventGenerator();
		generator.doProcessingAll();

		// Reconcile with TeamSnap
		generator.reconcile();

		// dump out errors for fixing before running
		ArenaMapper.getInstance().dumpErrors();

		ScheduleMaker.LOGGER.info("Done.");
	}

}
