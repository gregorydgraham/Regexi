/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

/**
 *
 * @author gregorygraham
 */
public class LiteralSequence extends Regex {
	
	private final String literal;

	public LiteralSequence(String literals) {
		if (literals == null) {
			this.literal = "";
		} else {
			this.literal = literals.replaceAll("\\\\", "\\").replaceAll("\\.", "\\.").replaceAll("\\?", "\\?").replaceAll("\\+", "\\\\+").replaceAll("\\*", "\\*").replaceAll("\\^", "\\^").replaceAll("\\$", "\\$").replaceAll("\\|", "\\|").replaceAll("\\[", "\\[").replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
		}
	}

	@Override
	public String getRegex() {
		return "" + literal;
	}
	
}
