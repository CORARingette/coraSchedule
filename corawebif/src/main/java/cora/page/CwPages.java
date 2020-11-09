package cora.page;

import java.net.URI;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.dhatim.dropwizard.jwt.cookie.authentication.DefaultJwtCookiePrincipal;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookiePrincipal;

import io.dropwizard.views.View;

@Path("/")
public class CwPages {
	
	static class PageView extends View {

	    public PageView(String templateName) {
	        super(templateName);
	    }
	}
	
	
	@Path("/logout")
	@Produces(MediaType.TEXT_HTML)
    @GET
    public PageView logout(@Context ContainerRequestContext requestContext) {
	    JwtCookiePrincipal.removeFromContext(requestContext);
        return new PageView("login.ftl");
    }

	@GET
	@Path("/login")
	public PageView getLogin() {
		return new PageView("login.ftl");
	}

	@POST
	@Path("/login")
	public Response validateLogin(@Context ContainerRequestContext requestContext,
			@FormParam("username") String username, @FormParam("password") String password) {

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
				URI uri = UriBuilder.fromUri("/login").build();
				return Response.seeOther(uri).build();
			}
		} catch (InterruptedException e) {
			URI uri = UriBuilder.fromUri("/login").build();
			return Response.seeOther(uri).build();
		}

	}

}
