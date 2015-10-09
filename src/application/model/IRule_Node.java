package application.model;

import javafx.collections.ObservableList;
import application.model.IRule_;

public interface IRule_Node extends IRule_ {

	public void AddChild (Rule_ child);
	public ObservableList<Rule_> getChildren();
}
