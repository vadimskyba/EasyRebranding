package application.model;

import javax.xml.bind.annotation.XmlType;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import application.model.Rule_;

@XmlType(propOrder = {"name", "description"})
public abstract class Rule_ implements IRule_ {

	private final StringProperty Name;
	private final StringProperty Description;
	
	
	public Rule_() {
        this(null);
    }
	
	public Rule_(String Name) {
		if(Name == null)
			Name = new String("");

		this.Name = new SimpleStringProperty(Name);
		
		// Default initialization
		this.Description = new SimpleStringProperty("");
    }

	public Rule_(String Name, Rule_ src) {
		
		this(Name);
		this.Description.set(src.getDescription());
    }
	
	@Override
	public String toString () {
		return getName();
	}	
	
	public String getName() {
        return Name.get();
    }

    public void setName(String Name) {
        this.Name.set(Name);
    }

    public StringProperty nameProperty() {
        return Name;
    }
    
    public String getDescription() {
        return Description.get();
    }

    public void setDescription(String Description) {
        this.Description.set(Description);
    }

    public StringProperty descriptionProperty() {
        return Description;
    }	

}
