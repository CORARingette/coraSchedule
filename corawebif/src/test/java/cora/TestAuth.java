package cora;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cora.auth.CwAuthHolder;
import cora.auth.CwPassword;

public class TestAuth {

	/**
	 * Tests password hashing
	 */
	@Test
	void testPassword() {
		String password = "MyNameIsLegion";
		try {
			String digest1 = "98273498273498273498";
			CwPassword badCwPassword = CwPassword.fromHashedPassword(digest1);
			badCwPassword.checkPassword(password);
			fail("Exception expected");
		} catch (Exception e) {

		}

		// Check that we can hash a password and then check the hash against the
		// original password
		CwPassword hashedPassword = CwPassword.generateFromPassword(password);
		assert (hashedPassword.checkPassword(password));
		assert (!hashedPassword.checkPassword(password + "x"));

		// Check that the comparison fails against a different has
		hashedPassword = CwPassword.generateFromPassword(password + "xx");
		assert (!hashedPassword.checkPassword(password));
	}

	@Test
	void testAuthHolder() throws Exception {
		String hashedP1 = "$2a$07$3NezBmNmtBObWxDCIZlzReC6ngrmSoTPvNtUWCNxu0xGWuC2BSj1i";
		
		Assertions.assertThrows(IOException.class, () -> {
			new CwAuthHolder(new File("/badbad"));
		});

		System.err.println("Current dir is " + System.getProperty("user.dir"));
		CwAuthHolder authHolder = new CwAuthHolder(new File("testAuthInfo.json"));
		assert (authHolder.getHashedPassword("u1").equals(hashedP1));
		assert (authHolder.getHashedPassword("u2") == null);
		
		CwPassword p1 = CwPassword.fromHashedPassword(hashedP1);
		assert(p1.checkPassword("p1"));
	}
}
