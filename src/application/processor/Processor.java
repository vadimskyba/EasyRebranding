package application.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import application.App;
import application.model.Rule_Replace;
import application.model.Source_File;
import application.model.Variable;
import application.util.FileCharsetDecoder;
import application.util.EOLDecoder;
import application.util.EOLDecoder.EOLType;
import application.util.PathTool;


public final class Processor {

	/* Prevent of instantiation: simulate static class behavior */
	private Processor () {}
	
	
	public static void GenerateStatus (ReplaceData rData) {
		GenerateReplacedContent(rData);
		rData.cleanActionList(); // free memory: leave only statistics (matches number, conflicts number)
	}
	
	public static TextBlock_ GenerateReplacedContent (ReplaceData rdata) {
		
		assert (rdata.getSource() instanceof Source_File);
		rdata.clean();
		String strContent = LoadFile((Source_File)rdata.getSource());
		TextBlock_ textBlockList = null;
		
		if(strContent != null) {

			LoadAndSearchMatches(strContent, rdata); //!!!!! Filling Source_.replaceData (only Source_File processing) 
			textBlockList = new TextBlock_(strContent);
			int conflicts = 0;
			
			for(Action_Replace action : rdata.getReplaceActionList()) {
				for(Match match : action.getMatchesList()) {
					
					int sequencePosition = 0;
					TextBlock_ blockStep = textBlockList;
					
					while (blockStep != null) {
						
						if(	match.getPosition() >= sequencePosition &&
							match.getPosition() < sequencePosition + blockStep.getString().length() ) {
							
							TextBlock_Replace blockReplace = new TextBlock_Replace(match);
							
							if( !blockStep.InsertReplacement (match.getPosition() - sequencePosition, match.getFoundStr().length(), blockReplace) )
								conflicts++;
							break;
						}
						
						sequencePosition += blockStep.getString().length();
						blockStep = blockStep.next();
					}
				}
			}
			
			if(conflicts > 0)
				rdata.setStatus(rdata.getStatus() + ", conflicts: " + conflicts);
		}
		
		return textBlockList;
	}
	
	private static void LoadAndSearchMatches (String strContent, ReplaceData rdata) {
		
		//!!!!!!!!!!!!!!!!!!!!!!!!!!  Don't forget to add rules from parent folders
		
		if(rdata.getSource().getAssignedRulesEnabled()) {
		
			rdata.getSource().getAssignedRules().forEach(rule -> {
				if(rule instanceof Rule_Replace) {
					Rule_Replace ruleReplace = (Rule_Replace)rule;
					
					if(ruleReplace.getEnabled())
						rdata.getReplaceActionList().add(new Action_Replace( ruleReplace ));
				}
			});
			
			int nAllManches = 0;
			if(strContent != null) {
				for (Action_Replace replace : rdata.getReplaceActionList()) {
					
					if(replace.processedRule.getSearchMode() == 0) // simple text search
						nAllManches += SearchSimpleMatches(replace, strContent);
					
					else if(replace.processedRule.getSearchMode() == 1) // regexp search
						nAllManches += SearchRegexpMatches(replace, strContent, 0);
					
					else if(replace.processedRule.getSearchMode() == 2)	// regexp multiline mode
						nAllManches += SearchRegexpMatches(replace, strContent, Pattern.MULTILINE);
					
					else if(replace.processedRule.getSearchMode() == 3) // regexp dotall mode
						nAllManches += SearchRegexpMatches(replace, strContent, Pattern.DOTALL);
					
					else if(replace.processedRule.getSearchMode() == 4) // regexp multiline + dotall mode
						nAllManches += SearchRegexpMatches(replace, strContent, Pattern.MULTILINE | Pattern.DOTALL);
					
					else
						App.log().Error("Unsuppoerted search mode type selected!");
				}
			}
			
			rdata.setStatus("matches: " + nAllManches);
		}
		else {
			rdata.setStatus("no rules");
			App.log().Warning("Warning. No rules assigned to the source: " + rdata.getSource().getSafeFullRelativePath1());
		}
	}
	
