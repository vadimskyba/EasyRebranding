package application.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import application.App;
import application.model.Source_;
import application.model.Source_File;
import application.model.Source_Folder;

public class NestController_Sources {

    private TableView<Source_> sourcesTable;

    private Controller_Source_Folder folderController;
    private VBox folderLayout;
    
    private Controller_Source_File fileController;
    private VBox fileLayout;
    
    public NestController_Sources (TableView<Source_> sourcesTable) {
    	
    	this.sourcesTable = sourcesTable;
    	
    	try {
    		
	    	FXMLLoader loader;

	        loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_Source_Folder.fxml"));
	        this.folderLayout = (VBox) loader.load();
	        this.folderController = loader.getController();
	        
	        loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_Source_File.fxml"));
	        this.fileLayout = (VBox) loader.load();
	        this.fileController = loader.getController();
	        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Node Bind(Source_ srcObj) {
    	
    	Unbind();
    	
    	if(srcObj != null) {
    		
        	if(srcObj instanceof Source_Folder)
        	{
        		folderController.Bind((Source_Folder)srcObj);
        		return folderLayout;        		
        	}
        	else if(srcObj instanceof Source_File)
        	{
        		fileController.Bind((Source_File)srcObj);
        		return fileLayout;        		
        	}        	
        	else
        		return null;
    	}
    	else {
    		return null;
    	}
    }
    
    protected void Unbind () {
    	fileController.Unbind();
    	folderController.Unbind();
    }

}
