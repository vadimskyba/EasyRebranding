package application.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * Auxiliary model Rule_ class.
 * Used only as a data of root TreeItem.
 * Doesn't attend in marshalling.
 *
 */
public class Rule_Root extends Rule_ implements IRule_Node {

	private final ObservableList<Rule_> childData;
	
	public Rule_Root(ObservableList<Rule_> childData) {
		super("Rule data root");
		
		this.childData = childData;
	}
	
	@Override
	public void AddChild (Rule_ child) {
		// Add checker function to be sure child doesn't present in list yet
		childData.add(child);
	}
	
	@Override
	public ObservableList<Rule_> getChildren() {
		return childData;
	}
}
