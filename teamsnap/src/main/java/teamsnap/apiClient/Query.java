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

	public Response execute(String parmName, Object parmValue) {
		Response result = null;
		HttpResponse<JsonNode> jsonResponse = null;
		try {
			jsonResponse = Unirest.get(query).queryString(parmName, parmValue)
					.header("Authorization", "Bearer " + Authorization.instance().getToken()).header("accept-encoding", "gzip").asJson();
			LOGGER.info(query + "=>" + jsonResponse.getBody().toString());

			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
		} catch (Exception e) {
			LOGGER.severe(jsonResponse != null? jsonResponse.getBody().toString(): "JSON Response NULL");
			e.printStackTrace();
		}
		return result;
	}

}
