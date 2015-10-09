package application.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Toggle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.App;
import application.controller.util.ChangeNotifier.event;
import application.model.Source_;
import application.model.Source_File;
import application.model.Variable;
import application.model.Rule_;
import application.model.Rule_Group;
import application.processor.Processor;
import application.processor.TextBlock_;
import application.processor.TextBlock_Replace;
import application.util.PathTool;

public class Controller_Root extends Controller_ {
    
    private VBox sourcesLayout;
    private Controller_Main mainController;
    
    //@FXML private BorderPane rootLayout;
    @FXML private AnchorPane childLayout;
    @FXML private ToggleGroup headerToggleGroup;
    @FXML private MenuItem saveMenuItem;
    
    public Controller_Root() {
    }
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);
    	
        initChildLayouts();
        //rootLayout.setCenter(this.sourcesLayout);
        SwitchChildLayout(childLayout, sourcesLayout);
    }

    private void initChildLayouts() {
    	
        try
        {
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_Main.fxml"));
	        this.sourcesLayout = (VBox) loader.load();
	        this.mainController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean SaveDataDialog () {
    	
    	if(App.data().getModifiedFlag()) {
	    	Alert alert = new Alert(AlertType.CONFIRMATION);
	    	alert.setTitle(App.GetName());
	    	alert.setHeaderText("Want to save your changes?");
	    	//alert.setContentText("Choose your option.");
	
	    	ButtonType buttonSave = new ButtonType("Save");
	    	ButtonType buttonDontSave = new ButtonType("Don't Save");
	    	ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	
	    	alert.getButtonTypes().setAll(buttonSave, buttonDontSave, buttonCancel);
	
	    	Optional<ButtonType> result = alert.showAndWait();
	    	if (result.get() == buttonSave){
	    		if(App.data().getProjectPath().length() > 0)
	    			return App.data().saveData();
	    		else
	    			return SaveAsDialog();
	    			
	    	} else if (result.get() == buttonDontSave) {
	    	    return true;
	    	} else {
	    	    return false;
	    	}
    	}
    	
    	return true;
    }
    
    private boolean SaveAsDialog () {
    	
    	FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(App.getPrimaryStage());

        if(file != null) {
        	return SaveAs(file);
        }
        
        return false;
    }
    
    private boolean SaveAs (File file) {
    	
    	if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            
            if(App.data().saveDataToFile(file)) {
            	App.log().Message("Project file was saved: " + App.data().getProjectPath());
            	return true;
            }
        }
        
    	App.log().Error("Can't save project to file: " + App.data().getProjectPath());
        return false;
    }
    //*********************************************************************************************
    // @FXML Event handling
    //*********************************************************************************************

    @FXML
    private void OnMenu_SaveAs () {
    	if(SaveAsDialog())
    		saveMenuItem.setDisable(false);
    }
    
    @FXML
    private void OnMenu_Save () {
    	SaveAs(new File(App.data().getProjectPath()));
    }

    @FXML
    private boolean OnMenu_Open () {
    	
    	if (!App.data().getModifiedFlag() || SaveDataDialog()) {
    	
	        FileChooser fileChooser = new FileChooser();
	
	        // Set extension filter
	        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
	                "XML files (*.xml)", "*.xml");
	        fileChooser.getExtensionFilters().add(extFilter);
	
	        // Show save file dialog
	        File file = fileChooser.showOpenDialog(App.getPrimaryStage());
	
	        if (file != null) {
	        	if(App.data().loadDataFromFile(file)) {
	        		mainController.Select_Null();
		        	saveMenuItem.setDisable(false);
		        	App.data().setModifiedFlag(false);
		        	App.log().Message("Loaded project: " + file.getPath());
		        	return true;
	        	}
	        	else {
	        		App.log().Error("Can't load project: " + file.getPath());
	        		return false;
	        	}
	        }
	        
	        return false;
    	}
    	
    	return true;
    }
    
    @FXML
    private void OnMenu_New () {
    	if (!App.data().getModifiedFlag() || SaveDataDialog()) {
    		App.data().newData();
    		
    		mainController.Select_Null();
        	saveMenuItem.setDisable(true);
        	App.data().setModifiedFlag(false);
        	
        	App.log().Message("New project");
    	}
    }
    
    @FXML
    private void OnMenu_ReplaceAllTo () {
    	DirectoryChooser dirChooser = new DirectoryChooser();
    	dirChooser.titleProperty().set("Choose output directory...");
        File selectedDirectory = dirChooser.showDialog(App.getPrimaryStage());
         
        if(selectedDirectory != null) {
        	
        	App.setCursor_Wait();
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			    	try {
			        	for(Source_ src : App.data().srcData()) {
			        		if(src instanceof Source_File)
			        			Processor.SaveFile((Source_File)src, selectedDirectory.getAbsolutePath());
			        	}
					} finally {
			    		// Anyway (even if exception was happened) we should return cursor to default state
			    		App.setCursor_Default();
			    	}
			    }
			});
        	
        }
    }
    
    @FXML
    private void OnMenu_SearchAll () {
    	App.setCursor_Wait();
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	try {

	    	    	for(Source_ src : App.data().srcData()) {
			    		if(src instanceof Source_File) {
			    			App.notifier().notifyControllers(Controller_Root.this, event.EXPAND_PANE_PREIVEW);
			    			Processor.GenerateStatus(src.rdata());
			    		}
			    	}	
		    		
				} finally {
		    		// Anyway (even if exception was happened) we should return cursor to default state
		    		App.setCursor_Default();
		    	}
		    }
		});
    }
    
    @FXML
    private void OnMenu_Exit () {
    	if (!App.data().getModifiedFlag() || SaveDataDialog()) {
	    	Platform.exit();
    	}
    }
    
    @FXML
    private void OnMenu_NewRule () {
    	mainController.OnButton_NewRule();
    }
    
    @FXML
    private void OnMenu_NewVariable () {
    	mainController.OnButton_NewVariable();
    }
    
    @FXML
    private void OnMenu_View_FileSystem () {
    	mainController.ExpandPane("File system");
    }
    
    @FXML
    private void OnMenu_View_Sources () {
    	mainController.ExpandPane("Sources");
    }
    
    @FXML
    private void OnMenu_View_Variables () {
    	mainController.ExpandPane("Variables");
    }
    
    @FXML
    private void OnMenu_View_Rules () {
    	mainController.ExpandPane("Rules");
    }
    
    @FXML
    private void OnMenu_View_Preview () {
    	mainController.ExpandPane("Preview");
    }
    
    @FXML
    private void OnMenu_Help_About () {
    	App.MessageBox(AlertType.INFORMATION, App.GetName(), "About information",
    			"Developed by Vadim Skyba for purposes of rebranding process automation.\n\nyear 2015.");
    }
}
