package application.processor;

import application.App;

public class TextBlock_Replace extends TextBlock_ {

	Match match;
	
	public TextBlock_Replace(Match match) {
		super(match.getFoundStr());
		this.match = match;
	}

	@Override
	public boolean InsertReplacement (int pos, int length, TextBlock_Replace textBlock){
		
		this.match.addConflict(textBlock.getMatch());
		textBlock.getMatch().addConflict(this.match);
		
		String conflictRuleName = textBlock.match.getOwnerAction().getOwnerRule_Replace().getName();
		
		App.log().Warning("Replacement conflict: " + "'" +
						match.getOwnerAction().getOwnerRule_Replace().getName() +
						"' & '" +
						conflictRuleName + "'. " + 
						"Rule '" + conflictRuleName + "' was ignored.");
		return false;
	}
	
	public Match getMatch () {
		return match;
	}
	
	@Override
	public String getTextOutputContent () {
		return getMatch().getReplaceStr();
	}
	
	/*
	 * <span> Shouldn't be empty, otherwise it's nodeValue can't be selected in html view! </span>
	 * At least add one space ' ':
	 */
	@Override
	public String getHTMLBlock () {
		return "<span class=\"" + "TextBlock_Replace" + "\" id=\"" + this.hashCode() + "\"> </span>";
	}
	
}
