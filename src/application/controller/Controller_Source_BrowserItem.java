package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import application.App;
import application.controller.util.BrowserItem;
import application.controller.util.ChangeNotifier.event;
import application.model.Source_File;
import application.model.Source_Folder;

public class Controller_Source_BrowserItem extends Controller_ {

	private BrowserItem browserItem;
	
	@FXML private Label itemType;
	@FXML private Label itemPath;
	@FXML private Button addButton;
	
    public Controller_Source_BrowserItem() {
    }
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);
    }
    
	// Binding controls to model
	public void Bind(BrowserItem BrowserItem) {
		
		Unbind();
		
		if(BrowserItem != null) {
			
			String strType = BrowserItem.getFile().isFile() ? "File:" : "Folder:";
			addButton.setVisible(BrowserItem.getFile().isFile());	// Hide add Button for 'folder' item type. It doesn't implemented yet.
			itemType.textProperty().set(strType);
			itemPath.textProperty().set(BrowserItem.getFile().getAbsolutePath());
			this.browserItem = BrowserItem;		
		}
	}
	
	@Override
	protected void Unbind() {

		if(browserItem != null){
			browserItem = null;
		}
	}
	
	
    //*********************************************************************************************
    // @FXML Event handling
    //*********************************************************************************************
	
	@FXML
	private void OnButton_AddToSources () {
		if(browserItem != null)
		{
			if(browserItem.getFile().isFile()) {
				//Source_File source = new Source_File(browserItem.getFile().getPath());
				//browserItem.setSrcObj(source);
				//App.data().srcData().add(source);
				browserItem.setSrcObj(App.data().new_Source_File( browserItem.getFile().getPath() ));
				
				App.notifier().notifyControllers(this, event.UPD_SRC_SELECTION, null);
			}
			else if(browserItem.getFile().isDirectory()) {
				//Source_Folder source = new Source_Folder(browserItem.getFile().getPath());
				//browserItem.setSrcObj(source);
				//App.data().srcData().add(source);
				browserItem.setSrcObj(App.data().new_Source_Folder( browserItem.getFile().getPath() ));
				
				App.notifier().notifyControllers(this, event.UPD_SRC_SELECTION, null);
			}
		}
	}
}
