/**
 * 
 */
package cora.page;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

import java.net.URI;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import com.google.common.base.Throwables;

import cora.auth.CwAuthConst;
import io.dropwizard.views.View;

@Path("/login")
@Produces(MediaType.TEXT_HTML)
public class CwPageLogin {
	static class PageView extends View {

		public PageView() {
			super("login.ftl");
		}
	}

	public CwPageLogin() {
	}

	@GET
	public PageView getPage() {
		return new PageView();
	}

	@POST
	public Response validateLogin(@FormParam("username") String username, @FormParam("password") String password) {

		// Validate credentials
		try {
			if (password.equals("p")) {
				// If OK, save token and redirect to main page
		        final JwtClaims claims = new JwtClaims();
		        claims.setSubject("good-guy");
		        claims.setClaim("username", username);
		        claims.setExpirationTimeMinutesInTheFuture(300);

		        final JsonWebSignature jws = new JsonWebSignature();
		        jws.setPayload(claims.toJson());
		        jws.setAlgorithmHeaderValue(HMAC_SHA256);
		        jws.setKey(new HmacKey(CwAuthConst.tokenKey));

		        try {
		            String x = jws.getCompactSerialization();
		            String a = x;
		        }
		        catch (JoseException e) { throw Throwables.propagate(e); }
				
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
