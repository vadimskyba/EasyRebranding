package application.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import application.App;
import application.controller.util.ChangeNotifier.event;
import application.model.Rule_;
import application.model.Rule_Group;
import application.model.Rule_Replace;
import application.model.Rule_Root;
import application.model.Source_File;
import application.util.EOLDecoder;
import application.util.EOLDecoder.EOLType;
import application.util.PathTool;

public class Controller_Source_File extends Controller_ {

	private Source_File sourceFile;
	
	@FXML private Label filePath;
	@FXML private CheckBox assignedRulesCheckBox;
	ChangeListener<Boolean> assignedRulesCheckBox_ChangeListener;
	
	@FXML private TreeView<Rule_> assignedRulesTree;
	
	@FXML private ComboBox charsetCBox;
	@FXML private ComboBox outCharsetCBox;
	@FXML private ChoiceBox outEolCBox;
	
    public Controller_Source_File() {
    }
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);
    	
    	charsetCBox.setItems(FXCollections.observableArrayList("Auto", "UTF-8", "US-ASCII", "ISO-8859-1", "UTF-16BE", "UTF-16LE", "UTF-16", "cp1252"));
    	outCharsetCBox.setItems(FXCollections.observableArrayList("As input", "UTF-8", "US-ASCII", "ISO-8859-1", "UTF-16BE", "UTF-16LE", "UTF-16", "cp1252"));
    	outEolCBox.setItems(FXCollections.observableArrayList("As input", "Windows", "UNIX/OSX", "Old Mac"));
    	
    	assignedRulesCheckBox_ChangeListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	App.data().setModifiedFlag(true);
            }
        };
    	
    	charsetCBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String val, String val2) {
            	if(sourceFile != null) {
            		if(val2.equalsIgnoreCase("Auto"))
            			sourceFile.setCharset("");
            		else
            			sourceFile.setCharset(val2);
            		
            		App.data().setModifiedFlag(true);
            	}
            }
    	});
    	
    	outCharsetCBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String val, String val2) {
            	if(sourceFile != null) {
            		if(val2.equalsIgnoreCase("As input"))
            			sourceFile.setOutCharset("");
            		else
            			sourceFile.setOutCharset(val2);
            		
            		App.data().setModifiedFlag(true);
            	}
            }
    	});
    	
    	outEolCBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            	if(sourceFile != null) {
            		sourceFile.setOutEOL(EOLType.IntToStrType((int)number2));
            		App.data().setModifiedFlag(true);
            	}
            }
        });
    	
    	assignedRulesTree.setCellFactory(p -> new CheckBoxTreeCell<Rule_>() {
   	     @Override
   	     public void updateItem(Rule_ item, boolean empty) {
   	        super.updateItem(item, empty);

   	        if (empty) {
   	        	setText(null);
   	        	setGraphic(null);
   	        }
   	        else {
   	        	TreeItem<Rule_> treeItem = getTreeItem();
   	        	if (treeItem instanceof CheckBoxTreeItem) {
   	        		CheckBoxTreeItem<Rule_> cbTreeItem = (CheckBoxTreeItem<Rule_>) treeItem;
   	        		//setText(item.getName()); - unnecessary because Rule_::toString() was implemented
   	        		CheckBox cb = new CheckBox();
   	        		cb.indeterminateProperty().bindBidirectional(cbTreeItem.indeterminateProperty());
   	        		cb.selectedProperty().bindBidirectional(cbTreeItem.selectedProperty());
   	        		setGraphic(cb);
   	        	}
   	        	else {
   	        		//setText(item.getName());  - unnecessary because Rule_::toString() was implemented
   	        		setGraphic(null);
   	        	}
   	        }
   	     }

	    });
    	
    	TreeItem_Rule_Group<Rule_> rootItem = new TreeItem_Rule_Group<Rule_>(
    			new Rule_Root(App.data().ruleData()) // create auxiliary model object for root TreeItem (Controller_Source_File:Rule_Root)
    	);
    	assignedRulesTree.setRoot(rootItem);
    	
    	App.notifier().addListener(this, event.UPD_RULE_NAME);
    	App.notifier().addListener(this, event.UPD_SRC_RULEASSIGN);
    }
    
	// Binding controls to model
	public void Bind(Source_File sourceFile) {
		
		Unbind();
		
		if(sourceFile != null) {
			filePath.textProperty().setValue(PathTool.GetSpecificOSPath(PathTool.getSafeRelativePath(sourceFile.getFullRelativePath())));
			
			assignedRulesCheckBox.selectedProperty().bindBidirectional(sourceFile.assignedRulesEnabledProperty());
	    	assignedRulesCheckBox.selectedProperty().addListener(assignedRulesCheckBox_ChangeListener);
			
			
			String charsetStr = sourceFile.getCharset();
			if(charsetStr.length() == 0)
				charsetCBox.getSelectionModel().select(0);
			else
				charsetCBox.setValue(charsetStr);

			String outCharsetStr = sourceFile.getOutCharset();
			if(outCharsetStr.length() == 0)
				outCharsetCBox.getSelectionModel().select(0);
			else
				outCharsetCBox.setValue(outCharsetStr);

			String eolStr = sourceFile.getOutEOL();
			if(eolStr.length() == 0)
				outEolCBox.getSelectionModel().select(0);
			else if(eolStr.equalsIgnoreCase("win"))
				outEolCBox.getSelectionModel().select(1);
			else if(eolStr.equalsIgnoreCase("unix"))
				outEolCBox.getSelectionModel().select(2);
			else if(eolStr.equalsIgnoreCase("mac"))
				outEolCBox.getSelectionModel().select(3);
			else
				outEolCBox.getSelectionModel().select(0);
			
			this.sourceFile = sourceFile;
			RefreshAssignedRulesTreeItems(assignedRulesTree.getRoot(), true);
		}
	}
	
	@Override
	protected void Unbind() {

		if(sourceFile != null){
			assignedRulesCheckBox.selectedProperty().unbindBidirectional(sourceFile.assignedRulesEnabledProperty());
			assignedRulesCheckBox.selectedProperty().removeListener(assignedRulesCheckBox_ChangeListener);
			sourceFile = null;
		}
	}
	
    private void RefreshAssignedRulesTreeItems (TreeItem<Rule_> ruleTreeItem, boolean bUpdateChilds) {
    	
    	if(sourceFile != null) {
    	
	    	if(ruleTreeItem instanceof CheckBoxTreeItem_Rule) {
	    		
	    		CheckBoxTreeItem_Rule<Rule_>cbTreeItem  = (CheckBoxTreeItem_Rule<Rule_>)ruleTreeItem;
	    		Rule_ rule = cbTreeItem.getValue();
	    				
	    		boolean bSelected = false;
	
				for (application.model.Rule_ rule_i : sourceFile.getAssignedRules())	{
					if(rule_i == rule){
						bSelected = true;
						break;
					}
				}
	
				// Disable selected state (checkbox) listener before changing value
				cbTreeItem.disableListener();
				cbTreeItem.selectedProperty().set( bSelected );
				cbTreeItem.enableListener();
	
				// Reset value to refresh TreeItem's label
				ruleTreeItem.setValue(null);
				ruleTreeItem.setValue(rule);
			}
	    	
	    	if(bUpdateChilds) {
				for(TreeItem<Rule_> item : ruleTreeItem.getChildren())
					RefreshAssignedRulesTreeItems(item, bUpdateChilds);
	    	}
    	}
    }
    
	@Override
    public void onNotify (event event, Object... p) {
		
    	if(	event == event.UPD_RULE_NAME ||
   			event == event.UPD_SRC_RULEASSIGN ) {
    		
    		assert p.length == 1;
    		assert p[0] instanceof Rule_;
    		
    		Rule_ updatedRule = (Rule_)p[0];
    		TreeItem<Rule_> updatedTreeItem = GetTreeItemByRule(updatedRule, assignedRulesTree.getRoot());
    		RefreshAssignedRulesTreeItems(updatedTreeItem, false);
    	}
    }
    
	private TreeItem<Rule_> GetTreeItemByRule (Rule_ rule, TreeItem<Rule_> rootTreeItem) {
		
		TreeItem<Rule_> resTreeItem = null;
		
		for(TreeItem<Rule_> item : rootTreeItem.getChildren()) {
			resTreeItem = GetTreeItemByRule(rule, item);
			
			if(resTreeItem != null)
				return resTreeItem;
		}
		
		return rootTreeItem.getValue() == rule ? rootTreeItem : null;
		
	}
	
    //*********************************************************************************************
    // Event handling
    //*********************************************************************************************
    
	@FXML
	private void OnButton_RemoveFromSourcesList ()  {
		Source_File sourceFile = this.sourceFile;
		Unbind();
		//App.data().srcData().remove(sourceFile);
		App.data().setModifiedFlag( App.data().removeSource(sourceFile) );
		
	}
	
	//********************************************************************************************* class TreeItem_Rule_Group
	public class TreeItem_Rule_Group<Rule_> extends TreeItem<Rule_> {
		
		public TreeItem_Rule_Group(final Rule_Root node){
	    	super((Rule_)node, null);
	        
	    	if(node != null) {
	            addChildrenListener( (ObservableList<Rule_>)node.getChildren() );
	        }
	    }        
	    
	    public TreeItem_Rule_Group(final Rule_Group node){
	        this(node, (Node) null);
	    }

	    public TreeItem_Rule_Group(final Rule_Group node, Node graphic) {
	        super((Rule_)node, graphic);

	        if(node != null) {
	            addChildrenListener( (ObservableList<Rule_>)node.getChildren() );
	        }
	    }
	    
	    private TreeItem<Rule_> CreateRuleTreeItem (Rule_ ruleObj)
	    {
	    	TreeItem<Rule_> newTreeItem = null;
	    	
	    	if(ruleObj instanceof Rule_Group) {
	    		
    			Rule_Group childGroup = (Rule_Group)ruleObj;
    			newTreeItem = new TreeItem_Rule_Group<>(childGroup);
    		}
    		else {
    			
    			assert ruleObj instanceof Rule_Replace : "Exception: unknown type of Rule_ instance.";
    		
    			Rule_Replace childReplace = (Rule_Replace)ruleObj;
    			CheckBoxTreeItem_Rule<Rule_>cbTreeItem  = new CheckBoxTreeItem_Rule<>(ruleObj);
    			cbTreeItem.selectedProperty().set(false);
    			
    			if(sourceFile != null) {
    				for (application.model.Rule_ rule : sourceFile.getAssignedRules())
    					if(this.getValue() == rule)
    					{
    						// Disable selected state (checkbox) listener before changing value
    						cbTreeItem.disableListener();
    						cbTreeItem.selectedProperty().set(true);
    						cbTreeItem.enableListener();
    						break;
    					}
    			}
    			
    			newTreeItem = cbTreeItem;
    		}
	    	
	    	return newTreeItem;
	    }
	    
	    
	    private void addChildrenListener(ObservableList<Rule_> list) {
	    	
	    	// Attaching model to TreeView items
	    	list.forEach(child -> {
	    		this.getChildren().add( CreateRuleTreeItem(child) );
	    	});

	    	// Listener: update TreeView items on model changes
        	list.addListener((ListChangeListener<? super Rule_>) change -> {

				while (change.next()) {

					if(change.wasAdded()) {
						change.getAddedSubList().forEach(t -> {
							this.getChildren().add( CreateRuleTreeItem(t) );
						});
					}

					if(change.wasRemoved()) {
						change.getRemoved().forEach(t -> {
							
							final List<TreeItem<Rule_>> itemsToRemove = TreeItem_Rule_Group.this.getChildren()
								.stream()
								.filter(treeItem ->
									treeItem.getValue().equals(t)).collect(Collectors.toList());

								this.getChildren().removeAll(itemsToRemove);
						});
					}
					
					if(change.wasReplaced()) {

					}
					
					if(change.wasPermutated()) {
							
					}
				}

        	});
	    }
	    
	}
	
	//********************************************************************************************* class CheckBoxTreeItem_Rule
	public class CheckBoxTreeItem_Rule <Rule_> extends CheckBoxTreeItem <Rule_> {
		
		private boolean bListenerEnabled = false;
		private ChangeListener<Boolean> listener;
		
	    public CheckBoxTreeItem_Rule(final Rule_ rule){
	        this(rule, (Node) null);
	    }

	    public CheckBoxTreeItem_Rule(final Rule_ rule, Node graphic) {
	        super((Rule_)rule, graphic);
	        
	        listener = new ChangeListener<Boolean>() {
	        	@Override
                public void changed(ObservableValue observable,Boolean oldValue, Boolean newValue) {
                	if(sourceFile != null) {
                		
    					if(newValue == true) {
    						App.data().AssignRule(sourceFile, (application.model.Rule_)getValue());
    					}
    					else if(oldValue == true) {
    						App.data().UnassignRule(sourceFile, (application.model.Rule_)getValue());
    					}
    					
    					App.notifier().notifyControllers(Controller_Source_File.this, event.UPD_SRC_RULEASSIGN, (application.model.Rule_)getValue());
    				}
                }
            };
            
            enableListener();
	    }
	    
		public void enableListener() {
			if(!bListenerEnabled) {
				bListenerEnabled = true;
				selectedProperty().addListener(listener);
			}
		}
		
		public void disableListener() {
			if(bListenerEnabled) {
				bListenerEnabled = false;
				selectedProperty().removeListener(listener);
			}
		}
	}
}
