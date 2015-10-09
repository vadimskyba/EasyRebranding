package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import application.controller.Controller_Root;
import application.controller.util.ChangeNotifier;
import application.controller.util.ControllerRegister;
import application.util.Logger;
import application.Data;


public class App extends Application {

	private TextFlow consoleFlow = null;
	
	private static App app;
	private static Logger console;
	
    private Stage primaryStage;
    private Controller_Root mainController;
    private final Data mainData = new Data ();
    private final ChangeNotifier changeNotifier = new ChangeNotifier ();
    private final ControllerRegister controllerRegistrator = new ControllerRegister(); 
    
    @Override
    public void start(Stage primaryStage) {
    	
    	app = this;
    	console = new Logger ();
    	
    	setUserAgentStylesheet(STYLESHEET_MODENA);
    	
        this.primaryStage = primaryStage;
        app.setTitle("", false);
        
        initRootLayout();
        
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("view/Layout_Root.fxml"));
            Parent rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            //scene.getStylesheets().add("application/view/styles.css"); - added in each fxml to reflect styles in SceneBuilder.
            primaryStage.setScene(scene);
            
            // Give the controller access to the main app.
            mainController = loader.getController();
            primaryStage.show();
            
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent event) {
                    if (!mainController.SaveDataDialog()) {
                    	event.consume();
                    }
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void RegisterConsoleTextFlow (TextFlow textFlow) {
    	app.consoleFlow = textFlow;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public static Stage getPrimaryStage() {
        return app.primaryStage;
    }

    public static Controller_Root controller () {
    	return app.mainController;
    }
    
    public static Data data () {
    	return app.mainData;
    }
    
    public static ChangeNotifier notifier () {
    	return app.changeNotifier;
    }
    
    public static ControllerRegister controllers () {
    	return app.controllerRegistrator;
    }
    
    public static Logger log() {
    	return app.console;
    }
    
    public static void setTitle (final String projectName, boolean bModifiedFlag) {
    	app.primaryStage.setTitle(	((bModifiedFlag == true) ? "*" : "") +
    								(projectName == null || projectName.length() == 0 ? "Untitled" : projectName ) +
    								" - " + app.GetName());
    }
    
    public static String GetName () {
    	return "EasyRebranding";
    }
    
    public static void MessageBox (Alert.AlertType type, String title, String header, String content) {
    	
    	// Show Messagebox
        // http://code.makery.ch/blog/javafx-dialogs-official/
    	
    	Alert alert = new Alert(type);
    	alert.setTitle(title);
    	alert.setHeaderText(header);
    	alert.setContentText(content);
    	alert.showAndWait();
    }

    public static void ExceptionBox (IOException ioe) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	ioe.printStackTrace(pw);
    	String traceStr = sw.toString(); // stack trace as a string
    	
    	app.console.Error(traceStr);
    	/*
    	Dialogs dialog = new Dialogs();
    	
    	Dialog.create()
        .owner(stage)
        .title("Exception Dialog")
        .masthead("Look, an Exception Dialog")
        .message("Ooops, there was an exception!")
        .showException(new FileNotFoundException("Could not find file blabla.txt"));
        */
    }

	//**************************************************** Auxiliary
	
	public static void setCursor_Wait () {
		App.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
	}
	
	public static void setCursor_Default () {
		App.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
	}
	
}
