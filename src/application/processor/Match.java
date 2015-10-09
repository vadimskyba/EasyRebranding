package application.processor;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Match {
	
	private Action_Replace parentAction;
	private int position;
	private StringProperty foundStr;
	private StringProperty replaceStr;	
	
	List <Match> conflictList;
	
	public Match (Action_Replace parentAction, int position, String foundStr, String replaceStr) {
		this.parentAction = parentAction;
		this.position = position;
		this.foundStr = new SimpleStringProperty(foundStr);
		this.replaceStr = new SimpleStringProperty(replaceStr);
	}

	public int getPosition () {
		return position;
	}
	
	public String getFoundStr () {
		return foundStr.get();
	}
	
	public StringProperty foundStrProperty() {
		return foundStr;
	}
	
	public String getReplaceStr () {
		return replaceStr.get();
	}

	public StringProperty replaceStrProperty() {
		return replaceStr;
	}
	
	public Action_Replace getOwnerAction () {
		return parentAction;
	}
	
	public void addConflict (Match conflictMatch) {
		if(conflictList == null)
			conflictList = new ArrayList<>();
		
		conflictList.add(conflictMatch);
	}
}