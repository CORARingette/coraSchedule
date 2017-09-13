package teamsnap.apiClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Response {

	private Collection collection;

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public List<Item> getItems() {
		return collection == null ? Collections.<Item>emptyList()
				: collection.getItems() == null ? Collections.<Item>emptyList() : Arrays.asList(collection.getItems());
	}
}