	public static String LoadFile(Source_File src) {
		
		String filename = PathTool.GetFullPath(App.data().getBaseFolder(), src.getFullRelativePath());
		
		try	{

			File f = new File(filename);
			
			if(!f.isFile()) {
				src.rdata().statusProperty().set("file not found");
				App.log().Warning("File '" + filename + "' doesn't exists!");
				return null;
			}
			
			long fileSize = f.length();
			if (fileSize > 50 * 1024 * 1024) {
				src.rdata().statusProperty().set("large file");
				App.log().Warning("Too big file to open. Max possible size: 50 Mb. File: " + filename + ". Size: " + fileSize);
				return null;
			}
			
	        Charset charset = null;
	        if(src.getCharset().length() == 0)
	        	charset = FileCharsetDecoder.detectCharset(f);
	        else {
	        	try {
	        		charset = Charset.forName(src.getCharset());
	        	}
	        	catch (Exception e) {
	        		src.rdata().setStatus("bad charset");
	        		src.rdata().SET_WorkCharset(null);
	        		App.log().Warning("Bad charset specified: " + filename);
	        		return null;
	        	}
	        }
	        
	        //FileEOLDecoder.EOLType format = FileEOLDecoder.discover(filename);
	        
	        if (charset != null) {
	        	
	        	FileInputStream fin = new FileInputStream(new File(filename));
	            java.util.Scanner scanner = new java.util.Scanner(fin, charset.displayName()).useDelimiter("\\A");
	            String strContent = scanner.hasNext() ? scanner.next() : "";
	        	
	            EOLDecoder.EOLType format = EOLDecoder.discoverString(strContent);
	            
	            if(!format.GetString().equalsIgnoreCase("\n"))
	            	strContent = strContent.replaceAll(format.GetString(), "\n");
	        	
	        	src.rdata().SET_WorkCharset(charset);
	        	src.rdata().SET_WorkEolType(format.toInteger());
	        	return strContent;
	        }
	        else {
	        	src.rdata().statusProperty().set("unrecognized charset");
	        	src.rdata().SET_WorkCharset(null);
	        	App.log().Warning("Unrecognized charset: " + filename);
	            return null;
	        }	
        }
		catch (FileNotFoundException fnfe) {
			src.rdata().statusProperty().set("file not found");
            fnfe.printStackTrace();
            App.log().Warning("File not found: " + filename);
            return null;
        }
		catch (IOException ioe) {
			src.rdata().statusProperty().set("IO exception");
            ioe.printStackTrace();
            App.log().Error(ioe.getMessage());
            return null;
        }
		
	}
	
	public static boolean SaveFile(Source_File src, String outBaseDir) {

		TextBlock_ textBlockList = Processor.GenerateReplacedContent(src.rdata());
		if(src.rdata().GET_WorkCharset() != null) {
			
			String fullPath = PathTool.GetFullPath(outBaseDir, PathTool.getSafeRelativePath(src.getFullRelativePath()));
			String dirPath = PathTool.GetFullPath(outBaseDir, PathTool.getSafeRelativePath(src.getRelativePath()));
			
			BufferedWriter out = null;
			try {
				File dir = new File(dirPath);
				if(!dir.exists())
					dir.mkdirs();
				
				File file = new File(fullPath);
				if(!file.exists())
					file.createNewFile();
				
				Path path = file.toPath();
				
				Charset charset = null;
				if(src.getOutCharset().length() == 0) {
					if(src.getCharset().length() == 0)
						charset = src.rdata().GET_WorkCharset();
					else
						charset = Charset.forName(src.getCharset());
				}
				else
					charset = Charset.forName(src.getOutCharset());
				
				String eolStr = null;
				if(src.getOutEOL().length() == 0)
					eolStr = EOLType.GetString(src.rdata().GET_WorkEolType());
				else
					eolStr = EOLType.GetString(src.getOutEOL());
				
				out = Files.newBufferedWriter(path, charset);
				
				TextBlock_ textBlock = textBlockList;
				while(textBlock != null) {
					
					if(eolStr.equals("\n"))
						out.write(textBlock.getTextOutputContent());
					else
						out.write(textBlock.getTextOutputContent().replaceAll("\n", eolStr));
					
					textBlock = textBlock.next();
				}
				out.close();
				App.log().Message("File saved: " + fullPath);
				
	        } catch (IOException ioe) {
				App.log().Error(ioe.getMessage());
	            ioe.printStackTrace();
			}
			
			return true;
		}		
		return false; // charset wasn't recognized during processing
	}
	
