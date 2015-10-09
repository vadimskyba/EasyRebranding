package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import application.App;
import application.model.Rule_Group;
import application.model.Variable;

public class Controller_Variable extends Controller_ {

	protected Variable variable;
	
	@FXML private TextField nameField;
	@FXML private TextField valueField;
	@FXML private TextArea descriptionArea;
	
	
	public Controller_Variable () {
	}
	
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);
    	
		addListener_OnEditCommit(nameField, this::onEditCommit_nameField);
		addListener_OnEditCommit(valueField, this::onEditCommit_valueArea);
		addListener_OnEditCommit(descriptionArea, this::onEditCommit_descriptionArea);
    }
    
	// Setting reference to model Rule_Group instance
	public void Bind(Variable variable) {
		Unbind();

		if(variable != null) {
			nameField.textProperty().setValue(variable.getName());
			valueField.textProperty().setValue(variable.getValue());
			descriptionArea.textProperty().setValue(variable.getDescription());
			
			this.variable = variable;
		}
	}
	
	@Override
	protected void Unbind() {
		
		if(variable != null){
			variable = null;
		}
	}	
	
	//*********************************************************************************************
    // Event handling registered with this::addListener_OnEditCommit(...) function
	//*********************************************************************************************
	
    public Boolean onEditCommit_nameField (Node node) {
    	
    	if(nameField.getText().equals(variable.getName())) {
    		return false;
    	}
    	else if( nameField.getText().isEmpty() ) {

    		nameField.setText(variable.getName()); // Attention. Change nameField before MessageBox to prevent from lost focus
			
			App.MessageBox(
					AlertType.WARNING,
					"Warning...",
					"Cannot rename variable",
					"Please specify variable name.");
			
			return false;
		}	        	
		else {
			
        	for (Variable o : App.data().varData()) {
        		
        		if(o == variable)
        			continue;

        		// if newName matches existing RuleVariable: discard changes
        		else if(o.getName().equals(nameField.getText())) {
    	        	
        			
        			
        			App.MessageBox(
        					AlertType.WARNING,
        					"Warning...",
        					"Cannot rename variable",
        					"A variable with the name '" + variable.getName() + "' already exists.\nPlease specify another name.");
        			
        			nameField.setText(variable.getName()); // Attention. Change nameField before MessageBox to prevent from lost focus
        			
        			return false;
                }
            }
		}

		variable.setName(nameField.getText());
		App.data().setModifiedFlag(true);
		return true;
	}

    public Boolean onEditCommit_valueArea (Node node) {
    	if(variable != null) {
    		variable.valueProperty().set(valueField.textProperty().get());
    		App.data().setModifiedFlag(true);
    	}
		return true;
	}
    
    public Boolean onEditCommit_descriptionArea (Node node) {
    	if(variable != null) {
    		variable.descriptionProperty().set(descriptionArea.textProperty().get());
    		App.data().setModifiedFlag(true);
    	}
		return true;
	}
    
    //*********************************************************************************************
    // Event handling
    //*********************************************************************************************
    
    @FXML
    private void OnButton_VariableRemove () {
    	
    	if(variable != null) {
	    	Variable variableToRemove = this.variable;
	    	Unbind();
	    	App.data().removeVariable(variableToRemove);
    	}
    	
    }
}
