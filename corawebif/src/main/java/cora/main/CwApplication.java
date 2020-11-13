package cora.main;

import java.security.Key;

import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookieAuthBundle;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import cora.auth.CwAuthConst;
import cora.auth.CwAuthMissingAuthRedirectFilter;
import cora.page.CwPages;
import cora.page.CwPagesAuth;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class CwApplication extends Application<CwConfiguration> {


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
		jersey.register(new CwAuthMissingAuthRedirectFilter());
		jersey.register(new CwPages());
		jersey.register(new CwPagesAuth());

		jersey.register(MultiPartFeature.class);

		// Health checks
		environment.healthChecks().register("APIHealthCheck", new CwHealthCheckTeamSnap());

		String args[] = {
				"/usr/local/Homebrew/opt/openjdk/bin/java",
				"-cp",
				"/Users/andrewmcgregor/git/coraSchedule/corawebif/target/classes",
				"cora.mock.CwMockAppl"};
		CwRunner.makeGlobalRunner(args);
	}

}
