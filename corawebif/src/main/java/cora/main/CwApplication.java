package cora.main;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookieAuthBundle;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookiePrincipal;

import cora.auth.CwAuthConst;
import cora.page.CwPageMain;
import cora.page.CwPageProfile;
import cora.page.CwPageUploadConfirm;
import cora.page.CwPageUploadDone;
import cora.page.CwPageUploadNewSchedule;
import cora.page.CwPageUploadWait;
import cora.page.CwPages;
import cora.page.CwfPageUploadRejected;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class CwApplication extends Application<CwConfiguration> {

	static URI loginUri;
	private static final String SESSION_TOKEN = "sessionToken";

	static {
		try {
			loginUri = new URI("/login");
		} catch (URISyntaxException e) {
		}
	}

	@Priority(Priorities.AUTHENTICATION - 1)
	@Provider
	public static class RequestServerFilter implements ContainerRequestFilter {

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
				Claims x = Jwts.parserBuilder()
						.setSigningKey(JwtCookieAuthBundle.generateKey(CwAuthConst.tokenKeyString)).build()
						.parseClaimsJws(token).getBody();
			} catch (Exception e) {
				// Token is bad - probably expired.  Might as well purge it.
			    JwtCookiePrincipal.removeFromContext(requestContext);
				requestContext.abortWith(Response.seeOther(loginUri).build());
				return;
			}
		}

	}

	public static void main(final String[] args) throws Exception {
		new CwApplication().run(args);
	}

	@Override
	public String getName() {
		return "webif";
	}

	public static Key keySupplier(Configuration c, Environment e) {
		return JwtCookieAuthBundle.generateKey(CwAuthConst.tokenKeyString);
	}

	@Override
	public void initialize(final Bootstrap<CwConfiguration> bootstrap) {
		bootstrap.addBundle(new ViewBundle<CwConfiguration>());
		bootstrap.addBundle(new AssetsBundle("/assets", "/assets"));

		// See https://github.com/dhatim/dropwizard-jwt-cookie-authentication
		bootstrap.addBundle(JwtCookieAuthBundle.getDefault().withKeyProvider(CwApplication::keySupplier));

	}

	@Override
	public void run(final CwConfiguration configuration, final Environment environment) {
		JerseyEnvironment jersey = environment.jersey();

		// Registering pages
		jersey.register(new RequestServerFilter());
		jersey.register(new CwPageMain());
		jersey.register(new CwPageUploadNewSchedule());
		jersey.register(new CwPageUploadConfirm());
		jersey.register(new CwPageUploadDone());
		jersey.register(new CwfPageUploadRejected());
		jersey.register(new CwPageUploadWait());
		jersey.register(new CwPages());
		jersey.register(new CwPageProfile());

		// Health checks
		environment.healthChecks().register("APIHealthCheck", new CwHealthCheckTeamSnap());

	}

}
