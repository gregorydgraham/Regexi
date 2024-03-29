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
public class UnescapedSequence extends PartialRegex {

	private final String literal;

	public UnescapedSequence(String literals) {
		if (literals == null) {
			this.literal = "";
		} else {
			this.literal = literals;
		}
	}

	@Override
	public String toRegexString() {
		return literal;
	}

	@Override
	public List<PartialRegex> getRegexParts() {
		ArrayList<PartialRegex> arrayList = new ArrayList<PartialRegex>(1);
		if (literal != null) {
			if (!literal.isEmpty()) {
				arrayList.add(this);
			}
		}
		return arrayList;
	}
}
