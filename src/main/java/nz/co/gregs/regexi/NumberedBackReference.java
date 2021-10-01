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
public class NumberedBackReference extends Regex {

	private final String literal;

	public NumberedBackReference(int number) {
		// \number e.g. \5
		this.literal = "\\" + number;
	}

	@Override
	public String getRegex() {
		return "" + literal;
	}

}
