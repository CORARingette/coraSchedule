package teamsnap.apiClient;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import lombok.extern.java.Log;
import teamsnap.main.Constants;

@Log
public class Template {

	private ObjectMapper mapper = new ObjectMapper();

	private ArrayNode attributesNode = mapper.createArrayNode();

	public void addStringAttribute(String name, Object value) {
		ObjectNode attributeNode = mapper.createObjectNode();
		attributeNode.put("name", name);

		if (value == null) {
			attributeNode.putNull("value");
		} else if (value instanceof String) {
			attributeNode.put("value", (String) value);
		} else if (value instanceof Integer) {
			attributeNode.put("value", (Integer) value);
		} else if (value instanceof Boolean) {
			attributeNode.put("value", (Boolean) value);
		} else if (value instanceof Date) {
			attributeNode.put("value", Constants.DATE_FORMAT.format(value));
		}
		attributesNode.add(attributeNode);
	}

	public String makeJsonString() {
		String result = "{}";
		try {
			ObjectNode dataNode = mapper.createObjectNode();
			dataNode.putPOJO("data", attributesNode);

			ObjectNode templateNode = mapper.createObjectNode();
			templateNode.putPOJO("template", dataNode);

			result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(templateNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		Template t = new Template();
		t.addStringAttribute("Test", 1);
		t.addStringAttribute("Test2", "Hello");
		Template.log.info(t.makeJsonString());
	}
}
