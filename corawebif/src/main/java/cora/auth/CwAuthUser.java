package cora.auth;

import java.security.Principal;
import java.util.Set;
 
public class CwAuthUser implements Principal {
    private final String name;
 
    private final Set<String> roles;
 
    public CwAuthUser(String name) {
        this.name = name;
        this.roles = null;
    }
 
    public CwAuthUser(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles;
    }
 
    public String getName() {
        return name;
    }
 
    // Not sure if this is needed
    public int getId() {
        return name.hashCode();
    }
 
    public Set<String> getRoles() {
        return roles;
    }
}