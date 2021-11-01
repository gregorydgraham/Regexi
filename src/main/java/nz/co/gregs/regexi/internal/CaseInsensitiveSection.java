/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the regex to return after closing this class, the type
 returned by {@link #endCaseInsensitiveSection() }
 */
public class CaseInsensitiveSection<REGEX extends HasRegexFunctions<REGEX>> extends RegexGroup<CaseInsensitiveSection<REGEX>, REGEX> {

	public CaseInsensitiveSection(REGEX original) {
		super(original);
	}

	@Override
	public String toRegexString() {
		final String regexp = getCurrent().toRegexString();
		return "((?i)" + regexp + "(?-i))";
	}

	public REGEX endCaseInsensitiveSection() {
		return endGroup();
	}
}
