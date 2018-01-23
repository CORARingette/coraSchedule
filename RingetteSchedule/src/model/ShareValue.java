package model;

public enum ShareValue {
	HOME("H", true), VISITOR("V", true), HALF("0.5", false), FULL("1.0", false), OTHER("X", false);

	private final String shortString;
	private final boolean isGame;

	ShareValue(String shortString, boolean isGame) {
		this.shortString = shortString;
		this.isGame = isGame;
	}

	public String getShortString() {
		return shortString;
	}

	public boolean isGame() {
		return isGame;
	}

	public static ShareValue fromShortString(String shortString) {
		if (shortString.equals(ShareValue.HOME.getShortString())) {
			return ShareValue.HOME;
		} else if (shortString.equals(ShareValue.VISITOR.getShortString())) {
			return ShareValue.VISITOR;
		} else if (shortString.equals(ShareValue.HALF.getShortString())) {
			return ShareValue.HALF;
		} else if (shortString.equals(ShareValue.FULL.getShortString())) {
			return ShareValue.FULL;
		} else {
			return ShareValue.OTHER;
		}
	}

}
