package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class MakeXML {

	public static void main(String[] args) {
		try {

			Properties sortOrder = new Properties();
			sortOrder
					.loadFromXML(new FileInputStream("properties/teamSort.xml"));

			Properties urlLookup = new Properties();
			urlLookup.loadFromXML(new FileInputStream(
					"properties/leagueURL.xml"));
			// urlLookup.list(System.err);

			Properties nameMapper = new Properties();
			nameMapper.loadFromXML(new FileInputStream(
					"properties/teamMapper.xml"));

			PrintWriter config = new PrintWriter(new FileWriter(
					"TeamConfig.xml"));

			for (Object team : sortOrder.keySet()) {
				config.println("<team>");
				config.println("<name>" + team + "</name>");

				config.println("<map>"
						+ (nameMapper.containsKey(team) ? nameMapper.get(team)
								: "") + "</map>");

				config.println("<url>"
						+ (urlLookup.containsKey(team) ? urlLookup.get(team)
								: "") + "</url>");

				config.println("</team>");
			}

			config.close();

		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
