package teamsnap.entities;

public enum EventType {
	GAME("Game"), PRACTICE("Practice"), OTHER ("Other");

	private final String description;

	private EventType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
