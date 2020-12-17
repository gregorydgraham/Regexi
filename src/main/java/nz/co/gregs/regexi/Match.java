/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author gregorygraham
 */
public class Match {

	static Match from(Regex regex, String group) {
		return new Match(regex, group);
	}
	private final String match;
	private final Regex regex;
	private HashMap<String, String> namedCaptures = null;

	private Match(Regex regex, String group) {
		this.match = group;
		this.regex = regex;
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

	public List<String> getAllGroups() {
		return regex.getAllGroups(match);
	}

	public String getNamedCapture(String name) {
		return getAllNamedCaptures().get(name);
	}

}
