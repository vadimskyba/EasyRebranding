package application.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import application.App;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Logger {

	private String outFileName;
	TextFlow outFlow = null;
	
	public void Logger () {
	}
	
	public void setOutputFile (String fileName) {
		this.outFileName = fileName;
	}

	public void setOutputTextFlow (TextFlow textFlow) {
		this.outFlow = textFlow;
	}

	public void Message (String message) {
		System.out.println(message);
		outFlowMessage(message);
		outFileMessage(message);
	}

	public void Warning (String message) {
		System.out.println(message);
		outFlowWarning(message);
		outFileWarning(message);
	}
	
	public void Error (String message) {
		System.err.print(message);
		outFlowError(message);
		outFileError(message);
	}

	private void outFlowMessage (String message) {
		if(outFlow != null) {
			Text t = new Text(message + "\n");
    		t.setId("console_text");
    		outFlow.getChildren().add(0, t);
		}
	}

	private void outFlowWarning (String message) {
		if(outFlow != null) {
			Text t = new Text(message + "\n");
    		t.setId("console_text_warning");
    		outFlow.getChildren().add(0, t);
		}
	}
	
	private void outFlowError (String message) {
		if(outFlow != null) {
    		Text t = new Text("Error. " + message + "\n");
    		t.setId("console_text_error");
    		outFlow.getChildren().add(0, t);
		}
	}
	
	private void outFileWarning (String message) {
		outFileMessage("warning: " + message);
	}
	
	private void outFileError (String message) {
		outFileMessage("error: " + message);
	}
	
	private void outFileMessage (String message) {
		
		try {
			if(outFileName != null) {
				File f = new File(outFileName);
				if(!f.exists()) {
				    f.createNewFile();
				} 
				
				FileOutputStream fos = new FileOutputStream(f);
				 
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			 
				for (int i = 0; i < 10; i++) {
					bw.write(message);
					bw.newLine();
				}
			 
				bw.close();
			}
	    }
		catch (IOException ioe) {
			
			String exceptionMsg = "Can't open or create log file: " + outFileName;
			System.out.println(exceptionMsg);
			App.ExceptionBox(ioe);
			
	        ioe.printStackTrace();
	    }
	}
}
