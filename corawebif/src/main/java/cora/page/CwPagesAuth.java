package cora.page;

import java.net.URI;

import javax.annotation.security.PermitAll;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.dhatim.dropwizard.jwt.cookie.authentication.DefaultJwtCookiePrincipal;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookiePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cora.auth.CwAuthHolder;
import cora.auth.CwAuthPassword;

@Path("/corawebif")
public class CwPagesAuth {

	private static Logger logger_ms = LoggerFactory.getLogger(CwPagesAuth.class.getName());
	
	@Path("/logout")
	@Produces(MediaType.TEXT_HTML)
	@GET
	public Response logout(@Context ContainerRequestContext requestContext) {
		logger_ms.info("User logged out");

		JwtCookiePrincipal.removeFromContext(requestContext);
		URI uri = UriBuilder.fromUri("/corawebif/login").queryParam(CwPageView.MESSAGE_PARAM, "User logged out").build();
		return Response.seeOther(uri).build();
	}

	@GET
	@Path("/login")
	public CwPageView getLogin(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		return new CwPageView("login.ftl").setMessage(message);
	}

	@POST
	@Path("/login")
	public Response validateLogin(@Context ContainerRequestContext requestContext, //
			@FormParam("username") String username, //
			@FormParam("password") String password) {

		CwAuthHolder authHolder = CwAuthHolder.getGlobalHolder();
		
		// Validate credentials
		try {
			String hashedPassword = authHolder.getHashedPassword(username);
			if (hashedPassword == null) {
				// If wrong, delay and make login page with message - delay is to reduce brute force attacks
				Thread.sleep(1000);
				logger_ms.warn("Invalid username: {}", username);
				URI uri = UriBuilder.fromUri("/corawebif/login").queryParam(CwPageView.MESSAGE_PARAM, "Invalid credentials").build();
				return Response.seeOther(uri).build();				
			}
			
			CwAuthPassword passwordHolder = CwAuthPassword.fromHashedPassword(hashedPassword);
			
			if (passwordHolder.checkPassword(password)) {
				// If OK, save token and redirect to main page
				DefaultJwtCookiePrincipal principal = new DefaultJwtCookiePrincipal(username);
				principal.addInContext(requestContext);
				logger_ms.info("User logged in as {}", username);
				URI uri = UriBuilder.fromUri("/corawebif").build();
				return Response.seeOther(uri).build();
			} else {
				// If wrong, delay and make login page with message
				Thread.sleep(1000);
				logger_ms.warn("Invalid password for user {}", username);
				URI uri = UriBuilder.fromUri("/corawebif/login").queryParam(CwPageView.MESSAGE_PARAM, "Invalid credentials").build();
				return Response.seeOther(uri).build();
			}
		} catch (InterruptedException e) {
			URI uri = UriBuilder.fromUri("/corawebif/login").build();
			return Response.seeOther(uri).build();
		}

	}

	@GET
	@Path("/profile")
	@PermitAll
	public CwPageView getProfile(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		return new CwPageView("profile.ftl").setMessage(message);
	}


}
