package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import netscape.javascript.JSObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import application.App;
import application.model.Source_File;
import application.processor.Action_Replace;
import application.processor.Match;
import application.processor.Processor;
import application.processor.TextBlock_;
import application.processor.TextBlock_Replace;
import application.util.PathTool;
import application.view.controls.TableViewEx;

public class Controller_Preview extends Controller_ {

    @FXML private WebView webView;
    private WebEngine webEngine;
    private JSBridge jsBridge;
    private TextBlock_ textBlockList;
    
    @FXML private TableViewEx<TextBlock_Replace> matchesTable;
    @FXML private TableColumn<TextBlock_Replace, String> ruleNameColumn;
    @FXML private TableColumn<TextBlock_Replace, String> foundStrColumn;
    @FXML private TableColumn<TextBlock_Replace, String> replacementStrColumn;
    private final ObservableList<TextBlock_Replace> replacesList = FXCollections.observableArrayList();
    
    @FXML private Label selectedFileLabel;    
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	super.initialize(fxmlFileLocation, resources);
    	
    	ruleNameColumn.setCellValueFactory(cellData -> cellData.getValue().getMatch().getOwnerAction().getOwnerRule_Replace().nameProperty());
    	foundStrColumn.setCellValueFactory(cellData -> cellData.getValue().getMatch().foundStrProperty());
    	replacementStrColumn.setCellValueFactory(cellData -> cellData.getValue().getMatch().replaceStrProperty());
    	matchesTable.setItems(replacesList);
    	matchesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			
    		if(!this.matchesTable.isIgnoreSelChanges()) {
				if(newValue != null)
					jsBridge.Select(newValue.hashCode());
	        	else {
	        		jsBridge.Select(0);
	        	}
    		}
		});
    	
    	try {
    		webView.setContextMenuEnabled(false);
    		webEngine = webView.getEngine();
    		webEngine.setJavaScriptEnabled(true);
    		
    		String URL = this.getClass().getResource("/application/view/LayoutMain_Results_WebView.html").toExternalForm();
    		
    		webEngine.load(this.getClass().getResource("/application/view/LayoutMain_Results_WebView.html").toExternalForm());

    		// Register callback function
            JSObject jsobj = (JSObject) webEngine.executeScript("window");
            jsBridge = new JSBridge(jsobj);
            jsobj.setMember("java", jsBridge);
            
        } catch (Exception ex) {
        	App.log().Error(ex.getMessage());
            ex.printStackTrace();
        }
    	
    }
    
	public void Bind (Source_File src) {
		Unbind();
		
		if(src != null) {
			selectedFileLabel.setText("Selected file: " + PathTool.GetFullPath(App.data().getBaseFolder(), src.getFullRelativePath()));
			
			App.setCursor_Wait();
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			    	try {
						jsBridge.StartModify();
			    		textBlockList = Processor.GenerateReplacedContent(src.rdata());
						
						if(textBlockList != null) {
							TextBlock_ textBlock = textBlockList;
							
							// Filling WebView with textBlockList data
							while (textBlock != null) {
								jsBridge.AddHTMLBlock(textBlock.getHTMLBlock());
								jsBridge.SetTextContent(textBlock.hashCode(), textBlock.getHtmlOutputContent(src.rdata().GET_WorkCharset().name()));
								
								if(textBlock instanceof TextBlock_Replace)
									replacesList.add((TextBlock_Replace)textBlock);
								
								textBlock = textBlock.next();
							}
						}	
			    	}
			    	finally {
			    		jsBridge.FinishModify();
			    		
			    		// Anyway (even if exception was happened) we should return cursor to default state
			    		App.setCursor_Default();
			    	}
			    }
			});
		}
	}
    
	@Override
    protected void Unbind () {
		textBlockList = null;
		
		replacesList.clear();
		jsBridge.StartModify();
		jsBridge.FinishModify();
		selectedFileLabel.setText("Selected file: file not selected");
    }
    
	/*
	 * Java <-> JavaScript bridge
	 * 
	 */
	public class JSBridge {
		
		JSObject jsObj;
		
		public JSBridge(JSObject jsObj) {
			this.jsObj = jsObj;
		}
		
		public void OnSelect_TextBlock_Replace(int hashCode) {
			
			TextBlock_ textBlock = textBlockList;
			while (textBlock != null) {
				if(textBlock instanceof TextBlock_Replace && ((TextBlock_Replace)textBlock).hashCode() == hashCode) {
					matchesTable.setIgnoreSelChanges(true);
					matchesTable.getSelectionModel().select(((TextBlock_Replace)textBlock));
					matchesTable.setIgnoreSelChanges(false);
					break;
				}
				textBlock = textBlock.next();
			}
			
		}

		public void AddHTMLBlock (String strContent) {
			jsObj.call("AddHTMLBlock", strContent);
		}
		
		public void SetTextContent (int id, String strContent) {
			jsObj.call("SetTextContent", "" + id, strContent);
			// can be processed with next block of statements as well:
			//import org.w3c.dom.Element;
			//Element el = webEngine.getDocument().getElementById(id);
			//el.setTextContent(strContent);
		}
		
		public void StartModify () {
			jsObj.call("StartModify");
		}
		
		public void FinishModify () {
			jsObj.call("FinishModify");
		}
		
		public void Select (int id) {
			jsObj.call("Select", "" + id);
		}
	}
}
