package cora.auth;

import io.dropwizard.auth.Authorizer;

public class CwAuthAuthorizer implements Authorizer<CwAuthUser> 
{
    @Override
    public boolean authorize(CwAuthUser user, String role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}