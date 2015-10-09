package application.controller.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import application.App;

public class Dialog_NewVariable extends Dialog<String> {

	public Dialog_NewVariable () {
		
    	setTitle("Create new Variable");
    	setHeaderText("Please, specify name for new Variable");
    	getDialogPane().getStyleClass().add("text-input-dialog");
    	getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(30, 20, 10, 20));

    	TextField varname = new TextField();
    	//varname.setPromptText("unique rule name");
    	
    	Label namewarning = new Label("Name already in use.");
    	namewarning.setTextFill(Color.web("red"));
    	namewarning.setVisible(false);
    	
    	grid.add(new Label("Variable name:"), 0, 0);
    	grid.add(varname, 1, 0);
    	grid.add(namewarning, 1, 1);
    	
    	getDialogPane().setContent(grid);
    	
    	Node OkButton = getDialogPane().lookupButton(ButtonType.OK);
    	OkButton.setDisable(true);
    	
    	// Do variable's name validation
    	varname.textProperty().addListener((observable, oldValue, newValue) -> {
    		boolean bNameInUse = (App.data().GetVariable(newValue) != null);
    		if(bNameInUse)
    			namewarning.setVisible(true);
    		else
    			namewarning.setVisible(false);
    		
    		OkButton.setDisable(newValue.trim().isEmpty() || bNameInUse);
    	});
    	
    	// Request focus on the varename field by default.
    	Platform.runLater(() -> varname.requestFocus());
    	
    	// Convert the result.
    	setResultConverter(dialogButton -> {
    	    if (dialogButton == ButtonType.OK) {
    	        return new String(varname.getText());
    	    }
    	    return null;
    	});
	}
}
