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
 * @param <REGEX> the type returned by {@link #endOrGroup() }
 */
public class Group<REGEX extends HasRegexFunctions<REGEX>> extends RegexGroup<Group<REGEX>, REGEX> {
	

	protected Group(REGEX original) {
		super(original);
	}

	protected Group(REGEX original, List<String> previousOptions) {
		super(original);
	}

	@Override
	public String getRegex() {
		final String regexp = getCurrent().getRegex();
		return "("+regexp+")";
	}

	@Override
	public REGEX endGroup() {
		return super.endGroup();
	}
	
}
