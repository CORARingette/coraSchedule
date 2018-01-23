package teamsnap.entities;

import teamsnap.apiClient.Item;

public class DivisionLocation extends BaseEntity {

	public DivisionLocation(Item item) {
		this();
		this.makeFromResponse(item);
	}

	public DivisionLocation(String division, String name, String address, String mapURL) {
		this();
		addAttribute("division_id", division);
		addAttribute("name", name);
		addAttribute("address", address);
		addAttribute("url", mapURL);
	}

	private DivisionLocation() {
		super("https://api.teamsnap.com/v3/division_locations");
	}

	public String getName() {
		return (String) attributes.get("name");
	}
	
	public Integer getLocationId() {
		return (Integer) attributes.get("id");
	}
}
