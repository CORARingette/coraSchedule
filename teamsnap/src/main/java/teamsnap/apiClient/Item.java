package teamsnap.apiClient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
	private Attribute[] data;

	public Attribute[] getData() {
		return data;
	}

	public void setData(Attribute[] data) {
		this.data = data;
	}
	
	
}
