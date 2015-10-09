package application.controller;

import java.io.File;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import application.App;
import application.controller.util.BrowserItem;
import application.model.Source_;

public class NestController_Browser {

    private TreeView <BrowserItem> browserTree;
    
    private Controller_Source_BrowserItem browserItemController;
    private VBox browserItemLayout;
    
    
    public NestController_Browser (TreeView <BrowserItem> browserTree) {
    	
    	this.browserTree = browserTree;
    	
    	try {
    		
	    	FXMLLoader loader;
	    	
	        loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_Source_BrowserItem.fxml"));
	        this.browserItemLayout = (VBox) loader.load();
	        this.browserItemController = loader.getController();
	    	
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    }
    
	public Node Bind (BrowserItem browserItem) {
		Unbind();
		
		if(browserItem != null) {
			assert browserItem.getSrcObj() == null;
    		browserItemController.Bind(browserItem);
    		return browserItemLayout;
		}
		
		return null;
	}

    protected void Unbind () {
    	browserItemController.Unbind();
    } 

    
}
