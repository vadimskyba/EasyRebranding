package application;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import application.model.Rule_;
import application.model.Rule_Group;
import application.model.Rule_Replace;
import application.model.Source_;
import application.model.Source_File;
import application.model.Source_Folder;
import application.model.Variable;
import application.model.IRule_Node;

/**
 * 
 * XML Wrapper class.
 * Used to load and save the data to xml file.
 * Basically used because marshaling of ObservableList-s is impossible.   
 *
 */
@XmlRootElement(name = "data")
@XmlType(propOrder = {"baseFolder", "variables", "rules" , "sources"})
public class Storage {

	// static data instances because class XmlAdapter_Source_AssignedRules should access them 
	private static String basefolder;
    private static List<Variable> variables = new ArrayList<>();
    private static List<Rule_> rules = new ArrayList<>();
    private static List<Source_> sources = new ArrayList<>();

    public void clearLists() {
    	basefolder = "";
    	variables.clear();
    	rules.clear();
    	sources.clear();
    }
    
  	//**************************************************** baseFolder
    
    public void setBaseFolder(String basefolder) {
    	this.basefolder = basefolder;
    }
    
    public String getBaseFolder() {
    	return basefolder;
    }
    
	//**************************************************** variables
    
    public void setVariables(List<Variable> variables) {
        this.variables.addAll(variables);
    }
    

    @XmlElementWrapper(name = "variables")
    @XmlElement(name = "variable")
    public List<Variable> getVariables() {
        return variables;
    }

    
	//**************************************************** rules   

    public void setRules(List<Rule_> rules) {
        this.rules.addAll(rules);
    }
    
    
    @XmlElementWrapper(name = "rules")
    @XmlElements({ 
        @XmlElement(name = "group", type=Rule_Group.class),
        @XmlElement(name = "replace", type=Rule_Replace.class)
    })
    public List<Rule_> getRules() {
        return rules;
    }      
    
    
	//**************************************************** sources
    
    public void setSources(List<Source_> sources) {
        this.sources.addAll(sources);
    }
    
    
    @XmlElementWrapper(name = "sources")
    @XmlElements({ 
        @XmlElement(name = "file", type=Source_File.class),
        @XmlElement(name = "folder", type=Source_Folder.class)
    })
    public List<Source_> getSources() {
        return sources;
    }

    
	//**************************************************** XmlAdapter-s

    public static class XmlAdapter_Source_AssignedRules extends XmlAdapter<String, Rule_> {

    	@Override
    	public Rule_ unmarshal(String ruleName) throws Exception {
    		
    		return App.data().FindRuleRecursive(Storage.rules, ruleName);
    	}

    	@Override
    	public String marshal(Rule_ rule) throws Exception {
    		
    		return rule.toString();
    	}
    }
}
