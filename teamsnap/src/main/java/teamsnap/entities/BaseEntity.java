package teamsnap.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import teamsnap.apiClient.Attribute;
import teamsnap.apiClient.Item;
import teamsnap.apiClient.Response;
import teamsnap.apiClient.Template;
import teamsnap.auth.Authorization;

public abstract class BaseEntity {

	protected static final Logger LOGGER = Logger.getLogger(BaseEntity.class.getName());
	protected String apiString;
	protected HashMap<String, Object> attributes = new HashMap<String, Object>();

	public BaseEntity(String apiString) {
		this.apiString = apiString;
	}

	public void addAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	protected void makeFromResponse(Item item) {
		for (Attribute attribute : item.getData()) {
			attributes.put(attribute.getName(), attribute.getValue());
		}

	}
	

	public void create() {
		try {
			Template template = new Template();
			for (Map.Entry<String, Object> entry : attributes.entrySet()) {
				template.addStringAttribute(entry.getKey(), entry.getValue());
			}
			HttpResponse<JsonNode> eventListResponse = Unirest.post(apiString)
					.header("Authorization", "Bearer " + Authorization.instance().getToken())
					.header("accept-encoding", "gzip")
					.body(template.makeJsonString()).asJson();
			LOGGER.info("Create Return String: " + template.makeJsonString() + ":\n"
					+ eventListResponse.getBody().toString());

			ObjectMapper mapper = new ObjectMapper();
			Response eventResponse = mapper.readValue(eventListResponse.getBody().toString(), Response.class);
			Item thisItem = eventResponse.getItems().get(0);
			makeFromResponse(thisItem);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		try {
			Unirest.delete(apiString + "/" + getId())
					.header("Authorization", "Bearer " + Authorization.instance().getToken()).asJson();
			LOGGER.info("Successfully deleted");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected int getId() {
		return (Integer) attributes.get("id");
	}
	
	public void dump()
	{
		StringBuilder sb = new StringBuilder();
		SortedSet<String> keys = new TreeSet<>(attributes.keySet());
		for (String key : keys) { 
			sb.append(key).append("=").append(attributes.get(key)).append("\n");
		}
		LOGGER.info(sb.toString());
	}

}
