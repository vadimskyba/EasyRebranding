package application.view.controls;

import javafx.scene.control.TableView;

public class TableViewEx<T>  extends TableView<T> {
	
	boolean ignoreSelChanges = false;
	
    public TableViewEx() {
        super();
    }

	public void setIgnoreSelChanges(boolean bIgnore) { ignoreSelChanges = bIgnore; }
	public boolean isIgnoreSelChanges() { return ignoreSelChanges; }
}