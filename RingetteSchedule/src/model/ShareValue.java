package model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ShareValue {
	HOME("H", true), VISITOR("V", true), HALF("0.5", false), FULL("1.0", false), OTHER("X", false);

	@Getter private final String shortString;
	@Getter private final boolean isGame;

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
