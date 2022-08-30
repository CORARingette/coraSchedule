package cora.page;

import java.util.List;

import io.dropwizard.views.View;

public class CwPageArenaList extends View {
	public static final String MESSAGE_PARAM = "message";
	
	List<CwPageArenaNames> arenaInfo_m;
	
	public CwPageArenaList(String templateName) {
			super(templateName);
		}

	public CwPageArenaList setArenaInfo(List<CwPageArenaNames> arenaInfo) {
		arenaInfo_m = arenaInfo;
		return this;
	}

	public List<CwPageArenaNames> getArenaInfo() {
		return arenaInfo_m;
	}

}
