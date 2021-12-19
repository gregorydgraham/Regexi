/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the type returned by {@link #endGroup() }
 */
public class NegativeLookahead<REGEX extends AbstractHasRegexFunctions<REGEX>> extends RegexGroup<NegativeLookahead<REGEX>, REGEX> {
	

	protected NegativeLookahead(REGEX original) {
		super(original);
	}

	@Override
	public String toRegexString() {
		final String regexp = getCurrent().toRegexString();
		return "(?!"+regexp+")";
	}

	public REGEX endLookahead() {
		return super.endGroup();
	}
	
}
