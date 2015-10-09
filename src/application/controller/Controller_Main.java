package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.List;
import java.io.File;

import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.event.Event;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextFlow;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import application.App;
import application.controller.Controller_Source_File.TreeItem_Rule_Group;
import application.controller.util.BrowserItem;
import application.controller.util.BrowserTreeItem;
import application.controller.util.ChangeNotifier.event;
import application.controller.util.Dialog_NewRule;
import application.controller.util.Dialog_NewVariable;
import application.model.IRule_Node;
import application.model.Rule_;
import application.model.Rule_Group;
import application.model.Rule_Replace;
import application.model.Rule_Root;
import application.model.Source_;
import application.model.Source_Folder;
import application.model.Source_File;
import application.model.Variable;
import application.processor.Processor;
import application.processor.TextBlock_;
import application.util.PathTool;
import application.view.Constants;

public class Controller_Main extends Controller_ {

	@FXML private AnchorPane selectedLayout;
    private FlowPane noselectLayout;
	
    @FXML private TitledPane consolePane;
    @FXML private TextFlow consoleFlow;
    
    @FXML private Accordion accordion;
    @FXML private Label BaseFolderLabel;
   
	//****************************************************************************** browserPane
    @FXML private TitledPane browserPane; 			
    @FXML private TreeView <BrowserItem> browserTree;
    NestController_Browser browserController;
    
	//****************************************************************************** sourcesPane    
    @FXML private TitledPane sourcesPane; 
    @FXML private TableView<Source_> sourcesTable;
    @FXML private TableColumn<Source_, String> srcNameColumn;
    @FXML private TableColumn<Source_, String> srcPathColumn;    
    NestController_Sources sourcesController;
    
	//****************************************************************************** rulesPane    
    @FXML private TitledPane rulesPane;
    @FXML private TreeView<Rule_> rulesTree;
    
    private NestController_Rules rulesController;
    
	//****************************************************************************** variablesPane
    @FXML private TitledPane variablesPane;
	@FXML private TableView<Variable> variableTable;
    @FXML private TableColumn<Variable, String> nameColumn;
    NestController_Variables variablesController; 
    
	//****************************************************************************** previewPane
    @FXML private TitledPane previewPane;

    @FXML private TableView<Source_> sourcesPreviewTable;
    @FXML private TableColumn<Source_, String> srcPreviewPathColumn;
    @FXML private TableColumn<Source_, String> srcPreviewNameColumn;
    @FXML private TableColumn<Source_, String> srcPreviewCharsetColumn;
    @FXML private TableColumn<Source_, String> srcPreviewStatusColumn;
    NestController_Preview previewController;
    
    //**************************************************************************************************
    
