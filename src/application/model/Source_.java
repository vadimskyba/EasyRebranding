package application.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import application.App;
import application.Storage;
import application.Storage.*;
import application.processor.ReplaceData;
import application.util.PathTool;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Source_ implements ISource_ {
	
	private final StringProperty Name; // file or folder Name without path
	private final StringProperty RelativePath; // relative path (without Name)
	
	private final BooleanProperty AssignedRulesEnabled = new SimpleBooleanProperty(true);	
	
	private final ObservableList<Rule_> AssignedRules = FXCollections.observableArrayList();
	
	// Auxiliary class to hold replacement processing data
	private final ReplaceData replaceData = new ReplaceData(this);
	
	public Source_ () {
		this("","");
	}
	
	public Source_ (String Name, String RelativePath) {
		super();
		
		this.Name = new SimpleStringProperty(Name);
		this.RelativePath = new SimpleStringProperty(RelativePath);
	}
	
	@Override
	public String toString () {
		return getName();
	}
	
	public ReplaceData rdata () {
		return replaceData;
	}
	
    //**************************************************** Name
    
	public String getName() {
        return Name.get();
    }

    public void setName(String Name) {
        this.Name.set(Name);
    }

    public StringProperty nameProperty() {
        return Name;
    }
    
    //**************************************************** RelativePath
    
	public String getRelativePath() {
        return RelativePath.get();
    }

    public void setRelativePath(String RelativePath) {
        this.RelativePath.set( PathTool.NormalizePath(RelativePath) );
    }

    public StringProperty relativePathProperty() {
        return RelativePath;
    }
    
    //**************************************************** AssignedRulesEnabled
    
    public boolean getAssignedRulesEnabled() {
        return AssignedRulesEnabled.get();
    }

    public void setAssignedRulesEnabled(boolean Enabled) {
        this.AssignedRulesEnabled.set(Enabled);
    }

    public BooleanProperty assignedRulesEnabledProperty() {
        return AssignedRulesEnabled;
    }
    
    //*****************************************************************

	public String getFullRelativePath() {
        return RelativePath.get() + Name.get();
    }

	public String getSafeFullRelativePath1 () {
		
		String fullRelativePath = PathTool.NormalizePath(getFullRelativePath());
		
		while (fullRelativePath.lastIndexOf("//") >= 0)
			fullRelativePath = fullRelativePath.replace("//", "/");
		
		while(fullRelativePath.startsWith("/"))
			fullRelativePath = fullRelativePath.substring(1);
		
		return fullRelativePath;
	}

    public ObservableList<Rule_> getAssignedRules () {
    	return AssignedRules;
    }

    //***************************************************************** Marshalling purposes
    
    // Because ObservableList doesn't support marshalling cast it to List
	// Method name should start with "get..."
    
    @XmlElementWrapper(name = "assignedRules")
    @XmlElement(name = "rule")
    @XmlJavaTypeAdapter(XmlAdapter_Source_AssignedRules.class)
    private List<Rule_> getAssignedRules_xml () {
    	return AssignedRules;
    }
    
}
