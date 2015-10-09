package application.processor;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.App;

/**
 * Doubly linked list of text blocks. This is a sequence.
 *
 */
public class TextBlock_ {
	 
	protected String str;
	protected TextBlock_ next;
	protected TextBlock_ prev;

	public TextBlock_ (String str) {
		this.str = str;
	}

	public String getString () {
		return str;
	}
	
	/*
	 * <span> Shouldn't be empty, otherwise it's nodeValue can't be selected in html view! </span>
	 * At least add one space ' ':
	 */
	public String getHTMLBlock () {
		return "<span class=\"" + "TextBlock_" + "\" id=\"" + this.hashCode() + "\"> </span>";
	}
	
	public String getTextOutputContent () {
		return str;
	}

	public String getHtmlOutputContent (String codepage) {
/*		
		String outStr = null;
		
		try {
			outStr = new String(EscapeHTMLChars(getTextOutputContent()).getBytes(codepage), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		};
		
		return outStr;
*/
		return EscapeHTMLChars(getTextOutputContent());
	}
	
	// WARNING!!! Doesn't check on zero length block after cutting current one!
	public boolean InsertReplacement (int pos, int length, TextBlock_Replace textBlock) {
		assert (pos >= 0);
		assert (pos + length < this.str.length());
		
		// New block always will be added to the right side
		if(pos >= 0) {
			if(pos + length <= this.str.length()) {
				TextBlock_ rightBlock = new TextBlock_ (this.str.substring(pos + length));
				rightBlock.next = this.next;
				rightBlock.prev = textBlock;
				
				this.str = this.str.substring(0, pos);
				this.next = textBlock;
				
				textBlock.next = rightBlock;
				textBlock.prev = this;
				
				return true;
			}
			else {
				// assume next block isn't TextBlock_, so it's TextBlock_Replace => means replacement conflict!
				assert next instanceof TextBlock_Replace;
				
				TextBlock_Replace textBlockReplace = (TextBlock_Replace)next;
				textBlock.getMatch().addConflict(textBlockReplace.getMatch());
				textBlockReplace.getMatch().addConflict(textBlock.getMatch());
				
				App.log().Error("Replacement conflict (TextBlock_)!");
				return false;
			}
		}
		
		return false;
	}

	public TextBlock_ next () {
		return next;
	}
	
	public TextBlock_ prev () {
		return prev;
	}
	
	public TextBlock_ first() {
		TextBlock_ textBlock = this;
		while(textBlock != null)
			textBlock = textBlock.prev();
		
		return textBlock;
	}
	
	public static String EscapeHTMLChars(String str) {
		
		/* 
		 * skipping because using <pre></pre> tag to display data
		 * 
		String strReplaced = str;
		strReplaced = strReplaced.replace("\"",	"&quot;");
		strReplaced = strReplaced.replace("'",	"&apos;");
		strReplaced = strReplaced.replace("<",	"&lt;");
		strReplaced = strReplaced.replace(">",	"&gt;");
		strReplaced = strReplaced.replace("&",	"&amp;");
		strReplaced = strReplaced.replaceAll("(\\r|\\n)+", "<br/>");
		*/
		
		return str;
	}
	
	
	
}



