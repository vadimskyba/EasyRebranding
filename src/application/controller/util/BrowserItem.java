package application.controller.util;

import java.io.File;

import application.App;
import application.model.Source_;

/**
 * Implements file system object (file or folder)
 *
 */
public class BrowserItem {
	
	private File f;
	private Source_ sourceObj;
	
	public BrowserItem (File f, Source_ sourceObj) {
		this.f = f;
		this.sourceObj = sourceObj;
	}
	
	public File getFile() {
		return f;
	}
	
	public Source_ getSrcObj() {
		return sourceObj;
	}
	
	public void setSrcObj(Source_ sourceObj) {
		this.sourceObj = sourceObj;
	}
	
	@Override
	public String toString () {
		return f.getName().length() > 0 ? f.getName() : App.data().getBaseFolder();
	}
}