	private static int SearchSimpleMatches(Action_Replace action, final String strContent) {
	
    	String findWhatString = PrepareFindString (action.getFindWhatStr(), false);
    	String replaceWithString = PrepareReplaceString (action.getReplaceWithStr(), false);
    	
    	if(replaceWithString != null) {
    		int iMatch = strContent.indexOf(findWhatString);
    		
    		while(iMatch >= 0) {
    			action.addMatch(iMatch, findWhatString, replaceWithString);
    			iMatch = strContent.indexOf(findWhatString, iMatch+findWhatString.length());
    		}
    	}
    	
		return action.getMatchesList().size();
	}
	
	
	private static int SearchRegexpMatches(Action_Replace action, final String strContent, int mode) {
		
    	String findWhatString = PrepareFindString (action.getFindWhatStr(), true);	// hangs on: Dimensions_(.*?)+(_([0-9\.])\.(exe|msi))
    	String replaceWithString = PrepareReplaceString (action.getReplaceWithStr(), true);
		
    	if(findWhatString != null && replaceWithString != null && findWhatString.length() > 0) {
    	
    		try {
    		
	    		Pattern pattern = null;
	    		
	    		if(mode != 0)
	    		   	pattern = Pattern.compile(findWhatString, mode);
	    		else
	    			pattern = Pattern.compile(findWhatString);
	    		
		        Matcher matcher = pattern.matcher(strContent);
		    	while(matcher.find()) {
		    		
		    		String matchStr = strContent.substring(matcher.start(), matcher.end());
		    		
		    		if(matchStr.length() == 0) // means error processing regexp. Have no time to handle it. Maybe in future.
		    			break;
		    		
		    		Matcher subMatcher = pattern.matcher(matchStr);
		    		subMatcher.find();
		    		String replaceString = subMatcher.replaceFirst(replaceWithString);
		    		
		    		//App.log().Message("ReplaceWithString: " + replaceString);
		    		
		    		action.addMatch(matcher.start(), matchStr, replaceString);
		    	}
    		} catch (final RuntimeException ex) {
    			App.log().Warning("Regexp syntax error: " + ex.getMessage());
    		}
    	}
    	
		return action.getMatchesList().size();
	}
	
	private static String PrepareFindString(String srcStr, boolean bRegExp) {
		
		String findStr = new String();
		
		Pattern pattern = Pattern.compile("\\$\\{([^\\$]+)\\}");
		Matcher matcher = pattern.matcher(srcStr);
		
		int pos = 0;

		while(matcher.find(pos)) {
    		
    		String varName = matcher.group(1);
    		String val = null;
    		
    		if(varName.indexOf("func:") == 0) {		// is varName a function?
    			val = ParseFunction(varName.substring(5));
    		}
    		else {
    			Variable var;
    			var = App.data().GetVariable(varName);
    		
	    		if(var != null)
	    			val = var.getValue(); 
    		}

    		if(val != null) {
    			if(bRegExp)
    				findStr += srcStr.substring(pos,matcher.start()) + Pattern.quote(val);
    			else
    				findStr += unescapeJavaString( srcStr.substring(pos,matcher.start()), bRegExp ) + val;
    			
        		pos = matcher.end();
        		matcher.reset();
        		matcher = pattern.matcher(srcStr);
    		}
    		else {
    			App.log().Error("variable or function '" + varName + "' not found.");
    			return null;
    		}
		}
		
		if(pos < srcStr.length()) {
			if(bRegExp)
				findStr += srcStr.substring(pos, srcStr.length());
			else
				findStr += unescapeJavaString( srcStr.substring(pos, srcStr.length()), false );
		}
		
		return findStr;
	}

