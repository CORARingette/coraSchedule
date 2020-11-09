package cora.main;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;

import cora.auth.CwAuthAuthenticator;
import cora.auth.CwAuthAuthorizer;
import cora.auth.CwAuthConst;
import cora.auth.CwAuthUnauthHandler;
import cora.auth.CwAuthUser;
import cora.auth.CwAuthForbiddenExceptionMapper;
import cora.page.CwPageLogin;
import cora.page.CwPageLogout;
import cora.page.CwPageMain;
import cora.page.CwPageProfile;
import cora.page.CwPageUploadConfirm;
import cora.page.CwPageUploadDone;
import cora.page.CwPageUploadNewSchedule;
import cora.page.CwfPageUploadRejected;
import cora.page.CwPageUploadWait;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
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
       
        
    }

    @Override
    public void run(final CwConfiguration configuration,
                    final Environment environment) {
        JerseyEnvironment jersey = environment.jersey();
    	
    	// Registering pages
		jersey.register( new CwPageMain());
        jersey.register( new CwPageUploadNewSchedule());
        jersey.register( new CwPageUploadConfirm());
        jersey.register( new CwPageUploadDone());
        jersey.register( new CwfPageUploadRejected());
        jersey.register( new CwPageUploadWait());
        jersey.register( new CwPageLogin());
        jersey.register( new CwPageLogout());
        jersey.register( new CwPageProfile());
        
        // Health checks
        environment.healthChecks().register("APIHealthCheck", new CwHealthCheckTeamSnap());

        //****** Dropwizard security - custom classes ***********/

        final JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setRequireSubject() // the JWT must have a subject claim
                .setVerificationKey(new HmacKey(CwAuthConst.tokenKey)) // verify the signature with the public key
                .setRelaxVerificationKeyValidation() // relaxes key length requirement
                .build(); // create the JwtConsumer instance

        jersey.register(new AuthDynamicFeature(
                new JwtAuthFilter.Builder<CwAuthUser>()
                    .setJwtConsumer(jwtConsumer)
                    .setRealm("realm")
                    .setPrefix("Bearer")
                    .setAuthenticator(new CwAuthAuthenticator())
                    .setUnauthorizedHandler(new CwAuthUnauthHandler())
                    .buildAuthFilter()));

        jersey.register(RolesAllowedDynamicFeature.class);
        jersey.register(new AuthValueFactoryProvider.Binder<>(CwAuthUser.class));
        // Register custom exception mapper to redirect 403 errors to the login page
        environment.jersey().register(CwAuthForbiddenExceptionMapper.class);

    }

}
