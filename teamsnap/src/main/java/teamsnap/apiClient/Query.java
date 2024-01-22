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
			if (jsonResponse.getStatus() != 400) {
				throw new Exception("Invalid HTTP response code from TeamSnap: " + jsonResponse.getStatus());
			}

			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
		} catch (Exception e) {

			String s;
			if (jsonResponse != null) {
				s = jsonResponse.getBody().toString();
			}
			else
				s = "JSON Response NULL";
			log.severe(s);
			e.printStackTrace();
		}
		return result;
	}

}
