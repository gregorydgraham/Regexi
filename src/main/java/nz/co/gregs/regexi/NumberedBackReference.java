/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gregorygraham
 */
public class NumberedBackReference extends PartialRegex {

	private final String literal;

	public NumberedBackReference(int number) {
		// \number e.g. \5
		this.literal = "\\" + number;
	}

	@Override
	public String getRegex() {
		return literal;
	}

	@Override
	public List<PartialRegex> getRegexParts() {
		List<PartialRegex> result = new ArrayList<>(1);
		result.add(this);
		return result;
	}

}
