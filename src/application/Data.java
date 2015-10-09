package application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import application.model.Rule_;
import application.model.Rule_Replace;
import application.model.Rule_Group;
import application.model.Rule_Root;
import application.model.Source_File;
import application.model.Variable;
import application.model.Source_;
import application.model.Source_Folder;
import application.model.ISource_;
import application.model.IRule_;
import application.model.IRule_Node;
import application.util.PathTool;
import application.controller.util.ChangeNotifier.event;

public class Data {

    private ObservableList<Variable> VariableData = FXCollections.observableArrayList();	// linear list
    private ObservableList<Rule_> RuleData = FXCollections.observableArrayList();			// nested list
    private ObservableList<Source_> SrcData = FXCollections.observableArrayList();			// linear list

    private StringProperty projectPath;
    private BooleanProperty modifiedFlag;
    private StringProperty baseFolder;
    
	public Data () {
		projectPath = new SimpleStringProperty("");
		baseFolder = new SimpleStringProperty("");
		modifiedFlag = new SimpleBooleanProperty(false);
	}
	
    public ObservableList<Variable> varData() {
        return VariableData;
    }

    public ObservableList<Rule_> ruleData() {
        return RuleData;
    }    
    
	public ObservableList<Source_> srcData() {
    	return SrcData;
    }
	
	//**************************************************** project

  	public String getProjectPath() {
  		return projectPath.get();
	}

	public void setProjectPath(String projectPath) {
		this.projectPath.set(projectPath);
		App.setTitle(projectPath, modifiedFlag.get());
	}

	public StringProperty projectPathProperty() {
		return projectPath;
	}
	
	//**************************************************** modifiedFlag

  	public boolean getModifiedFlag() {
  		return modifiedFlag.get();
	}

	public void setModifiedFlag(boolean bModified) {
		this.modifiedFlag.set(bModified);
		App.setTitle(projectPath.get(), bModified);
	}

	public BooleanProperty modifiedFlagProperty() {
		return modifiedFlag;
	}
	
	//**************************************************** baseFolder
    
  	public String getBaseFolder() {
  		return baseFolder.get();
	}

	public void setBaseFolder(String BaseFolder) {
		String normBaseFolder = PathTool.NormalizePath(BaseFolder);
		normBaseFolder = normBaseFolder.endsWith("/") ? normBaseFolder : normBaseFolder + "/";
		this.baseFolder.set( PathTool.GetSpecificOSPath(normBaseFolder));
	}

	public StringProperty baseFolderProperty() {
		return baseFolder;
	}
	
	//**************************************************** Data create operations
	
	public Source_File new_Source_File (String fullPath) {
		
		String fullRelativePath = PathTool.NormalizePath(PathTool.GetRelativePath(fullPath, baseFolder.get()));
		String fileName;
		String filePath;
		
		if(fullRelativePath.lastIndexOf("/") >= 0) {
			fileName = fullRelativePath.substring(fullRelativePath.lastIndexOf("/")+1, fullRelativePath.length());
			filePath = fullRelativePath.substring(0, fullRelativePath.lastIndexOf("/")+1);
		}
		else {
			fileName = fullRelativePath;
			filePath = "";
		}
		
		Source_File source = new Source_File(fileName,  filePath);
		App.data().srcData().add(source);
		
		setModifiedFlag(true);
		return source;
	}
	
	public Source_Folder new_Source_Folder (String fullPath) {
		
		String fullRelativePath = PathTool.NormalizePath(PathTool.GetRelativePath(fullPath, baseFolder.get()));
		String folderName;
		String folderPath;
		
		if(fullRelativePath.lastIndexOf("/") >= 0) {
			folderName = fullRelativePath.substring(fullRelativePath.lastIndexOf("/")+1, fullRelativePath.length());
			folderPath = fullRelativePath.substring(0, fullRelativePath.lastIndexOf("/")+1);
		}
		else {
			folderName = fullRelativePath;
			folderPath = "";
		}		
		
		Source_Folder source = new Source_Folder( folderName, folderPath );
		App.data().srcData().add(source);
		
		setModifiedFlag(true);		
		return source;
	}	
	
	public Rule_Replace new_Rule_Replace (String name, IRule_Node parent) {
		Rule_Replace newRule = new Rule_Replace(name);
		parent.getChildren().add(newRule);
		
		setModifiedFlag(true);		
		return newRule;
	}

	public Rule_Replace duplicate_Rule_Replace (String name, IRule_Node parent, Rule_Replace src) {
		Rule_Replace newRule = new Rule_Replace(name, src);
		parent.getChildren().add(newRule);
		
		setModifiedFlag(true);		
		return newRule;
	}
	
	public Rule_Group new_Rule_Group (String name, IRule_Node parent) {
		Rule_Group newGroup = new Rule_Group(name);
		parent.getChildren().add(newGroup);
		
		setModifiedFlag(true);
		return newGroup;
	}
	
	public Variable new_Variable (String name) {
		Variable newVar = new Variable(name, "");
		VariableData.add(newVar);
		
		setModifiedFlag(true);
		return newVar;		
	}
	//**************************************************** Data remove operations
	
	public void removeRule(IRule_ ruleObj) {
		if(ruleObj != null) {
			
			if(ruleObj instanceof IRule_Node) {
				
				IRule_Node ruleNode = ((IRule_Node)ruleObj);
				
				while(ruleNode.getChildren().size() > 0)
					removeRule(ruleNode.getChildren().get(0));
			}
			
			// Remove references to ruleObj owned by Sources
			for(Source_ src : SrcData) {
				
				if(src instanceof Source_) {
					Source_ srcFile = (Source_)src;
					while(srcFile.getAssignedRules().remove(ruleObj));
				}
			}
			
			removeRuleRecursively(ruleObj, RuleData);
		}
		
		setModifiedFlag(true);
	}
	
