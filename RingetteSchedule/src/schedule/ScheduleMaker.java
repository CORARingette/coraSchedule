package schedule;


import ice.ArenaMapper;
import ice.IceSpreadsheet;
import lombok.extern.java.Log;
import processing.teamSnap.TeamSnapEventGenerator;

@Log
public class ScheduleMaker {

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

		ScheduleMaker.log.info("Scheduling for weeks: 1 to " + Context.getInstance().getProcessingEndWeek());


		// create the event generator and process
		TeamSnapEventGenerator generator = new TeamSnapEventGenerator();
		generator.doProcessingAll();

		// Reconcile with TeamSnap
		generator.reconcile();

		// dump out errors for fixing before running
		//ArenaMapper.getInstance().dumpErrors();

		ScheduleMaker.log.info("Done.");
	}

}
