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
public class NamedBackReference extends Regex {

	private final String literal;

	public NamedBackReference(String name) {
		if (name == null) {
			this.literal = "";
		} else {
			// \k<name>
			this.literal = "\\k<" + name + ">";
		}
	}

	@Override
	public String getRegex() {
		return "" + literal;
	}

}
