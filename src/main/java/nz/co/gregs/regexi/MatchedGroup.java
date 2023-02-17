/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.io.Serializable;

/**
 *
 * @author gregorygraham
 */
public class MatchedGroup implements Serializable {

	private final String string;
	private final int index;

	public MatchedGroup(String group, int i) {
		this.string = group;
		this.index = i;
	}

	@Override
	public String toString() {
		return "MatchedGroup{ index=" + index + ", string=" + string + "}";
	}

	public String getContents() {
		return string;
	}

	public int getIndex() {
		return index;
	}
	
}
