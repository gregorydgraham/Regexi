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
public class SingleCharacter extends Regex {
	
	private final Character literal;

	protected SingleCharacter(Character character) {
		this.literal = character;
	}

	@Override
	public String getRegex() {
		return "" + literal;
	}
	
}
