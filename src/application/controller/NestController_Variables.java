package application.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import application.App;
import application.model.Variable;

public class NestController_Variables {

	private TableView<Variable> variableTable;
	
    private Controller_Variable variableController;
    private VBox variableLayout;
	

    public NestController_Variables (TableView<Variable> variableTable) {
		this.variableTable = variableTable;
		
    	try {
	    	FXMLLoader loader;
	    	
	        loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_Variable.fxml"));
	        this.variableLayout = (VBox) loader.load();
	        this.variableController = loader.getController();
	        
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public Node Bind (Variable variable) {
		Unbind();
		
		if(variable != null) {
			variableController.Bind(variable);
    		return variableLayout;
		}
		
		return null;
	}

    protected void Unbind () {
    	variableController.Unbind();
    } 
}