	private void removeRuleRecursively (IRule_ ruleObj, ObservableList<Rule_> list) {
		
		for (IRule_ child : list) {
			if(child instanceof IRule_Node)
				removeRuleRecursively(ruleObj, ((IRule_Node)child).getChildren());
		}
		
		while(list.remove(ruleObj));
		
		setModifiedFlag(true);
	}
	
	public boolean removeSource (ISource_ srcObj) {
		
		if(srcData().remove(srcObj) == true) {
			setModifiedFlag(true);
			return true;
		}
		
		return false;
	}
	
	public boolean removeVariable (Variable variable) {
		
		if(VariableData.remove(variable)) {
			setModifiedFlag(true);
			return true;
		}
			
		return false;
	}
	
	//**************************************************** Assigning Rule_ to Source_
	
	public void AssignRule (Source_ source, Rule_ rule) {
		
		// don't add same value twice
		if( source.getAssignedRules().stream().filter(rule_i -> 
			rule_i == rule).collect(Collectors.toList()).size() == 0 ) {
			
			source.getAssignedRules().add(rule);
			App.log().Message("+1 assigned new rule: " + source.getAssignedRules().size());
			setModifiedFlag(true);
		}
	}

	public void UnassignRule (Source_ source, Rule_ rule) {
		
		while(source.getAssignedRules().remove(rule));
		App.log().Message("-1 assigned new rule: " + source.getAssignedRules().size());
		setModifiedFlag(true);
	}
	

	//**************************************************** Auxiliary operations	
	/*
	public String GetRelativePath (String fullPath) {
		if(fullPath.indexOf(baseFolder.get()) == 0)
			return fullPath.substring(baseFolder.length().get(), fullPath.length());
		
		return fullPath;
	}
	*/
	public Source_ FindSourceByFullRelativePath (String fullRelativePath, boolean isFolder) {
		
		String path = PathTool.NormalizePath(fullRelativePath);
		
		for(Source_ source : SrcData) {
			if(PathTool.getSafeRelativePath(source.getFullRelativePath()).equals(path) && !((source instanceof Source_Folder) ^ isFolder))
				return source;
		};
		
		return null;
	}
	
	public static Rule_ FindRuleRecursive(List<Rule_> ruleList, String ruleName) {
		
		Rule_ matchedRule = null;
		
		for(Rule_ rule : ruleList)	{
			if(rule.toString().equals(ruleName))
				matchedRule = rule;
			else if(rule instanceof IRule_Node)
				matchedRule = FindRuleRecursive(((IRule_Node)rule).getChildren(), ruleName);

			if(matchedRule != null)
				break;
		}
		
		return matchedRule; 
	}
	
	public Variable GetVariable (String varName) {
		for(Variable var : VariableData) {
			if(var.getName().equals(varName))
				return var;
		}
		
		return null;
	}
	
	//**************************************************** Load/Save data to xml
	
	public boolean saveData() {
		
		if(projectPath.get().length() > 0)
			return saveDataToFile(new File(projectPath.get()));
			
		return false;
	}
	
	public boolean saveDataToFile(File file) {

		try {
	        JAXBContext context = JAXBContext.newInstance(Storage.class);
	        Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        Storage xmlWrapper = new Storage();
	        
	        xmlWrapper.setBaseFolder(baseFolder.get());
	        xmlWrapper.setSources(App.data().srcData());
	        xmlWrapper.setVariables(App.data().varData());
	        xmlWrapper.setRules(App.data().ruleData());
	        
	        marshaller.marshal(xmlWrapper, file);
	        
	        xmlWrapper.clearLists();
	        
	        // Save the file path to the registry.
	        /////////// setPersonFilePath(file);
	        
	    } catch (Exception e) { // catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not save data");
	        alert.setContentText("Could not save data to file:\n" + file.getPath() + "\n\nCause: " + e.getMessage());

	        alert.showAndWait();
	        
	        return false;
	    }
		
		setProjectPath(file.getPath());
		setModifiedFlag(false);
		return true;
	}	

	public boolean loadDataFromFile(File file) {
		
	    try {
	    	
	        JAXBContext context = JAXBContext.newInstance(Storage.class);
	        Unmarshaller um = context.createUnmarshaller();
	        
	        // Reading XML from the file and unmarshalling.
	        Storage xmlWrapper = (Storage) um.unmarshal(file);
	        
	        App.notifier().notifyControllers(null, event.CMD_UNBIND_ALL, null);
	        // Preserve order: sources -> rules -> others
	        SrcData.clear();
	        RuleData.clear();
	        VariableData.clear();
	        
	        baseFolder.set(xmlWrapper.getBaseFolder());
	        RuleData.addAll(xmlWrapper.getRules());
	        SrcData.addAll(xmlWrapper.getSources());
	        VariableData.addAll(xmlWrapper.getVariables());
	        
	        xmlWrapper.clearLists();
	        
	        // Save the file path to the registry.
	        ///////////////setPersonFilePath(file);
	        
	    } catch (Exception e) { // catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not load data");
	        alert.setContentText("Could not load data from file:\n" + file.getPath() + "\n\nCause: " + e.getMessage());

	        alert.showAndWait();
	        
	        return false;
	    }
	    
	    setProjectPath(file.getPath());
	    return true;
	}

	public void newData () {
		App.notifier().notifyControllers(null, event.CMD_UNBIND_ALL, null);
        // Preserve order: sources -> rules -> others
        SrcData.clear();
        RuleData.clear();
        VariableData.clear();
        baseFolder.set("");
	}
}
