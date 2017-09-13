package utils;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Config {

	Hashtable<String, ConfigItem> configItems = new Hashtable<String, ConfigItem>();
	
	public static Config instance = new Config();

	private Config() {
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			Document document = builder.parse(new FileInputStream(
					"properties\\TeamConfig.xml"));

			XPath xPath = XPathFactory.newInstance().newXPath();

			NodeList nodeList = (NodeList) xPath.evaluate("//team", document,
					XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node teamNode = nodeList.item(i);
				String team = xPath.evaluate("name", teamNode);
				String url = xPath.evaluate("url", teamNode);
				String map = xPath.evaluate("map", teamNode);

				configItems.put(team, new ConfigItem(team, url, map));
			}

			System.err.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class ConfigItem {

		private String team;
		private String url;
		private String map;

		public ConfigItem(String team, String url, String map) {
			this.team = team;
			this.url = url;
			this.map = map;
		}

		public String getTeam() {
			return team;
		}

		public String getUrl() {
			return url;
		}

		public String getMap() {
			return map;
		}
	}
	
	public ConfigItem GetConfig(String team)
	{
		return configItems.get(team);
	}

	public List<String> GetTeams()
	{
		return Collections.list(configItems.keys());
	}
	
	public static void main(String[] args) {
		//Config c = new Config();
	}

}
