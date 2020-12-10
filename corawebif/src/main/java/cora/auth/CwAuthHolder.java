package cora.auth;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class to hold the usernames and passwords.  These are typically loaded from a file.
 * 
 * @author andrewmcgregor
 *
 */
public class CwAuthHolder {
	static CwAuthHolder globalHolder_ms;
	private Map<String, String> userNamesToPasswords_m;
	
	public static CwAuthHolder getGlobalHolder() {
		return globalHolder_ms;
	}

	public static void setGlobalHolder(CwAuthHolder globalHolder) {
		CwAuthHolder.globalHolder_ms = globalHolder;
	}
	
	@SuppressWarnings("unchecked")
	public CwAuthHolder(File path) throws Exception {
	    // create object mapper instance
	    ObjectMapper mapper = new ObjectMapper();

	    // convert JSON file to map
	    userNamesToPasswords_m = mapper.readValue(path, Map.class);

		
	}
	
	public String getHashedPassword(String username) {
		return userNamesToPasswords_m.get(username);
	}

	
}
