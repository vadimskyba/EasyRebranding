package application.processor;

import java.util.ArrayList;
import java.util.List;

import application.model.Rule_Replace;
import application.processor.Match;

public class Action_Replace {

	Rule_Replace processedRule;
	
	String findWhatStr;
	String replaceWithStr;

	final List <Match> matchesList = new ArrayList<>();
	
	public Action_Replace (Rule_Replace processedRule) {
		this.processedRule = processedRule;
		
		// Here we should parse variables from Variable list!!!!!!!!!!!!!!!!!!!!!!!!!
		findWhatStr = processedRule.getFindWhat();
		
		// Here we should parse variables from Variable list!!!!!!!!!!!!!!!!!!!!!!!!!
		replaceWithStr = processedRule.getReplaceWith();
	}
	
	public void addMatch (int position, String foundStr, String replaceStr) {
		matchesList.add( new Match(this, position, foundStr, replaceStr) );
	}
	
	public String getFindWhatStr () {
		return findWhatStr;
	}
	
	public String getReplaceWithStr () {
		return replaceWithStr;
	}

	public List<Match> getMatchesList() {
		return matchesList;
	}
	
	public Rule_Replace getOwnerRule_Replace () {
		return processedRule;
	}
}
