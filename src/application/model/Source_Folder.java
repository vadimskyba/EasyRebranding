package application.model;

import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.model.Rule_;

//@XmlRootElement(name="Source_Folder")
public class Source_Folder extends Source_ {

	//protected final StringProperty FolderName;
	protected final StringProperty IncludeMask;
	protected final StringProperty ExcludeMask;
	
	private final BooleanProperty IncludeMaskEnabled;
	private final BooleanProperty ExcludeMaskEnabled;
	
	//private final Source_Folder ParentFolder;
	//private final ObservableList<Source_> ChildSources = FXCollections.observableArrayList();
	
	public Source_Folder () {
		this("","");
	}
	
	//public Source_Folder(String FolderName, Source_Folder ParentFolder) {
	public Source_Folder(String Name, String RelativePath) {
		super(Name,RelativePath);
		
		this.IncludeMaskEnabled = new SimpleBooleanProperty(false);
		this.ExcludeMaskEnabled = new SimpleBooleanProperty(false);
		
		this.IncludeMask = new SimpleStringProperty("");
		this.ExcludeMask = new SimpleStringProperty("");
		
		//this.ParentFolder = ParentFolder;
    }
	
    //**************************************************** IncludeMaskEnabled
	
    public boolean getIncludeMaskEnabled() {
        return IncludeMaskEnabled.get();
    }

    public void setIncludeMaskEnabled(boolean Enabled) {
        this.IncludeMaskEnabled.set(Enabled);
    }

    public BooleanProperty includeMaskEnabledProperty() {
        return IncludeMaskEnabled;
    }
	
    //**************************************************** ExcludeMaskEnabled
    
    public boolean getExcludeMaskEnabled() {
        return ExcludeMaskEnabled.get();
    }

    public void setExcludeMaskEnabled(boolean Enabled) {
        this.ExcludeMaskEnabled.set(Enabled);
    }

    public BooleanProperty excludeMaskEnabledProperty() {
        return ExcludeMaskEnabled;
    }
    
	//**************************************************** IncludeMask
    
  	public String getIncludeMask() {
  		return IncludeMask.get();
	}

	public void setIncludeMask(String IncludeMask) {
		this.IncludeMask.set(IncludeMask);
	}

	public StringProperty includeMaskProperty() {
		return IncludeMask;
	}
      
	//**************************************************** ExcludeMask
    
  	public String getExcludeMask() {
  		return ExcludeMask.get();
	}

	public void setExcludeMask(String ExcludeMask) {
		this.ExcludeMask.set(ExcludeMask);
	}

	public StringProperty excludeMaskProperty() {
		return ExcludeMask;
	}      

	//*****************************************************************
}
