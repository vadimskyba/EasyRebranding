package application.processor;

import java.util.UUID;

/*
 * Class with predefined functions that could be run using regexp: ${func:FunctionName}
 */
public class PredefinedFunctions {

	public static String uuid () {
		
		return UUID.randomUUID().toString().toUpperCase();
	}
}
