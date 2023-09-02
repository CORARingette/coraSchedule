package cora.page;

import java.util.ArrayList;
import java.util.List;

public class CwPageArenaNames {

	String arenaName_m;
	List<String> nameList_m;

	public CwPageArenaNames(String arenaName, List<String> nameList) {
		nameList_m = nameList;
		arenaName_m = arenaName;
	}

	public CwPageArenaNames(String arenaName) {
		nameList_m = new ArrayList<String>();
		arenaName_m = arenaName;
	}

	public List<String> getNameList() {
		return nameList_m;
	}

	public String getArenaName() {
		return arenaName_m;
	}

}
