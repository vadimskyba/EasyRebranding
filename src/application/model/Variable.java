package application.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Variable {
	private final StringProperty Name;
	private final StringProperty Value;
	private final StringProperty Description;

	public Variable() {
        this(null, null);
    }
	
	public Variable(String Name, String Value) {
		super();
		
		if(Name == null)
			Name = new String("");
		
		if(Value == null)
			Value = new String("");
		
        this.Name = new SimpleStringProperty(Name);
        this.Value = new SimpleStringProperty(Value);
        this.Description = new SimpleStringProperty("");
    }
	
	@Override
	public String toString () {
		return getName();
	}	
	
	public String getName() {
        return Name.get();
    }

    public void setName(String firstName) {
        this.Name.set(firstName);
    }

    public StringProperty nameProperty() {
        return Name;
    }
    
	public String getValue() {
        return Value.get();
    }

    public void setValue(String firstValue) {
        this.Value.set(firstValue);
    }

    public StringProperty valueProperty() {
        return Value;
    }
    
	public String getDescription() {
        return Description.get();
    }

    public void setDescription(String description) {
        this.Description.set(description);
    }

    public StringProperty descriptionProperty() {
        return Description;
    }
}
