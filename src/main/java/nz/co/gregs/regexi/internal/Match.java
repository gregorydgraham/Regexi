/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.MatchResult;

/**
 *
 * @author gregorygraham
 */
public class Match {

	static Match from(PartialRegex aThis, MatchResult m) {
		return new Match(aThis, m);
	}

	private final String match;
	private final List<MatchedGroup> groups = new ArrayList<>(1);
	private final PartialRegex regex;
	private HashMap<String, String> namedCaptures = null; // will be set when needed
	private final boolean didMatch;

	private Match(PartialRegex regex, MatchResult matchResult) {
		this.regex = regex;
		this.match = matchResult.group();
		for (int i = 0; i < matchResult.groupCount(); i++) {
			this.groups.add(new MatchedGroup(matchResult.group(i), i));
		}
		didMatch = true;
	}

	public boolean didMatch() {
		return didMatch;
	}

	public String getEntireMatch() {
		return match;
	}

	public HashMap<String, String> getAllNamedCaptures() {
		if (namedCaptures == null) {
			namedCaptures = regex.getAllNamedCapturesOfFirstMatchWithinString(match);
		}
		return namedCaptures;
	}

	public List<MatchedGroup> allGroups() {
		return groups;
	}

	public String getNamedCapture(String name) {
		return getAllNamedCaptures().get(name);
	}

	@Override
	public String toString() {
		return "MATCH :"+getEntireMatch();
	}

}
