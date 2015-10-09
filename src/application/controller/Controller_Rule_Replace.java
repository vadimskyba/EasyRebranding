package application.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import application.App;
import application.controller.util.Dialog_NewRule;
import application.controller.util.ChangeNotifier.event;
import application.model.Rule_;
import application.model.Rule_Group;
import application.model.Rule_Replace;
import application.model.Source_;
import application.model.Variable;

public class Controller_Rule_Replace extends Controller_ {
	
	protected Rule_Replace ruleReplace;
	
	@FXML private CheckBox enabledCheckBox;
	ChangeListener<Boolean> enabledCheckBox_ChangeListener;
	
	@FXML private TextField nameField;
	@FXML private TextArea descriptionArea;	
	@FXML private ChoiceBox searchModeCBox;
	@FXML private TextField findWhatField;
	@FXML private TextField replaceWithField;
	@FXML private TableView<Source_> sourcesTable;
	@FXML private TableColumn<Source_, Boolean> selectedColumn;
    @FXML private TableColumn<Source_, String> nameColumn;
    @FXML private TableColumn<Source_, String> pathColumn;  
    
	public Controller_Rule_Replace() {
    }
	
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);
        assert searchModeCBox != null : "fx:id=\"choiceboxSearchMode\" was not injected: check your FXML file 'RulesLayout.fxml'.";

        enabledCheckBox_ChangeListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	App.data().setModifiedFlag(true);
            }
        };        
        
        // Initialize Rule Types ChoiceBox:
        searchModeCBox.setItems(FXCollections.observableArrayList("simple", "regexp", "regexp multiline", "regexp dotall", "regexp all modes"));
        
        addListener_OnEditCommit(nameField, this::onEditCommit_nameField);
        addListener_OnEditCommit(descriptionArea, this::onEditCommit_descriptionArea);
        addListener_OnEditCommit(findWhatField, this::onEditCommit_findWhatField);
        addListener_OnEditCommit(replaceWithField, this::onEditCommit_replaceWithField);
      
        searchModeCBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            	if(ruleReplace != null) {
	            	assert (((int)number2) < 2 ) : "Controller_Rule_Replace::searchModeCBox : unbound choice selected";
	            	ruleReplace.setSearchMode((int)number2);
            	}
            }
        });
    	
        // Initialize the variable table with the two columns and specify list with data
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		pathColumn.setCellValueFactory(cellData -> cellData.getValue().relativePathProperty());
		selectedColumn.setCellValueFactory(new PropertyValueFactory<Source_,Boolean>(null) {});
		
		sourcesTable.setEditable(true);
		sourcesTable.setItems(App.data().srcData());
		
		selectedColumn.setCellFactory(p -> new CheckBoxTableCell<Source_, Boolean> () {

			@Override
			public void updateItem(Boolean item, boolean empty) {
				 super.updateItem(item, empty);

			     if (empty) {
			         setText(null);
			         setGraphic(null);
			     }
			     else if (getGraphic() instanceof CheckBox) {
			    	 
			    	 Source_ srcObj = (Source_)this.getTableRow().itemProperty().getValue();
			    	 if(srcObj != null) {

			    		 boolean isRuleAssigned = false;
				    	 
				    	 if(ruleReplace != null) {
				    		 for (Rule_ assignedRule : srcObj.getAssignedRules()) {
				    			 if(assignedRule == ruleReplace)
				    				 isRuleAssigned = true;
				    		 }
				    	 }
	
				    	 CheckBox cb = new ListenerCheckBox(isRuleAssigned, srcObj);
				    	 this.setGraphic(cb);
			    	 }
			    }
			}
		});
		
		App.notifier().addListener(this, event.UPD_SRC_RULEASSIGN);
    }
    
    private class ListenerCheckBox extends CheckBox {
    	
    	Source_ assignedSourceObj;
    	private ChangeListener<Boolean> listener;
    	
    	public ListenerCheckBox (Boolean bChecked, Source_ assignedSourceObj) {
    		super();
    		
    		this.assignedSourceObj = assignedSourceObj;
    		selectedProperty().set(bChecked);
    		
    		selectedProperty().addListener(new ChangeListener<Boolean>() {
	             @Override
	             public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
	             	if(ruleReplace != null) {
	             		
				    	 if(ruleReplace != null) {
				    		 
				    		 if(newValue == true)
				    			 assignedSourceObj.getAssignedRules().add(ruleReplace);
				    		 else
				    			 while(assignedSourceObj.getAssignedRules().remove(ruleReplace));
				    		 
				    		 App.notifier().notifyControllers(Controller_Rule_Replace.this, event.UPD_SRC_RULEASSIGN, ruleReplace);
				    		 App.data().setModifiedFlag(true);
				    	 }
	             	}
	             }
	         });
    	}
    }
    
    
	// Binding controls to model
	public void Bind(Rule_Replace ruleReplace) {
		
		if(ruleReplace != null) {
			
			Unbind();
			
			enabledCheckBox.selectedProperty().bindBidirectional(ruleReplace.enabledProperty());
			enabledCheckBox.selectedProperty().addListener(enabledCheckBox_ChangeListener);
			
			
			nameField.textProperty().setValue(ruleReplace.getName());
			descriptionArea.textProperty().setValue(ruleReplace.getDescription());
			findWhatField.textProperty().setValue(ruleReplace.getFindWhat());
			replaceWithField.textProperty().setValue(ruleReplace.getReplaceWith());

			searchModeCBox.getSelectionModel().select(ruleReplace.getSearchMode());

			RefreshSourcesTable();
			
			this.ruleReplace = ruleReplace;		
		}
		else
		{
			Unbind();
		}
		
	}
	
	@Override
	protected void Unbind() {

		if(ruleReplace != null){
			enabledCheckBox.selectedProperty().unbindBidirectional(ruleReplace.enabledProperty());
			enabledCheckBox.selectedProperty().removeListener(enabledCheckBox_ChangeListener);
			ruleReplace = null;
		}
	}
    
	private void RefreshSourcesTable () {
		
		// Workaround to refresh sourcesTable data
		sourcesTable.setItems(null);
		sourcesTable.layout();
		sourcesTable.setItems(App.data().srcData());
	}
	
	@Override
    public void onNotify (event event, Object... p) {
		
    	if(	event == event.UPD_SRC_RULEASSIGN ) {
   		/*
    		assert p.length == 1;
    		assert p[0] instanceof Rule_;
    		Rule_ updatedRule = (Rule_)p[0];
    		*/
    		RefreshSourcesTable();
    	}
    }
	
    ///////////////////////////////////////////////////////////////////////////////
    // Event handling registered with this::addListener_OnEditCommit(...) function
    //////////////////////////////////////////////////////////////////////////////
	
    public Boolean onEditCommit_nameField (Node node) {
    	if(ruleReplace != null) {

    		if(nameField.getText().equals(ruleReplace.getName())) {
        		return false;
        	}
        	else if(nameField.getText().isEmpty()) {

        		nameField.setText(ruleReplace.getName()); // Attention. Change nameField before MessageBox to prevent from lost focus
    			
    			App.MessageBox(
    					AlertType.WARNING,
    					"Warning...",
    					"Cannot rename rule",
    					"Please specify rule name.");
    			
    			return false;
    		}	        	
    		else if(App.data().FindRuleRecursive(App.data().ruleData(), nameField.getText()) != null) {

    				nameField.setText(ruleReplace.getName()); // Attention. Change nameField before MessageBox to prevent from lost focus
    			
    				App.MessageBox(
        					AlertType.WARNING,
        					"Warning...",
        					"Cannot rename rule",
        					"A rule with the name '" + nameField.getText() + "' already exists.\nPlease specify another name.");
        			
        			return false;
    		}
    		else {
    			ruleReplace.nameProperty().set(nameField.textProperty().get());
    	    	App.notifier().notifyControllers(this, event.UPD_RULE_NAME, ruleReplace);
    	    	App.data().setModifiedFlag(true);
    	    	return true;
    		}
    	}
    		
    	return false;
	}

    public Boolean onEditCommit_descriptionArea (Node node) {
    	if(ruleReplace != null) {
    		ruleReplace.descriptionProperty().set(descriptionArea.textProperty().get());
    		App.data().setModifiedFlag(true);
    	}
		return true;
	}

    public Boolean onEditCommit_findWhatField (Node node) {
    	if(ruleReplace != null) {
    		ruleReplace.findWhatProperty().set(findWhatField.textProperty().get());
    		App.data().setModifiedFlag(true);
    	}
		return true;
	}

    public Boolean onEditCommit_replaceWithField (Node node) {
    	if(ruleReplace != null) {
    		ruleReplace.replaceWithProperty().set(replaceWithField.textProperty().get());
    		App.data().setModifiedFlag(true);
    	}
		return true;
	}
    
    //*********************************************************************************************
    // @FXML Event handling
    //*********************************************************************************************
	
    @FXML
    private void OnButton_RuleRemove() {
    	if(ruleReplace != null) {
	    	Rule_ ruleToRemove = this.ruleReplace;
	    	Unbind();
    		App.data().removeRule(ruleToRemove);
    		App.data().setModifiedFlag(true);
    	}
    }
    
    @FXML
    private void OnButton_RuleDuplicate() {
    	if(ruleReplace != null) {
	    	
        	Dialog_NewRule dialog = new Dialog_NewRule ();
        	Optional<String> result = dialog.showAndWait();
        	
        	if (result.isPresent()){
        		NestController_Rules rulesController = (NestController_Rules)App.controllers().GetInstance("NestController_Rules");
        		rulesController.DuplicateRule(result.get(), ruleReplace);
        		App.data().setModifiedFlag(true);
        	}
    	}
    }
    
}
