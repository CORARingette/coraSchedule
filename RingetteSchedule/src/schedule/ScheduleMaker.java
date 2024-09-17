package schedule;


import java.util.Map;

import ice.IceDataGlobal;
import lombok.extern.java.Log;
import processing.teamSnap.TeamSnapEventGenerator;

@Log
public class ScheduleMaker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        Map<String, String> env = System.getenv();
		String mode = env.getOrDefault("CW_MODE", "unset");
		if (mode.equals("CLASSIC")) {
			IceDataGlobal.makeIceData(IceDataGlobal.IceDataType.CLASSIC);
		} else if (mode.equals("SWERK")) {
			IceDataGlobal.makeIceData(IceDataGlobal.IceDataType.SWERK);
		} else {
			throw new Error("Invalid value for property CW_MODE: '" + mode + "'");
		}
		
		for (int i = 1; i < 52; i++) {
			if (IceDataGlobal.getInstance().getWeekComment(i) == null) {
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
