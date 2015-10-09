package application.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Rule_Replace extends Rule_ {

	private final BooleanProperty Enabled;	
	private final IntegerProperty SearchMode;	// 0 - simple text search;
												// 1 - regexp
												// 2 - regexp multiline mode
												// 3 - regexp dotall mode
												// 4 - regexp multiline + dotall mode
	
	private final StringProperty FindWhat;
	private final StringProperty ReplaceWith;

	public Rule_Replace() {
        this(null);
    }		
	
	public Rule_Replace(String Name) {
		super(Name);
		this.Enabled = new SimpleBooleanProperty(true);
		this.SearchMode = new SimpleIntegerProperty(0);
		this.FindWhat = new SimpleStringProperty("");
		this.ReplaceWith = new SimpleStringProperty("");
    }

	public Rule_Replace(String Name, Rule_Replace src) {
		super(Name, src);
		
		this.Enabled = new SimpleBooleanProperty(src.getEnabled());
		this.SearchMode = new SimpleIntegerProperty(src.getSearchMode());
		this.FindWhat = new SimpleStringProperty(src.getFindWhat());
		this.ReplaceWith = new SimpleStringProperty(src.getReplaceWith());
    }
	
    public boolean getEnabled() {
        return Enabled.get();
    }

    public void setEnabled(boolean Enabled) {
        this.Enabled.set(Enabled);
    }

    public BooleanProperty enabledProperty() {
        return Enabled;
    } 
	
	public int getSearchMode() {
        return SearchMode.get();
    }

    public void setSearchMode(int SearchMode) {
        this.SearchMode.set(SearchMode);
    }

    public IntegerProperty searchModeProperty() {
        return SearchMode;
    }	
    

	public String getFindWhat() {
        return FindWhat.get();
    }

    public void setFindWhat(String FindWhat) {
        this.FindWhat.set(FindWhat);
    }

    public StringProperty findWhatProperty() {
        return FindWhat;
    }
    
    
	public String getReplaceWith() {
        return ReplaceWith.get();
    }

    public void setReplaceWith(String ReplaceWith) {
        this.ReplaceWith.set(ReplaceWith);
    }

    public StringProperty replaceWithProperty() {
        return ReplaceWith;
    }	    
}
