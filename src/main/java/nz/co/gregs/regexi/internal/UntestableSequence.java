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
public class UntestableSequence extends UnescapedSequence {

	public UntestableSequence(String literals) {
		super(literals);
	}

	@Override
	public List<PartialRegex> getRegexParts() {
		ArrayList<PartialRegex> arrayList = new ArrayList<PartialRegex>(0);
		return arrayList;
	}
}
