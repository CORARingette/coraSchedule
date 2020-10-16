package model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ShareValue {
	HOME("H", true, false), VISITOR("V", true, false), HALF("0.5", false, true), FULL("1.0", false, true),
	OTHER("X", false, false), TEAM_FULL("T1", false, true), TEAM_HALF("T0.5", false, true);

	@Getter
	private final String shortString;
	@Getter
	private final boolean isGame;
	@Getter
	private final boolean toLoad;

	public static ShareValue fromShortString(String shortString) {
		if (shortString.equals(ShareValue.HOME.getShortString())) {
			return ShareValue.HOME;
		} else if (shortString.equals(ShareValue.VISITOR.getShortString())) {
			return ShareValue.VISITOR;
		} else if (shortString.equals(ShareValue.HALF.getShortString())) {
			return ShareValue.HALF;
		} else if (shortString.equals(ShareValue.FULL.getShortString())) {
			return ShareValue.FULL;
		} else if (shortString.equals(ShareValue.TEAM_FULL.getShortString())) {
			return ShareValue.TEAM_FULL;
		} else if (shortString.equals(ShareValue.TEAM_HALF.getShortString())) {
			return ShareValue.TEAM_HALF;
		} else {
			return ShareValue.OTHER;
		}
	}

	public static boolean isGame(ShareValue shareValue) {
		return shareValue != null && (shareValue == ShareValue.HOME || shareValue == ShareValue.VISITOR);
	}

	public static boolean isPractice(ShareValue shareValue) {
		return shareValue != null && (shareValue == ShareValue.HALF || shareValue == ShareValue.FULL
				|| shareValue == ShareValue.TEAM_HALF || shareValue == ShareValue.TEAM_FULL);
	}
	
	public static boolean isFullIce(ShareValue shareValue) {
		return shareValue != null && (shareValue == ShareValue.FULL||shareValue == ShareValue.TEAM_FULL);
	}

}
