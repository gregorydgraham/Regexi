/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.util.List;

/**
 *
 * @author gregorygraham
 */
class RegexpCombination extends Regex {

	private final HasRegexFunctions<?> first;
	private final HasRegexFunctions<?> second;

	protected RegexpCombination(HasRegexFunctions<?> first, HasRegexFunctions<?> second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String getRegex() {
		return first.getRegex() + second.getRegex();
	}

	@Override
	public List<String> testAgainst(String testStr) {
		List<String> strings = first.testAgainst(testStr);
		strings.addAll(second.testAgainst(testStr));
		strings.addAll(super.testAgainst(testStr));
		return strings;
	}
}
