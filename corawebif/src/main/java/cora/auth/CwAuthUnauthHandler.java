package cora.auth;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import io.dropwizard.auth.UnauthorizedHandler;

public class CwAuthUnauthHandler implements UnauthorizedHandler {

	@Override
	public Response buildResponse(String prefix, String realm) {
		try {
			return Response.temporaryRedirect(new URI("/login")).build();
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
