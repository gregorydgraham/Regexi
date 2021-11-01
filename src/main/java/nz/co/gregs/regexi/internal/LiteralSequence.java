/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements literal sequences by replacing all protected characters as
 * required.
 *
 * @author gregorygraham
 */
public class LiteralSequence extends PartialRegex {

	private final String literal;

	/**
	 * Creates a new literal sequence by replacing all protected characters as
	 * required.
	 *
	 * <p>
	 * All characters are escaped as required to avoid them acting as regular
	 * expression commands.<p>
	 *
	 * @param literals the literal characters to be added to the pattern
	 */
	public LiteralSequence(String literals) {
		if (literals == null) {
			this.literal = "";
		} else {
			this.literal = literals
					.replace("\\", "\\\\")
					.replace("+","\\+")
					.replace("{","\\{")
					.replace("}","\\}")
					.replace("(","\\(")
					.replace(")","\\)")
					.replace("[","\\[")
					.replace("]","\\]")
					.replace(".","\\.")
					.replace("?","\\?")
					.replace("*","\\*")
					.replace("^","\\^")					
					.replace("$","\\$")
					.replace("|","\\|");
		}
	}

	@Override
	public String toRegexString() {
		return "" + literal;
	}

	@Override
	public List<PartialRegex> getRegexParts() {
		List<PartialRegex> result = new ArrayList<PartialRegex>(1);
		result.add(this);
		return result;
	}

}
