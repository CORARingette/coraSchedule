package model;

public enum GameType {
	AWAY_GAME("Away Game"), TOURNAMENT("Tournament");

	private final String gameType;

	GameType(String gameType) {
		this.gameType = gameType;
	}

	public String getGameType() {
		return gameType;
	}

}