	private static String PrepareReplaceString(final String srcStr, boolean bRegExp) {
	
		String replaceStr = new String();
		
		Pattern pattern = Pattern.compile("\\$\\{([^\\$\\{\\}]+?)\\}");
		Matcher matcher = pattern.matcher(srcStr); 
		
		int pos = 0;
		
		while(matcher.find(pos)) {

			String varName = matcher.group(1);
			String val = null;
			
    		if(varName.indexOf("func:") == 0) {		// is varName a function?
    			val = ParseFunction(varName.substring(5));
    		}
    		else {
    			Variable var;
    			var = App.data().GetVariable(varName);
    		
	    		if(var != null)
	    			val = var.getValue(); 
    		}
			
    		if(val != null) {
    			
    			if(bRegExp == true) {
    				val = val.replace("\\", "\\\\");
    				val = val.replace("$", "\\$");

    				replaceStr += unescapeJavaString( srcStr.substring(pos,matcher.start()), bRegExp ) + val;
    			}
    			else
    				replaceStr += unescapeJavaString( srcStr.substring(pos,matcher.start()), bRegExp ) + val;
    			
        		pos = matcher.end();
        		matcher.reset();
        		matcher = pattern.matcher(srcStr);
    		}
    		else {
    			App.log().Error("variable or function '" + varName + "' not found.");
    			return null;
    		}
    		
		}
			
		if(pos < srcStr.length()) {
			if(bRegExp == true)
				replaceStr += unescapeJavaString( srcStr.substring(pos, srcStr.length()), bRegExp );
			else
				replaceStr += unescapeJavaString( srcStr.substring(pos, srcStr.length()), bRegExp );
		}
		
		return replaceStr;
	}
	
	private static String unescapeJavaString(String st, boolean bRegExp) {

	    StringBuilder sb = new StringBuilder(st.length());
	    
	    for (int i = 0; i < st.length(); i++) {
	        char ch = st.charAt(i);
	        if (ch == '\\') {
	            char nextChar = (i == st.length() - 1) ? '\\' : st
	                    .charAt(i + 1);
	            // Octal escape?
	            if (nextChar >= '0' && nextChar <= '7') {
	                String code = "" + nextChar;
	                i++;
	                if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                        && st.charAt(i + 1) <= '7') {
	                    code += st.charAt(i + 1);
	                    i++;
	                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                            && st.charAt(i + 1) <= '7') {
	                        code += st.charAt(i + 1);
	                        i++;
	                    }
	                }
	                sb.append((char) Integer.parseInt(code, 8));
	                continue;
	            }
	            switch (nextChar) {
	            case '\\':
	                ch = '\\';
	                if(bRegExp)
	                	sb.append('\\');
	                break;
	            case 'b':
	                ch = '\b';
	                break;
	            case 'f':
	                ch = '\f';
	                break;
	            case 'n':
	                ch = '\n';
	                break;
	            case 'r':
	                ch = '\r';
	                break;
	            case 't':
	                ch = '\t';
	                break;
	            case '\"':
	                ch = '\"';
	                break;
	            case '\'':
	                ch = '\'';
	                break;
	            // Hex Unicode: u????
	            case 'u':
	                if (i >= st.length() - 5) {
	                    ch = 'u';
	                    break;
	                }
	                int code = Integer.parseInt(
	                        "" + st.charAt(i + 2) + st.charAt(i + 3)
	                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
	                sb.append(Character.toChars(code));
	                i += 5;
	                continue;
	            case '$':
		        	ch = '$';
		        	if(bRegExp)
		        		sb.append('\\');
		        	break;
	            	
	            }
	            i++;
	        }
	        
	        sb.append(ch);
	    }
	    return sb.toString();
	}
	
	private static String ParseFunction (String funcStr) {

		String result = null;
		java.lang.reflect.Method method;

		// http://stackoverflow.com/questions/2467544/invoking-a-static-method-using-reflection
		try {
			method = PredefinedFunctions.class.getMethod(funcStr);
			result = method.invoke(PredefinedFunctions.class).toString();
			//result = method.getReturnType().toString();
			
		} catch (SecurityException e) {
			// ...
		} catch (NoSuchMethodException e) {
			// ...
		} catch (IllegalArgumentException e) {
			// ...	
		} catch (IllegalAccessException e) {
			// ...
		} catch (InvocationTargetException e) {
			// ...
		}
		
		return result;
	}
}
