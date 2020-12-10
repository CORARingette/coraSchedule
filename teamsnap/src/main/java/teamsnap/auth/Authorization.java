package teamsnap.auth;

import org.codehaus.jackson.map.ObjectMapper;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import lombok.extern.java.Log;

@Log
public class Authorization {

	private String token = null;

	private static Authorization instance = new Authorization();

	private Authorization() {

		try {
            String auth_url = System.getenv("CW_TS_AUTH_URL");
            if (auth_url == null) {
            	System.err.println("The environment variable CW_TS_AUTH_URL must be set to the TeamSnap authorization URL");
            	System.exit(1);
            }
			HttpResponse<JsonNode> authResponse = Unirest
					.post(auth_url)
					.asJson();
			log.fine(authResponse.getBody().toString());
			ObjectMapper mapper = new ObjectMapper();
			String text = authResponse.getBody().toString();
			AuthorizationResponse authorizationResponse = mapper.readValue(text, AuthorizationResponse.class);

			token = authorizationResponse.getAccess_token();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Authorization instance() {
		return instance;
	}

	public String getToken() {
		return token;
	}

}
