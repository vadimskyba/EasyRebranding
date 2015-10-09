package application.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;
import application.App;
import application.model.IRule_Node;
import application.model.Rule_;
import application.model.Rule_Group;
import application.model.Rule_Replace;
import application.model.Rule_Root;

public class NestController_Rules {

    private TreeView<Rule_> rulesTree;
    
    private Controller_Rule_Group ruleGroupController;
    private VBox groupLayout;
    
    private Controller_Rule_Replace ruleReplaceController;
    private VBox replaceLayout;
	
	
    public NestController_Rules (TreeView<Rule_> rulesTree) {
    	
    	App.controllers().Register(this);
    	this.rulesTree = rulesTree;
    	
    	try {
	    	FXMLLoader loader;
	    	
	        loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_Rule_Group.fxml"));
	        this.groupLayout = (VBox) loader.load();
	        this.ruleGroupController = loader.getController();
	        
	        loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_Rule_Replace.fxml"));
	        this.replaceLayout = (VBox) loader.load();
	        this.ruleReplaceController = loader.getController();
	    	
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	rulesTree.setCellFactory(p -> new CheckBoxTreeCell<Rule_>() {
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
      	
       	GroupTreeItem rootItem = new GroupTreeItem( new Rule_Root(App.data().ruleData()) );
       	rulesTree.setRoot(rootItem);
         
       	/* MOVED TO Controller_MAIN
       	// Handle TreeView selection changes
       	rulesTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
          	
       		if(newValue != null) {
       			Bind(newValue.getValue());
   			}
   			else if (oldValue != null)	{
   					UnbindAll();
   					SwitchChildLayout(selectedLayout, noselectLayout);
   			}
          	
       	});
       	*/
    }
    
    
	
    public Node Bind (Rule_ ruleObj) {
    	Unbind();
   		
    	if(ruleObj != null) {
        	if(ruleObj instanceof Rule_Group) {
        		
        		ruleGroupController.Bind((Rule_Group)ruleObj);
        		//SwitchChildLayout(selectedLayout, groupLayout);
        		return groupLayout;
        		
        	}
        	else if(ruleObj instanceof Rule_Replace) {
        		
        		ruleReplaceController.Bind((Rule_Replace)ruleObj);
        		//SwitchChildLayout(selectedLayout, replaceLayout);
        		return replaceLayout;
        	}
        	//else
        	//	SwitchChildLayout(selectedLayout, noselectLayout);
    	}
    	
    	return null;
    }
    
    
    
    private IRule_Node GetSelectedNode() {
    	
    	int selectedIndex = rulesTree.getSelectionModel().getSelectedIndex();
    	if(selectedIndex >= 0) {

    		TreeItem<Rule_> selectedItem = rulesTree.getTreeItem(selectedIndex);
    		
    		if(selectedIndex == 0 && selectedItem == null) // means TreeView has invisible root TreeItem selected
    			return (IRule_Node)rulesTree.getRoot().getValue();
    		
    		Rule_ selectedRuleObject = selectedItem.getValue();
    		
    		if(	selectedRuleObject instanceof Rule_Root ||
    			selectedRuleObject instanceof Rule_Group ) {
    			
    			return (IRule_Node)selectedRuleObject;
    		}
    		else {
    			
    			assert	(selectedItem.getParent().getValue() instanceof Rule_Root) ||
    					(selectedItem.getParent().getValue() instanceof Rule_Group): 
    					"Exception: The parent of rule isn't Rule_Group class instance";
    			return (IRule_Node)selectedItem.getParent().getValue();
    		}
    	}
    	else {
    		return (IRule_Node)rulesTree.getRoot().getValue();
    	}
    }

    private TreeItem<Rule_> FindTreeItemByRule(TreeItem<Rule_> rootTreeItem, Rule_ rule) {
    	
    	if(rootTreeItem != null) {
    		if(rootTreeItem.getValue() == rule)
    			return rootTreeItem;
    		
    		for(TreeItem<Rule_> childTreeItem : rootTreeItem.getChildren()) {
    			TreeItem<Rule_> treeItem = FindTreeItemByRule(childTreeItem, rule);
    			
    			if(treeItem != null)
    				return treeItem;
    		}
    	}
    	
    	return null;
    }
    
    private void ScrollRulesTreeToSelected () {
    //private void AddNew (Rule_ newRule, IRule_Node parent) {
    	//parent.getChildren().add(newRule);
    	
		int selectedIndex = rulesTree.getSelectionModel().getSelectedIndex();
		if (selectedIndex > 0) {
			rulesTree.getFocusModel().focus(selectedIndex);
	    	rulesTree.requestFocus();
			rulesTree.scrollTo(selectedIndex);
		}
    }
    
    public void CreateNewRule(String ruleName) {
    	
    	Rule_Replace newRule = App.data().new_Rule_Replace(ruleName, GetSelectedNode());
    	rulesTree.getSelectionModel().select( FindTreeItemByRule(rulesTree.getRoot() ,newRule) );
    	// Scroll and focus to selected TreeItem
    	ScrollRulesTreeToSelected();
    }

    public void DuplicateRule(String ruleName, Rule_Replace src) {
    	
    	App.data().duplicate_Rule_Replace(ruleName, GetSelectedNode(), src);
    	// Previously added item has been selected. Scroll and focus to it.
    	ScrollRulesTreeToSelected();
    }

    
    /* hasn't been used yet */
    private void CreateNewGroup(String groupName) {

    	App.data().new_Rule_Group(groupName, GetSelectedNode());
    	
    	// Previously added item has been selected. Scroll and focus to it.
    	ScrollRulesTreeToSelected();
    } 
    
    /*
    @FXML
    private void onButton_Remove() {
    	int selectedIndex = rulesTree.getSelectionModel().getSelectedIndex();
    	
    	if(selectedIndex >= 0) {
    		TreeItem<Rule_> selectedItem = rulesTree.getTreeItem(selectedIndex);
    		
    		if(selectedItem != null) {
	    		Rule_ selectedRule = selectedItem.getValue();
	    		
	    		if(selectedItem != null && selectedItem != rulesTree.getRoot()) {
	    			//IRule_Node node = (IRule_Node)selectedItem.getParent().getValue();
	    			//node.getChildren().remove(selectedRule);
	    			
	    			//rulesTree.getSelectionModel().select(null);
	    			//UnbindAll();
	    			App.data().removeRule(selectedRule);
	    			
	    			// Select none if can't get selected TreeItem (happens when root TreeItem is invisible)
	    			if( rulesTree.getTreeItem( rulesTree.getSelectionModel().getSelectedIndex() ) == null)
	    				rulesTree.getSelectionModel().select(null);
	    		}
    		}
    	}
    }
    */
    
    /**
     * Implementing recursive functionality for TreeItem
     *  
     */
    public class GroupTreeItem extends TreeItem<Rule_> {

        public GroupTreeItem(final Rule_Root node){
        	super((Rule_)node, null);
            
        	if(node != null) {
                addChildrenListener( (ObservableList<Rule_>)node.getChildren() );
            }
        }        
        
        public GroupTreeItem(final Rule_Group node){
            this(node, (Node) null);
        }

        public GroupTreeItem(final Rule_Group node, Node graphic) {
            super((Rule_)node, graphic);

            if(node != null) {
            	AddListener_OnNameChange( node.nameProperty() );
                addChildrenListener( (ObservableList<Rule_>)node.getChildren() );
            }
        }
        
        private TreeItem<Rule_> CreateRuleTreeItem (Rule_ ruleObj)
	    {
	    	TreeItem<Rule_> newTreeItem = null;
	    	
	    	if(ruleObj instanceof Rule_Group) {
	    		
    			Rule_Group childGroup = (Rule_Group)ruleObj;
    			newTreeItem = new GroupTreeItem(childGroup);
    		}
    		else {
    			
    			assert ruleObj instanceof Rule_Replace : "Exception: unknown type of Rule_ instance.";
    		
    			Rule_Replace childReplace = (Rule_Replace)ruleObj;
    			CheckBoxTreeItem<Rule_> cbTreeItem  = new CheckBoxTreeItem<>(ruleObj);
    			
    			// Binding checkbox to model
    			cbTreeItem.selectedProperty().bindBidirectional(((Rule_Replace)ruleObj).enabledProperty());
    			
    			AddListener_OnNameChange(childReplace.nameProperty());
    			newTreeItem = cbTreeItem;
    		}
	    	
	    	return newTreeItem;
	    }
        
        private void addChildrenListener(ObservableList<Rule_> list) {
        	
        	// Initialization model to TreeView items
        	list.forEach(child -> {
        		this.getChildren().add( CreateRuleTreeItem(child) );
        	});
        		
        	// Listener: update TreeView on model changes
        	list.addListener((ListChangeListener<? super Rule_>) change -> {

				while (change.next()) {

					if(change.wasAdded()) {
						change.getAddedSubList().forEach(t -> {
							
							TreeItem<Rule_> newTreeItem = CreateRuleTreeItem(t);
							GroupTreeItem.this.getChildren().add(newTreeItem);
						});
					}

					if(change.wasRemoved()) {
						change.getRemoved().forEach(t -> {
							
							final List<TreeItem<Rule_>> itemsToRemove = GroupTreeItem.this.getChildren()
								.stream()
								.filter(treeItem ->
									treeItem.getValue().equals(t)).collect(Collectors.toList());

							GroupTreeItem.this.getChildren().removeAll(itemsToRemove);
								
							// If the last item has been deleted only root item stays.
							// In this case rulesTree doesn't include itemsToRemove anymore
							// but rulesTree.getSelectionModel().getSelectedItem() still points
							// to deleted item (JavaFX bug?). So, we have to reset selection manually:
							if(rulesTree.getRoot().getChildren().size() == 0)
								rulesTree.getSelectionModel().select(null);

						});
					}
					
					if(change.wasReplaced()) {
					
					}
					
					if(change.wasPermutated()) {
						
					}
				}
        	});
        }
        
        /**
         * Workaround function: updates TreeItem label on Rule_::Name property changes
         */
        private void AddListener_OnNameChange (StringProperty treeItemNameProperty) {

        	treeItemNameProperty.addListener( (o, oldVal, newVal) -> {
        	//  .addListener(new ChangeListener(){ @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
        	
        		TreeItem<Rule_> selectedItem = (TreeItem <Rule_>)rulesTree.getSelectionModel().getSelectedItem();
	        	
	        	Rule_ value = selectedItem.getValue();
	        	
	        	if(( (application.model.Rule_)value ).nameProperty() == o) {
	        		selectedItem.setValue(null);
	        		selectedItem.setValue(value);
	        		
	        		App.log().Message("TreeItem name changed.");
	        	}
        	});
        }
        
	}        
    
    public void Unbind () {
    	ruleGroupController.Unbind();
    	ruleReplaceController.Unbind();
    }
    
}
