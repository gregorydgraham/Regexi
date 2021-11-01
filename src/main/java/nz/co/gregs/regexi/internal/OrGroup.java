/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

import nz.co.gregs.regexi.internal.HasRegexFunctions;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the type returned by {@link #endOrGroup() }
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
		ors.add(getCurrent().toRegexString());
		return new OrGroup<>(getOrigin(), ors);
	}

	public REGEX endOrGroup() {
		return endGroup();
	}

	@Override
	public String toRegexString() {
		final String regexp = getCurrent().toRegexString();
		ors.add(regexp);
		String result = "";
		for (String or : ors) {
			result += (result.isEmpty() ? "" : "|") + or;
		}
		if (result.isEmpty()) {
			return result;
		} else {
			return "(" + result + ")";
		}
	}

}
