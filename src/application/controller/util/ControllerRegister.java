package application.controller.util;

import java.util.ArrayList;
import java.util.List;

import application.controller.Controller_;
import application.controller.util.ChangeNotifier.Listener;

public class ControllerRegister {
	private final List<Object> controllers = new ArrayList<>();

	public boolean Register (Object controller) {

		for(Object ctrl : controllers) {
			if(ctrl.getClass().getSimpleName().equals(controller.getClass().getSimpleName()))
				return false;
		}

		controllers.add(controller);
		return true;
	}
	
	public boolean Unregister (String classStr) {

		for(Object ctrl : controllers) {
			if(ctrl.getClass().getSimpleName().equals(classStr)) {
				controllers.remove(ctrl);
				return true;
			}
		}
		
		return false;
	}
	
	public Object GetInstance (String classStr) {
		
		for(Object ctrl : controllers) {
			if(ctrl.getClass().getSimpleName().equals(classStr)) {
				return ctrl;
			}
		}
		
		return null;
	}
}
