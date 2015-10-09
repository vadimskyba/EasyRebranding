package application.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import application.App;
import application.model.Source_;
import application.model.Source_File;
import application.model.Source_Folder;

public class NestController_Preview {

    private TableView<Source_> sourcesTable;
    private TableColumn<Source_, String> srcNameColumn;
    private TableColumn<Source_, String> srcStatusColumn;
    
    private Controller_Preview previewController;
    private VBox previewLayout;
    
    public NestController_Preview (TableView<Source_> sourcesTable) {
    	
    	this.sourcesTable = sourcesTable;
    	
    	try {
    		
	    	FXMLLoader loader;

	        loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_Preview.fxml"));
	        this.previewLayout = (VBox) loader.load();
	        this.previewController = loader.getController();
	        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Node Bind (Source_ srcObj) {
    	
    	Unbind();
    	
    	if(srcObj != null && srcObj instanceof Source_File) {
    		previewController.Bind((Source_File)srcObj);
    		return previewLayout;
    	}
    	else {
    		return null;
    	}
    }
    
    public void Unbind () {
    	previewController.Unbind();
    }
}
