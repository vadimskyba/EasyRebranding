package application.controller.util;

import javafx.scene.Node;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

import application.controller.Controller_;

public class ChangeNotifier {

	private final List<Listener> listeners = new ArrayList<>();
	
	public ChangeNotifier () {
	}
	
	public void addListener (Controller_ controller, event event) {

		if(!isRegistered(controller, event))
			listeners.add( new Listener (controller, event) );
	}
	
	private boolean isRegistered (Controller_ controller, event event) {
		
		for(Listener listener : listeners) {
			if(listener.controller == controller && listener.event == event)
				return true;
		}
		
		return false;
	}
	
	public void removeListener (Controller_ controller, event event) {
	
		for(Listener listener : listeners) {
			if(listener.controller == controller && listener.event == event)
				listeners.remove(listener);
		}
	}
	
	public void notifyControllers (Controller_ sender, event event, Object... p) {
		
		for(Listener listener : listeners) {
			if(listener.controller != sender && listener.event == event)
				listener.controller.onNotify(event, p);
		}
	}
	
	
    //*********************************************************************************************
    // Internal types
    //*********************************************************************************************
	public enum event { 
		UPD_RULE_NAME,			// update controllers on Rule_.Name change
		UPD_SRC_RULEASSIGN,		// update controllers on Source_.AssignedRules change
		UPD_SRC_SELECTION,		// Something was changed in selected Source_. Reselect it. 
		CMD_UNBIND_ALL,			// Unbind all controllers from data. Used before loading model from xml-file.
		EXPAND_PANE_PREIVEW		// Expands preview pane
	}
	
	public class Listener {
		event event;
		Controller_ controller;
		
		public Listener (Controller_ controller, event event) {
			this.controller = controller;
			this.event = event;
		}
	}

	
}
