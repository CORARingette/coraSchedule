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

@Path("/")
public class CwPages {

	@Path("/logout")
	@Produces(MediaType.TEXT_HTML)
	@GET
	public Response logout(@Context ContainerRequestContext requestContext) {
		JwtCookiePrincipal.removeFromContext(requestContext);
		URI uri = UriBuilder.fromUri("/login").queryParam(CwPageView.MESSAGE_PARAM, "User logged out").build();
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

		// Validate credentials
		try {
			if (password.equals("p")) {
				// If OK, save token and redirect to main page
				DefaultJwtCookiePrincipal principal = new DefaultJwtCookiePrincipal(username);
				principal.addInContext(requestContext);

				URI uri = UriBuilder.fromUri("/").build();
				return Response.seeOther(uri).build();
			} else {
				// If wrong, delay and make login page with message
				Thread.sleep(1000);
				URI uri = UriBuilder.fromUri("/login").queryParam(CwPageView.MESSAGE_PARAM, "Invalid credentials").build();
				return Response.seeOther(uri).build();
			}
		} catch (InterruptedException e) {
			URI uri = UriBuilder.fromUri("/login").build();
			return Response.seeOther(uri).build();
		}

	}

	@GET
	@Path("/")
	@PermitAll
	public CwPageView getMain(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		return new CwPageView("main.ftl").setMessage(message);
	}


	@GET
	@Path("/profile")
	@PermitAll
	public CwPageView getProfile(@QueryParam(CwPageView.MESSAGE_PARAM) String message) {
		return new CwPageView("profile.ftl").setMessage(message);
	}


}
