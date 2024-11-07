/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gregorygraham
 */
public class NamedBackReference extends PartialRegex {

	private final String literal;
	private final String name;

	public NamedBackReference(String name) {
		if (name == null) {
			this.literal = "";
			this.name = "";
		} else {
			// \k<name>
			this.literal = "\\k<" + name + ">";
			this.name = name;
		}
	}

	@Override
	public String toRegexString() {
		return literal;
	}
	
	@Override
	public String toString() {
		return "\\k<"+this.name+">";
	}

	@Override
	public List<PartialRegex> getRegexParts() {
		List<PartialRegex> result = new ArrayList<PartialRegex>(1);
		result.add(this);
		return result;
	}

}
