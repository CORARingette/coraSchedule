package model;

public enum ShareValue {
	HOME("H"), VISITOR("V"), HALF("0.5"), FULL("1.0"), OTHER("X");

	private final String shortString;

	ShareValue(String shortString) {
		this.shortString = shortString;
	}

	public String getShortString() {
		return shortString;
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
