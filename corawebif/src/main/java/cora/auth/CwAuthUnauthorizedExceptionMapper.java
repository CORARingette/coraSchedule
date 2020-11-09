package cora.auth;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;

import org.eclipse.jetty.security.authentication.FormAuthenticator;

public class CwAuthUnauthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
		
		private UriInfo ui;
		private HttpServletRequest req;
		
		public CwAuthUnauthorizedExceptionMapper(@Context UriInfo ui, @Context HttpServletRequest req) {
			this.ui = ui;
			this.req = req;
		}

		@Override public Response toResponse(NotAuthorizedException e) {

			String location = ui.getPath();
			
			if (location != null) {
				req.getSession().setAttribute(FormAuthenticator.__J_URI, location);
			}
			return Response.temporaryRedirect(URI.create("/login")).build();
		}
		
	}
