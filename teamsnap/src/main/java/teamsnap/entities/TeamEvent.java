package teamsnap.entities;

import teamsnap.apiClient.Item;

public class TeamEvent extends BaseEntity {

	public TeamEvent(Item item) {
		this();
		makeFromResponse(item);
	}

	public TeamEvent() {
		super("https://api.teamsnap.com/v3/events");
	}
	
	

}

