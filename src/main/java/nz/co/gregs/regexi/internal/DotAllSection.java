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
 returned by {@link #endDotAllSection() }
 */
public class DotAllSection<REGEX extends AbstractHasRegexFunctions<REGEX>> extends RegexGroup<DotAllSection<REGEX>, REGEX> {

	public DotAllSection(REGEX original) {
		super(original);
	}

	@Override
	public String toRegexString() {
		final String regexp = getCurrent().toRegexString();
		return "((?s)" + regexp + "(?-s))";
	}

	public REGEX endDotAllSection() {
		return endGroup();
	}
}
