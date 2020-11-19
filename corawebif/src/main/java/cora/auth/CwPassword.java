package cora.auth;

import org.mindrot.jbcrypt.BCrypt;

public class CwPassword {
	private final String hashedPassword;
	private static final int BCRYPT_COST = 7;

	private CwPassword(final String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public boolean checkPassword(final String passwordToCheck) {
		return BCrypt.checkpw(passwordToCheck, hashedPassword);
	}

	public static CwPassword fromHashedPassword(final String hashedPassword) {
		return new CwPassword(hashedPassword);
	}

	public static CwPassword generateFromPassword(final String password) {
		final String salt = BCrypt.gensalt(BCRYPT_COST);
		return CwPassword.fromHashedPassword(BCrypt.hashpw(password, salt));
	}

	
	public static void main(String args[]) {
		if (args.length == 0) {
			System.err.println("Must supply password as an argument");
			System.exit(1);
		}
		String password = args[0];
		CwPassword hashedPass = CwPassword.generateFromPassword(password);
		System.out.println("Hashed password for '" + password + "' is '" + hashedPass.getHashedPassword() + "'");
	}
}
