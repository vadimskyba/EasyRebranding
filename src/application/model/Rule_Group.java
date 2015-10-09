package application.model;

import java.util.List;

import javax.xml.bind.annotation.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Rule_Group extends Rule_ implements IRule_Node {

	private ObservableList<Rule_> childRuleData = FXCollections.observableArrayList();
	
	public Rule_Group() {
        this(null);
    }
	
	public Rule_Group(String Name) {
		super(Name);
	}
	
	@Override
	public void AddChild (Rule_ child) {
		// Add checker function to be sure child doesn't present in list yet
		childRuleData.add(child);
	}
	
	@Override
	public ObservableList<Rule_> getChildren() {
		return childRuleData;
	}
	
	
	
	//***************************************************************** Marshalling purposes
    
    // Because ObservableList doesn't support marshalling cast it to List
	// Method name should start with "get..."
	
	@XmlElementWrapper(name = "rules")
    @XmlElements({ 
        @XmlElement(name = "group", type=Rule_Group.class),
        @XmlElement(name = "replace", type=Rule_Replace.class)
    })
    private List<Rule_> getChildren_xml () {
    	return childRuleData;
    }
}
