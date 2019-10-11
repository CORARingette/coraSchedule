package model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameType {
	AWAY_GAME("Away Game"), TOURNAMENT("Tournament");

	@Getter private final String gameType;
}
