package application.model;

import javax.xml.bind.annotation.XmlRootElement;

import java.nio.charset.Charset;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.model.Rule_;
import application.model.Source_Folder;
import application.processor.ReplaceData;
import application.util.EOLDecoder;

public class Source_File extends Source_ {

	private final StringProperty charset;
	private final StringProperty outCharset;
	private final StringProperty outEol;
	
	public Source_File () {
		this("","");
	}
	
	public Source_File(String Name, String RelativePath) {
		super(Name, RelativePath);
		
		this.charset = new SimpleStringProperty("");
		this.outCharset = new SimpleStringProperty("");
		this.outEol = new SimpleStringProperty("");
    }
	
	
	public String getCharset() {
        return charset.get();
    }

    public void setCharset(String charset) {
        this.charset.set(charset);
    }

    public StringProperty charsetProperty() {
        return charset;
    }
    
    
	public String getOutCharset() {
        return outCharset.get();
    }

    public void setOutCharset(String outCharset) {
        this.outCharset.set(outCharset);
    }

    public StringProperty outCharset() {
        return outCharset;
    }    
	
    
	public String getOutEOL() {
        return outEol.get();
    }

    public void setOutEOL(String outEOL) {
        this.outEol.set(outEOL);
    }

    public StringProperty outEOL() {
        return outEol;
    }	

}
