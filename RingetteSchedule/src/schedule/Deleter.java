package schedule;

import java.util.List;

import teamsnap.entities.Division;
import teamsnap.entities.DivisionEvent;
import teamsnap.entities.DivisionLocation;
import teamsnap.entities.League;

public class Deleter {
	
	public void deleteAllGames()
	{
		League league = new League();
		List<DivisionEvent> events = league.getAllEvents();
		for (DivisionEvent event:events)
		{
			event.delete();
		}
	}
	
	public void deleteAllDivisions()
	{
		League league = new League();
		List<Division> divisions = league.getAllDivisions();
		for (Division division: divisions)
		{
			division.delete();
		}
	}
	
	public void deleteAllLocations()
	{
		League league = new League();
		List<DivisionLocation> divisionLocations = league.getLocations();
		for (DivisionLocation divisionLocation: divisionLocations)
		{
			divisionLocation.delete();
		}
	}

	public static void main(String[] args) {
		new Deleter().deleteAllGames();
		//new Deleter().deleteAllDivisions();
		//new Deleter().deleteAllLocations();
	}

}
