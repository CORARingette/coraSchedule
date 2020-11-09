package cora.main;

import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookieAuthBundle;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookieAuthRequestFilter;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookiePrincipalAuthenticator;

import cora.auth.CwAuthForbiddenExceptionMapper;
import cora.auth.CwAuthUnauthorizedExceptionMapper;
import cora.page.CwPageMain;
import cora.page.CwPageProfile;
import cora.page.CwPageUploadConfirm;
import cora.page.CwPageUploadDone;
import cora.page.CwPageUploadNewSchedule;
import cora.page.CwPageUploadWait;
import cora.page.CwPages;
import cora.page.CwfPageUploadRejected;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.Authorizer;
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

    @Override
    public void initialize(final Bootstrap<CwConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<CwConfiguration>());
        bootstrap.addBundle(new AssetsBundle("/assets", "/assets"));
        
        // See https://github.com/dhatim/dropwizard-jwt-cookie-authentication
        bootstrap.addBundle(JwtCookieAuthBundle.getDefault());
        
    }

    @Override
    public void run(final CwConfiguration configuration,
                    final Environment environment) {
        JerseyEnvironment jersey = environment.jersey();
    	
        // Register custom exception mapper to redirect 403 errors to the login page
        environment.jersey().register(CwAuthForbiddenExceptionMapper.class);
        environment.jersey().register(CwAuthUnauthorizedExceptionMapper.class);

        // Registering pages
		jersey.register( new CwPageMain());
        jersey.register( new CwPageUploadNewSchedule());
        jersey.register( new CwPageUploadConfirm());
        jersey.register( new CwPageUploadDone());
        jersey.register( new CwfPageUploadRejected());
        jersey.register( new CwPageUploadWait());
        jersey.register( new CwPages());
        jersey.register( new CwPageProfile());
        
        
        // Health checks
        environment.healthChecks().register("APIHealthCheck", new CwHealthCheckTeamSnap());
        

    }

}
