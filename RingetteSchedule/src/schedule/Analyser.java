package schedule;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import model.Event;
import model.ShareValue;

public class Analyser {

	IceSpreadsheet sheet = IceSpreadsheet.getInstance();

	HashMap<String, List<Event>> iceByTeam = new HashMap<String, List<Event>>();
	HashMap<String, List<Event>> iceByLocation = new HashMap<String, List<Event>>();

	private void loadModel() {
		List<Event> events = sheet.getIceEvents();
		for (Event event : events) {

			List<Event> teamList = iceByTeam.get(event.getTeam());
			if (teamList == null) {
				teamList = new ArrayList<Event>();
				iceByTeam.put(event.getTeam(), teamList);
			}
			teamList.add(event);

			List<Event> locationList = iceByLocation.get(event.getLocation());
			if (locationList == null) {
				locationList = new ArrayList<Event>();
				iceByLocation.put(event.getLocation(), locationList);
			}
			locationList.add(event);
		}

	}

	private void dumpForExcel() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter("AllIce.txt"));
			out.println("Team\tLocation\tShare Value");
			List<Event> events = sheet.getIceEvents();
			for (Event event : events) {
				out.println(event.getTeam() + "\t" + event.getLocation() + "\t"
						+ ((event.getShareValue() == ShareValue.FULL || event.getShareValue() == ShareValue.HOME) ? 1.0
								: 0.5));
			}
			out.close();
		} catch (Exception e) {

			e.printStackTrace();
		} 
	}

	private void dumpByTeam() {
		System.err.println("Ice by Team\n");
		double total = 0;
		List<String> teamIceList = new ArrayList<String>(iceByTeam.keySet());
		Collections.sort(teamIceList);
		for (String team : teamIceList) {
			List<Event> teamList = iceByTeam.get(team);
			{
				double iceTotal = 0;
				for (Event event : teamList) {
					ShareValue shareValue = event.getShareValue();
					if (shareValue == ShareValue.FULL) {
						iceTotal = iceTotal + 1;
					}
					if (shareValue == ShareValue.HALF) {
						iceTotal = iceTotal + 0.5;
					}
					if (shareValue == ShareValue.HOME) {
						iceTotal = iceTotal + 1.0;
					}
				}
				System.err.printf("%-30s: %.1f\n", team, iceTotal);
				total += iceTotal;
			}
		}
		System.err.println("Total = " + total);
	}

	private void dumpByLocation() {
		System.err.println("Ice by Location\n");
		double total = 0;
		List<String> locationIceList = new ArrayList<String>(iceByLocation.keySet());
		Collections.sort(locationIceList);
		for (String location : locationIceList) {
			List<Event> locationList = iceByLocation.get(location);
			{
				double iceTotal = 0;
				for (Event event : locationList) {
					ShareValue shareValue = event.getShareValue();
					if (shareValue == ShareValue.FULL) {
						iceTotal = iceTotal + 1;
					}
					if (shareValue == ShareValue.HALF) {
						iceTotal = iceTotal + 0.5;
					}
					if (shareValue == ShareValue.HOME) {
						iceTotal = iceTotal + 1.0;
					}
				}
				System.err.printf("%-30s: %.1f\n", location, iceTotal);
				total += iceTotal;
			}
		}
		System.err.println("Total = " + total);
	}

	public static void main(String[] args) {
		Analyser analyser = new Analyser();
		analyser.loadModel();
		analyser.dumpByTeam();
		System.err.println("");
		analyser.dumpByLocation();
		analyser.dumpForExcel() ;
	}

}
