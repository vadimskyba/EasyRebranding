package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import application.App;
import application.model.Rule_Replace;
import application.model.Source_Folder;

public class Controller_Source_Folder extends Controller_ {

	private Source_Folder sourceFolder;
	
	@FXML private CheckBox includeMaskCheckBox;
	@FXML private CheckBox excludeMaskCheckBox;
	
	@FXML private Label folderPath;
	@FXML private TextField includeMask;
	@FXML private TextField excludeMask;

    public Controller_Source_Folder() {
    }
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);
    	
		addListener_OnEditCommit(includeMask, this::onEditCommit_includeMask);
		addListener_OnEditCommit(excludeMask, this::onEditCommit_excludeMask);
    }
    
	// Binding controls to model
	public void Bind(Source_Folder sourceFolder) {
		
		Unbind();
		
		if(sourceFolder != null)
		{
			includeMaskCheckBox.selectedProperty().bindBidirectional(sourceFolder.includeMaskEnabledProperty());
			excludeMaskCheckBox.selectedProperty().bindBidirectional(sourceFolder.excludeMaskEnabledProperty());
			
			folderPath.textProperty().setValue(sourceFolder.getFullRelativePath());
			includeMask.textProperty().setValue(sourceFolder.getIncludeMask());
			excludeMask.textProperty().setValue(sourceFolder.getExcludeMask());
			
			this.sourceFolder = sourceFolder;		
		}
	}
	
	@Override
	protected void Unbind() {
		
		if(sourceFolder != null){
			
			includeMaskCheckBox.selectedProperty().unbindBidirectional(sourceFolder.includeMaskEnabledProperty());
			excludeMaskCheckBox.selectedProperty().unbindBidirectional(sourceFolder.excludeMaskEnabledProperty());
			
			sourceFolder = null;
		}
	}
    
	//*********************************************************************************************
    // Event handling registered with this::addListener_OnEditCommit(...) function
	//*********************************************************************************************
    
    public Boolean onEditCommit_includeMask (Node node) {
    	assert (sourceFolder != null);
    	sourceFolder.includeMaskProperty().set(includeMask.textProperty().get());
		return true;
	}
 
    public Boolean onEditCommit_excludeMask (Node node) {
    	assert (sourceFolder != null);
    	sourceFolder.excludeMaskProperty().set(excludeMask.textProperty().get());
		return true;
	}
}
