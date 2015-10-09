package application.controller.util;

import application.App;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Dialog_NewRule extends Dialog<String> {
	
    public Dialog_NewRule () {
    	setTitle("Create new Rule");
    	setHeaderText("Please, specify name for new Rule");
    	getDialogPane().getStyleClass().add("text-input-dialog");
    	getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(30, 20, 10, 20));

    	TextField rulename = new TextField();
    	//rulename.setPromptText("unique rule name");
    	
    	Label namewarning = new Label("Name already in use.");
    	namewarning.setTextFill(Color.web("red"));
    	namewarning.setVisible(false);
    	
    	grid.add(new Label("Rule name:"), 0, 0);
    	grid.add(rulename, 1, 0);
    	grid.add(namewarning, 1, 1);
    	
    	getDialogPane().setContent(grid);
    	
    	Node OkButton = getDialogPane().lookupButton(ButtonType.OK);
    	OkButton.setDisable(true);
    	
    	// Do rule's name validation
    	rulename.textProperty().addListener((observable, oldValue, newValue) -> {
    		boolean bNameInUse = (App.data().FindRuleRecursive(App.data().ruleData(), newValue) != null);
    		if(bNameInUse)
    			namewarning.setVisible(true);
    		else
    			namewarning.setVisible(false);
    		
    		OkButton.setDisable(newValue.trim().isEmpty() || bNameInUse);
    	});
    	
    	// Request focus on the rulename field by default.
    	Platform.runLater(() -> rulename.requestFocus());
    	
    	// Convert the result.
    	setResultConverter(dialogButton -> {
    	    if (dialogButton == ButtonType.OK) {
    	        return new String(rulename.getText());
    	    }
    	    return null;
    	});
    }
    
}
