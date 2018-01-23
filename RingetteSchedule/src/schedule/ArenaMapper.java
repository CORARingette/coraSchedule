package schedule;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import processing.AbstractTeamEventProcessor;

public class ArenaMapper {
	private static ArenaMapper instance = new ArenaMapper();
	private static final Logger LOGGER = Logger.getLogger(ArenaMapper.class.getName());

	private Properties arenaMapper = new Properties();
	List<String> arenaMapperErrors = new ArrayList<String>();

	private ArenaMapper() {
		try {
			arenaMapper.loadFromXML(new FileInputStream("properties/ArenaNameMapper.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArenaMapper getInstance() {
		return instance;
	}

	public String getProperty(String location) {
		return arenaMapper.getProperty(location);
	}

	public void addError(String error) {
		if (error == null)
		{
			LOGGER.severe("Error string is null");
		}
		if (!arenaMapperErrors.contains(error)) {
			arenaMapperErrors.add(error);
		}
	}

	public void dumpErrors() {
		for (String location : arenaMapperErrors) {
			String arenaEntry = "<entry key=\"" + location + "\"></entry>";
			System.err.println(arenaEntry);
		}
	}

	public boolean hasErrors() {
		return arenaMapperErrors.size() > 0;
	}
}
