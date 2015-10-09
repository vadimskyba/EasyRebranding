package application.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class EOLDecoder {
    private static final char CR = '\r';
    private static final char LF = '\n';

    public static EOLType discoverString(String strContent) {
    	
        int index = 0;
        int prevChar = 0;
        
        while (index < strContent.length()) {
        	
        	int c = strContent.charAt(index);
        	index++;
        	
        	if(prevChar == CR) {
        		if (c == LF)
                	return EOLType.WINDOWS;
        		else
        			return EOLType.MAC;	 
        	}
        	else if(c == CR) {
        		prevChar = CR;
        	}
        	else if(c == LF)
        		return EOLType.UNIX;
        }
        
        return EOLType.UNKNOWN;
    }    
    
    public static EOLType discover(String fileName) throws IOException {    

        Reader reader = new BufferedReader(new FileReader(fileName));
        EOLType result = discover(reader);
        reader.close();
        return result;
    }

    private static EOLType discover(Reader reader) throws IOException {
        int c;
        while ((c = reader.read()) != -1) {
            switch(c) {        
	            case LF:
	            	return EOLType.UNIX;
	            case CR: {
	                if (reader.read() == LF)
	                	return EOLType.WINDOWS;
	                
	                return EOLType.MAC;
	            }
	            default: continue;
            }
        }
        
        return EOLType.UNKNOWN;
    }
    
    public enum EOLType {
    	AUTO(0), WINDOWS(1), UNIX(2), MAC(3), UNKNOWN(4);
        private final int value;
        
        private EOLType(int value) {
            this.value = value;
        }

        public int toInteger() {
            return value;
        }
        
        public static String GetString (String eolStr) {
        	if(eolStr.length() == 0)
				return GetString(0);
			else if(eolStr.equalsIgnoreCase("win"))
				return GetString(1);
			else if(eolStr.equalsIgnoreCase("unix"))
				return GetString(2);
			else if(eolStr.equalsIgnoreCase("mac"))
				return GetString(3);
			else
				return GetString(4);
        }
        
        public static String IntToStrType (int type) {
        	switch((int)type) {
    		case 0: return "";
    		case 1: return "win";
    		case 2: return "unix";
    		case 3: return "mac";
    		default: return "";
    		}
        }
        
        public String GetString () {
        	return GetString(value);
        }
        
        public static String GetString (int val) {
        	switch(val) {
        	case 0:
        		return System.lineSeparator();
        	case 1:
        		return "\r\n";
        	case 2:
        		return "\n";
        	case 3:
        		return "\r";
        	case 4:
        		return "\n";
        	}
        	
        	return "";
        }
    }
}
