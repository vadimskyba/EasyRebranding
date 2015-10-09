package application.processor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import application.model.Rule_Replace;
import application.model.Source_;
import application.model.Source_File;

public class ReplaceData {

	private Source_ source;
	private Charset workCharset;
	private IntegerProperty workEolType;

	private final List <Action_Replace> actionList = new ArrayList<>();
	
	private final StringProperty statusProperty = new SimpleStringProperty(); // Property for view data in TableView
	private final StringProperty charsetProperty = new SimpleStringProperty();	// Property for view data in TableView
	
	public ReplaceData (Source_ source) {
		assert (source != null);
		this.source = source;
		this.workEolType = new SimpleIntegerProperty(0);
		
		statusProperty.set("not processed");
		charsetProperty.set("not processed");
	}
	
	public Source_ getSource () {
		return source;
	}
	
	public List <Action_Replace> getReplaceActionList () {
		return actionList;
	}
	
    public StringProperty statusProperty() {
        return statusProperty;
    }
    
    public String getStatus () {
    	return statusProperty.get();
    }
    
    public void setStatus(String status) {
    	statusProperty.set(status);
    }
    
    public StringProperty charsetProperty() {
        return charsetProperty;
    }
    
	//***************************************************************** Purposely renamed functions to SET_ GET_ to avoid from marshalling
	public void SET_WorkCharset (Charset charset) {
		this.workCharset = charset;
		if(charset == null)
			this.charsetProperty.set("bad charset");
		else
			this.charsetProperty.set(charset.name());
	}
	
	public Charset GET_WorkCharset () {
		return workCharset;
	}

	public void SET_WorkEolType (int eolType) {
		this.workEolType.set(eolType);
	}
	
	public int GET_WorkEolType () {
		return this.workEolType.get();
	}

    
    
	public void clean() {
		actionList.clear();
		statusProperty.set("not processed");
	}
	
	public void cleanActionList() {
		actionList.clear();
	}
}
