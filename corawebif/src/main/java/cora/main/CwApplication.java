package cora.main;

import java.io.File;
import java.security.Key;

import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookieAuthBundle;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import cora.auth.CwAuthConst;
import cora.auth.CwAuthHolder;
import cora.auth.CwAuthMissingAuthRedirectFilter;
import cora.page.CwPages;
import cora.page.CwPagesAuth;
import cora.page.CwPagesEmail;
import cora.page.CwPagesSwerk;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class CwApplication extends Application<CwConfiguration> {

	public String[] getScheduleToolArgs() {
		String javaPath = System.getenv("CW_JAVA_PATH");
		if (javaPath == null)
			javaPath = "/usr/local/Homebrew/opt/openjdk/bin/java";
		String classPath = System.getenv("CW_CLASS_PATH");
		if (classPath == null)
			classPath = "/Users/andrewmcgregor/git/coraSchedule/corawebif/target/classes";
		String mainClass = System.getenv("CW_MAIN_CLASS");
		if (mainClass == null)
			mainClass = "cora.mock.CwMockAppl";

		String args[] = {
					javaPath,
					"-Djava.util.logging.config.file=properties/logging.properties",
					"-cp",
					classPath,
					mainClass};
			return args;
			
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
		bootstrap.addBundle(new AssetsBundle("/assets", "/corawebif/assets"));

		// See https://github.com/dhatim/dropwizard-jwt-cookie-authentication
		bootstrap.addBundle(JwtCookieAuthBundle.getDefault().withKeyProvider(CwApplication::keySupplier));

	}

	@Override
	public void run(final CwConfiguration configuration, final Environment environment) throws Exception {
		JerseyEnvironment jersey = environment.jersey();

		// Registering pages
		jersey.register(new CwAuthMissingAuthRedirectFilter());
		jersey.register(new CwPages());
		jersey.register(new CwPagesSwerk());
		jersey.register(new CwPagesAuth());
		jersey.register(new CwPagesEmail());

		jersey.register(MultiPartFeature.class);

		// Health checks
		environment.healthChecks().register("APIHealthCheck", new CwHealthCheckTeamSnap());

		CwRunner.makeGlobalRunner(getScheduleToolArgs());
		
		String authPath = System.getenv("CW_FILE_PATH");
		if (authPath == null) {
			System.err.println("Env var CW_FILE_PATH not set.");
			System.exit(1);
		}
		CwAuthHolder.setGlobalHolder(new CwAuthHolder(new File(authPath, "auth_info.json")));
	}

}
