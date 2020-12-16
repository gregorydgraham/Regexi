/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.util.stream.Stream;

/**
 *
 * @author gregorygraham
 */
public class Match {

	static Match from(String group) {
		return new Match(group);
	}
	private final String match;

	private Match(String group) {
		this.match = group;
	}

	public String getEntireMatch() {
		return match;
	}
	
}
