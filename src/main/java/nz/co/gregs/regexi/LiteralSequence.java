/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

/**
 * Implements literal sequences by replacing all protected characters as required.
 *
 * @author gregorygraham
 */
public class LiteralSequence extends Regex {
	
	private final String literal;

	/**
	 * Creates a new literal sequence by replacing all protected characters as required.
	 * @param literals 
	 */
	public LiteralSequence(String literals) {
		if (literals == null) {
			this.literal = "";
		} else {
			this.literal = literals
					.replaceAll("\\\\", "\\")
					.replaceAll("\\+", "\\\\+")
					.replaceAll("\\(", "\\\\(")
					.replaceAll("\\)", "\\\\)")
					.replaceAll("\\[", "\\\\[")
					.replaceAll("\\.", "\\\\.")
					.replaceAll("\\?", "\\\\?")
					.replaceAll("\\*", "\\\\*")
					.replaceAll("\\^", "\\\\\\^")
					.replaceAll("\\$", "\\\\\\$")
					.replaceAll("\\|", "\\|")
					;
		}
	}

	@Override
	public String getRegex() {
		return "" + literal;
	}
	
}
