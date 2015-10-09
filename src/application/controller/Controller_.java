package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.util.Callback;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


import application.App;
import application.controller.util.ChangeNotifier;
import application.controller.util.ChangeNotifier.event;

public abstract class Controller_ implements Initializable {

	public Controller_() {
	}    
    
	/**
     * This method is called by the FXMLLoader when initialization is complete
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	
    	// Add listening of Unbind event for all Controller_ instances by default.
    	App.notifier().addListener(this, event.CMD_UNBIND_ALL);
    }
	
    public void onNotify (event event, Object... p) {
    	if(	event == event.CMD_UNBIND_ALL ) {
    		Unbind();
    	}
    }
    
    protected void Unbind () {}
    
    protected static void SwitchChildLayout(AnchorPane nodeParent, Node nodeChild) {
    	nodeParent.getChildren().clear();
    	nodeParent.getChildren().add(nodeChild);
    	
    	// Fit child layout to parent size
    	nodeParent.setTopAnchor(nodeChild, 0.0);
    	nodeParent.setRightAnchor(nodeChild, 0.0);
    	nodeParent.setLeftAnchor(nodeChild, 0.0);
    	nodeParent.setBottomAnchor(nodeChild, 0.0);
    }
    
    /**
     * Commit edition on ENTER key or after focus has been lost
     */
    protected static void addListener_OnEditCommit(Node node, Callback<Node, Boolean> callbackFunction) {

    	node.setOnKeyReleased(new EventHandler<KeyEvent>() {
    	    public void handle(KeyEvent t) {
    	        if (t.getCode() == KeyCode.ENTER) {
    	        	
    	        	callbackFunction.call(node);
    	        	
    	        	// Reflecting commit by selecting all text in string 
    	        	if (node instanceof TextField)
    	        		((TextField)node).selectAll();
    	        }
    	    }
    	});    	
    	
    	node.focusedProperty().addListener(new ChangeListener<Boolean>() {
    		boolean inProcessing = false;
    	    @Override
    	    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
    	    	
    	    	if(newPropertyValue == false && !inProcessing) {
    	    		inProcessing = true;
    	    		callbackFunction.call(node);
    	    		inProcessing = false;
    	    	}
    	    }
    	});
    }
    
}





