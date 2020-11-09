package cora.auth;

import java.util.Optional;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class CwAuthAuthenticator implements Authenticator<JwtContext, CwAuthUser> {

	@Override
	public Optional<CwAuthUser> authenticate(JwtContext context) throws AuthenticationException {
		JwtClaims claims = context.getJwtClaims();
		
		try {
			return Optional
						.of(new CwAuthUser(claims.getStringClaimValue("username")));
		} catch (MalformedClaimException e) {
			return Optional.empty();
		}
	}
}