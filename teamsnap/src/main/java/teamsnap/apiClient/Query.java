package teamsnap.apiClient;

import org.codehaus.jackson.map.ObjectMapper;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import lombok.extern.java.Log;
import teamsnap.auth.Authorization;

@Log
public class Query {

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
			log.fine(query + "=>" + jsonResponse.getBody().toString());

			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
		} catch (Exception e) {
			log.severe(jsonResponse != null? jsonResponse.getBody().toString(): "JSON Response NULL");
			e.printStackTrace();
		}
		return result;
	}

}
