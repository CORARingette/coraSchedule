package teamsnap.auth;

import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import teamsnap.entities.League;

public class Authorization {

	protected static final Logger LOGGER = Logger.getLogger(League.class.getName());
	private String token = null;

	private static Authorization instance = new Authorization();

	private Authorization() {

		try {
			HttpResponse<JsonNode> authResponse = Unirest
					.post("https://auth.teamsnap.com/oauth/token?password=C1a2m3p4&grant_type=password&scope=read+write&client_secret=520f0361a747cf1fa78c915cc8c3736015cffd638c6826eae010f14951ab781b&client_id=51892d7a5d4737e1f0e4941f5e2a38e394e71c6b8c53483109e7f9736b21b52f&username=rkw850%40yahoo.ca")
					.asJson();
			LOGGER.info(authResponse.getBody().toString());
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
