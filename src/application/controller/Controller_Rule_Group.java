package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.Node;

import application.App;
import application.model.Rule_;
import application.model.Rule_Group;

public class Controller_Rule_Group extends Controller_ {

	protected Rule_Group ruleGroup;
	
	//@FXML private CheckBox enabledCheckBox;
	@FXML private TextField nameField;
	@FXML private TextArea descriptionArea;
	
    public Controller_Rule_Group() {
    }
	
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);
    	
		addListener_OnEditCommit(nameField, this::onEditCommit_nameField);
		addListener_OnEditCommit(descriptionArea, this::onEditCommit_descriptionArea);    	
    }
    
	// Setting reference to model Rule_Group instance
	public void Bind(Rule_Group ruleGroup) {
		
		if(ruleGroup != null)
		{
			Unbind();

			nameField.textProperty().setValue(ruleGroup.getName());
			descriptionArea.textProperty().setValue(ruleGroup.getDescription());
			
			this.ruleGroup = ruleGroup;			
		}
		else
		{
			Unbind();
		}
		
	}
	
	@Override
	protected void Unbind() {
		
		if(ruleGroup != null){
			ruleGroup = null;
		}
	}	
	
	
	//*********************************************************************************************
    // Event handling registered with this::addListener_OnEditCommit(...) function
	//*********************************************************************************************
	
    public Boolean onEditCommit_nameField (Node node) {
    	if(ruleGroup != null) {
    		ruleGroup.nameProperty().set(nameField.textProperty().get());
    	}
		return true;
	}

    public Boolean onEditCommit_descriptionArea (Node node) {
    	if(ruleGroup != null) {
    		ruleGroup.descriptionProperty().set(descriptionArea.textProperty().get());
    	}
		return true;
	}

    
    //*********************************************************************************************
    // @FXML Event handling
    //*********************************************************************************************
    
    
}
