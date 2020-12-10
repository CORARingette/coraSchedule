package cora.auth;

import org.mindrot.jbcrypt.BCrypt;

public class CwAuthPassword {
	private final String hashedPassword;
	private static final int BCRYPT_COST = 7;

	private CwAuthPassword(final String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public boolean checkPassword(final String passwordToCheck) {
		return BCrypt.checkpw(passwordToCheck, hashedPassword);
	}

	public static CwAuthPassword fromHashedPassword(final String hashedPassword) {
		return new CwAuthPassword(hashedPassword);
	}

	public static CwAuthPassword generateFromPassword(final String password) {
		final String salt = BCrypt.gensalt(BCRYPT_COST);
		return CwAuthPassword.fromHashedPassword(BCrypt.hashpw(password, salt));
	}

	
	public static void main(String args[]) {
		if (args.length == 0) {
			System.err.println("Must supply password as an argument");
			System.exit(1);
		}
		String password = args[0];
		CwAuthPassword hashedPass = CwAuthPassword.generateFromPassword(password);
		System.out.println("Hashed password for '" + password + "' is '" + hashedPass.getHashedPassword() + "'");
	}
}
