package teamsnap.apiClient;

import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import teamsnap.auth.Authorization;

public class Query {

	protected static final Logger LOGGER = Logger.getLogger(Query.class.getName());

	private final String query;

	public Query(String query) {
		this.query = query;
	}

	public Response execute(String parmName, String parmValue) {
		Response result = null;
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(query).queryString(parmName, parmValue)
					.header("Authorization", "Bearer " + Authorization.instance().getToken()).asJson();
			LOGGER.info(jsonResponse.getBody().toString());

			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