	public Controller_Main() {
	}
	
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);

    	App.log().setOutputTextFlow(consoleFlow);
    	
    	try {
    		FXMLLoader loader;

	    	loader = new FXMLLoader();
	        loader.setLocation(App.class.getResource("view/Layout_NoSelect.fxml"));
	        this.noselectLayout = (FlowPane) loader.load();
	        
        } catch (IOException e) {
            e.printStackTrace();
        }
    	SwitchLayout(null);
    	
        browserController = new NestController_Browser (browserTree);
        sourcesController = new NestController_Sources(sourcesTable);	        
        rulesController = new NestController_Rules (rulesTree);
        variablesController = new NestController_Variables(variableTable);
        previewController = new NestController_Preview(sourcesPreviewTable);
        
    	accordion.setExpandedPane(sourcesPane);
    	accordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
			@Override
			public void changed(ObservableValue<? extends TitledPane> ov, TitledPane old_val, TitledPane new_val) {
				if (new_val != null) {
					if(accordion.getExpandedPane() == browserPane)
						Select_BrowserTreeItem((BrowserTreeItem)browserTree.getSelectionModel().getSelectedItem());
						
					else if(accordion.getExpandedPane() == sourcesPane)
						Select_Source(sourcesTable.getSelectionModel().getSelectedItem());
						
					else if(accordion.getExpandedPane() == rulesPane)
						Select_RuleTreeItem ( rulesTree.getSelectionModel().getSelectedItem() );
					
					else if(accordion.getExpandedPane() == variablesPane)
						Select_Variable(variableTable.getSelectionModel().getSelectedItem());
						
					else if(accordion.getExpandedPane() == previewPane)
						Select_Preview(sourcesPreviewTable.getSelectionModel().getSelectedItem());
				}
			}
		});
    	
		// baseFolder change listener
		App.data().baseFolderProperty().addListener((observable, oldValue, newValue) -> {
			
			if(newValue.length() > 0) {
				BaseFolderLabel.textProperty().set("Base folder: " + newValue);
				BrowserItem sRT = new BrowserItem (new File(newValue), null);
				TreeItem<BrowserItem> item = new BrowserTreeItem (sRT);
	        	browserTree.setRoot(item);
			}
			else {
				BaseFolderLabel.textProperty().set(Constants.STR_EMPTY_BASEFOLDER);
				browserTree.setRoot(null);
			}
   	

		});
		
    	Initialize_Browser();
    	Initialize_Sources();
    	Initialize_Rules();
    	Initialize_Variables();
    	Initialize_Preview();
    	
    	App.notifier().addListener(this, event.UPD_SRC_SELECTION);
    	App.notifier().addListener(this, event.EXPAND_PANE_PREIVEW);
    }
    
    private void Initialize_Browser () {
    	
        // Handle TreeView selection changes
        browserTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        	Select_BrowserTreeItem((BrowserTreeItem)newValue);
        });
        
        App.data().srcData().addListener((ListChangeListener.Change<? extends Source_> change) -> {
        	
        	while (change.next()) {
        		
        		if(change.wasRemoved()) {
					change.getRemoved().forEach(t -> {
	
						BrowserTreeItem rootItem = (BrowserTreeItem)browserTree.getRoot();
						if(rootItem != null) {
							BrowserItem item = rootItem.findTreeItem(t);
							// !!! WARNING! On second time file open findTreeItem(..) return null!
							if(item != null)
								item.setSrcObj(null);
						}
					});
					
					RefreshView();
				}
        	}
        });
        
    	browserTree.setCellFactory(p -> new CheckBoxTreeCell <BrowserItem>() {
    	     @Override
    	     public void updateItem(BrowserItem item, boolean empty) {
    	        super.updateItem(item, empty);

    	        if (empty) {
    	        	setText(null);
    	        	setGraphic(null);
    	        }
    	        else {
    	        	
    	        	if(item == null)
    	        		return;
    	        	
    	        	TreeItem<BrowserItem> treeItem = getTreeItem();
    	        	if (treeItem instanceof CheckBoxTreeItem) {
    	        		CheckBoxTreeItem<BrowserItem> cbTreeItem = (CheckBoxTreeItem<BrowserItem>) treeItem;
    	        		//setText(item.toString()); - unnecessary because Source::toString() was implemented
    	        		CheckBox cb = new CheckBox();
    	        		cb.indeterminateProperty().bindBidirectional(cbTreeItem.indeterminateProperty());
    	        		cb.selectedProperty().bindBidirectional(cbTreeItem.selectedProperty());
    	        		setGraphic(cb);
    	        	}
    	        	else {
    	        		//setText(item.toString()); - unnecessary because Source::toString() was implemented
    	        		
    	        		if(item.getSrcObj() != null)
    	        			setId("assigned_source");
    	        		else
    	        			setId(null);
    	        		
    	        		this.
    	        		setGraphic(null);
    	        	}
    	        }
     	     }

	    });
    }
    
    private void Initialize_Sources () {
    	
        // Initialize the variable table with the two columns and specify list with data
		srcNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		srcPathColumn.setCellValueFactory(cellData -> cellData.getValue().relativePathProperty());		
		sourcesTable.setItems(App.data().srcData());
    	
		sourcesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			Select_Source(newValue);
		});
    }
    
    private void Initialize_Rules () {
    	
       	// Handle TreeView <Rules_> selection changes
       	rulesTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
       		rulesTree.requestFocus(); // Fix: Take away focus from focused field to save property change before selecting another Rule_ item
       		Select_RuleTreeItem(newValue);
       	});

       	// Fixing case: when the last rule has been deleted rulesTree doesn't update selection change
		App.data().ruleData().addListener((ListChangeListener<? super Rule_>) change -> {
			if(App.data().ruleData().size() == 0 && accordion.getExpandedPane() == rulesPane) 
				Select_RuleTreeItem(null);
    	});
		
    }
    
    private void Initialize_Variables () {
    	
        // Initialize the variable table with one column.
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        variableTable.setItems(App.data().varData());
        
        // Select first row by default
    	variableTable.getSelectionModel().select(0);
    	variableTable.getFocusModel().focus(0);
    	// variableTable.requestFocus();
    	
    	variableTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
    		variableTable.requestFocus(); // Fix: Take away focus from focused field to save property change before selecting another Variable_ item
			Select_Variable(newValue);
		});
    }
    
    private void Initialize_Preview () {
    	
        // Initialize the sources table with the two columns and specify list with data
    	srcPreviewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	srcPreviewPathColumn.setCellValueFactory(cellData -> cellData.getValue().relativePathProperty());
    	srcPreviewStatusColumn.setCellValueFactory(cellData -> cellData.getValue().rdata().statusProperty());
    	srcPreviewCharsetColumn.setCellValueFactory(cellData -> cellData.getValue().rdata().charsetProperty());
    	sourcesPreviewTable.setItems(App.data().srcData());
     	
    	sourcesPreviewTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
 			
 			if(newValue != null) {
 				SwitchLayout( previewController.Bind( newValue ));
 			}
         	else {
         		if (oldValue != null) {

         			
         		}
         	}
 		});
    }
    
    private void Select_BrowserTreeItem (BrowserTreeItem browserTreeItem) {
    	Unbind();
    	
    	if(browserTreeItem != null) {
    		
			BrowserItem browserItem = browserTreeItem.getValue();
			
			if(browserItem != null && browserItem.getSrcObj() != null)
				Select_Source( browserItem.getSrcObj() );
			else {
				SwitchLayout( browserController.Bind(browserItem) );
			}
		}
    	else
    		SwitchLayout(null);
    }
    
    private void Select_Source (Source_ source) {
    	Unbind();
    	SwitchLayout( sourcesController.Bind( source ));
    }
    
    private void Select_RuleTreeItem (TreeItem <Rule_> ruleTreeItem) {
    	Unbind();
    	
    	if(ruleTreeItem != null)
			SwitchLayout( rulesController.Bind( ruleTreeItem.getValue() ));
		else
			SwitchLayout(null);
    }
    
    private void Select_Variable (Variable variable) {
    	Unbind();
    	SwitchLayout( variablesController.Bind( variable ));
    }
    
    private void Select_Preview (Source_ srcObj) {
    	Unbind();
    	SwitchLayout( previewController.Bind( srcObj ));
    }
    
    public void Select_Null () {
    	Unbind();
    	SwitchChildLayout(selectedLayout, noselectLayout);
    }
    
    private void SwitchLayout (Node layout) {
    	
    	if(layout != null)
			SwitchChildLayout(selectedLayout, layout);
		else
			SwitchChildLayout(selectedLayout, noselectLayout);
    }	
    
    @Override
    protected void Unbind () {
    	browserController.Unbind();
    	sourcesController.Unbind();
    	rulesController.Unbind();
    	variablesController.Unbind();
    	previewController.Unbind();
    }
    
	@Override
    public void onNotify (event event, Object... p) {
		
    	if(	event == event.UPD_SRC_SELECTION ) {
    		RefreshView();
    	}
    	else if( event == event.EXPAND_PANE_PREIVEW ) {
    		accordion.setExpandedPane(previewPane);
    	}
    }
    
	/**
	 * Usually called by child controller to update view on selection change
	 * 
	 */
    private void RefreshView () {
    	
    	// Refresh childLayout by reselecting TreeItem
    	int selectedIndex = browserTree.getSelectionModel().getSelectedIndex();
    	browserTree.getSelectionModel().select(null);
    	browserTree.getSelectionModel().select(selectedIndex);
    	
    	// Refresh selected TreeItem by resetting it's value (no other way)
    	TreeItem <BrowserItem> selectedItem = browserTree.getSelectionModel().getSelectedItem();
    	if(selectedItem!= null)	{
	    	BrowserItem item = selectedItem.getValue();
	    	selectedItem.setValue(null);
	    	selectedItem.setValue(item);
    	}
    }
    
    public void ExpandPane (String strPane) {
    	
    	if(strPane.equalsIgnoreCase("File system"))
    		accordion.setExpandedPane(browserPane);
    	else if(strPane.equalsIgnoreCase("Sources"))
    		accordion.setExpandedPane(sourcesPane);
    	else if(strPane.equalsIgnoreCase("Variables"))
    		accordion.setExpandedPane(variablesPane);
    	else if(strPane.equalsIgnoreCase("Rules"))
    		accordion.setExpandedPane(rulesPane);
    	else if(strPane.equalsIgnoreCase("Preview"))
    		accordion.setExpandedPane(previewPane);
    	
    }
    
    //*********************************************************************************************
    // Event handling
    //*********************************************************************************************
    
    @FXML
    private void onButton_ChooseBaseFolder() {
    	DirectoryChooser dirChooser = new DirectoryChooser();
        File selectedDirectory = dirChooser.showDialog(App.getPrimaryStage());
         
        if(selectedDirectory != null) {
        	App.data().setBaseFolder( selectedDirectory.getAbsolutePath() );
        	accordion.setExpandedPane(browserPane);
        }
    }
    
    @FXML
    public void OnButton_NewRule () {
    	
    	Dialog_NewRule dialog = new Dialog_NewRule ();
    	Optional<String> result = dialog.showAndWait();
    	
    	if (result.isPresent()){
    		App.log().Message("New rule has been created: " + result.get());
    	    rulesController.CreateNewRule(result.get());
    	    accordion.setExpandedPane(rulesPane);
    	}
    }
    
    @FXML
    public void OnButton_NewVariable () {
    	Dialog_NewVariable dialog = new Dialog_NewVariable ();
    	Optional<String> result = dialog.showAndWait();
    	
    	if (result.isPresent()){
    		App.log().Message("New variable has been created: " + result.get());
    	    variableTable.getSelectionModel().select( App.data().new_Variable(result.get()) );
    	    //Select_Variable( App.data().new_Variable(result.get()) );
    	    accordion.setExpandedPane(variablesPane);
    	}
    }
    
}
