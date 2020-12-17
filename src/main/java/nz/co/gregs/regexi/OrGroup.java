/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.separatedstring.SeparatedString;

/**
 *
 * @author gregorygraham
 * @param <REGEX>
 */
public class OrGroup<REGEX extends HasRegexFunctions<REGEX>> extends RegexGroup<OrGroup<REGEX>, REGEX> {
	
	private final List<String> ors = new ArrayList<>(0);

	protected OrGroup(REGEX original) {
		super(original);
	}

	protected OrGroup(REGEX original, List<String> previousOptions) {
		super(original);
		ors.addAll(previousOptions);
	}

	public OrGroup<REGEX> or() {
		ors.add(getCurrent().getRegex());
		return new OrGroup<>(getOrigin(), ors);
	}

	public REGEX endOrGroup() {
		return endGroup();
	}

	@Override
	public String getRegex() {
		final String regexp = getCurrent().getRegex();
		ors.add(regexp);
		final SeparatedString groupedString = SeparatedString.of(ors).separatedBy("|").withPrefix("(").withSuffix(")");
		return groupedString.toString();
	}
	
}
