package cora.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookieAuthBundle;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookiePrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * This is not the real authentication filter. This is a quick and dirty filter
 * to check if the JWT auth token is present and valid. If it is not then we
 * redirect to /login. Theoretically this could be done within the
 * org.dhatim.dropwizard.jwt.cookie but it was really hard and this was way
 * faster.
 * 
 * @author andrewmcgregor
 *
 */
@Priority(Priorities.AUTHENTICATION - 1)
@Provider
public class CwAuthMissingAuthRedirectFilter implements ContainerRequestFilter {
	static URI loginUri;
	private static final String SESSION_TOKEN = "sessionToken";

	static {
		try {
			loginUri = new URI("/login");
		} catch (URISyntaxException e) {
		}
	}


	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Map<String, Cookie> cookies = requestContext.getCookies();

		if (requestContext.getUriInfo().getPath().equals("login"))
			return;

		if (!cookies.containsKey(SESSION_TOKEN)) {
			requestContext.abortWith(Response.seeOther(loginUri).build());
			return;
		}

		String token = cookies.get(SESSION_TOKEN).getValue();
		try {
			@SuppressWarnings("unused")
			Claims x = Jwts.parserBuilder().setSigningKey(JwtCookieAuthBundle.generateKey(CwAuthConst.tokenKeyString))
					.build().parseClaimsJws(token).getBody();
			// If we get here, the token is OK so we are OK to proceed.
		} catch (Exception e) {
			// Token is bad - probably expired. Might as well purge it.
			JwtCookiePrincipal.removeFromContext(requestContext);
			requestContext.abortWith(Response.seeOther(loginUri).build());
			return;
		}
	}

}
