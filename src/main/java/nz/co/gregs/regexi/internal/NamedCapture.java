/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

import nz.co.gregs.regexi.internal.HasRegexFunctions;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the type return by {@link #endNamedCapture() }
 */
public class NamedCapture<REGEX extends HasRegexFunctions<REGEX>> extends RegexGroup<NamedCapture<REGEX>, REGEX> {
	
	private final String name;

	protected NamedCapture(REGEX original, String name) {
		super(original);
		this.name = name;
	}

	@Override
	public String getRegex() {
		final String regexp = getCurrent().getRegex();
		return "(?<" + name + ">" + regexp + ")";
	}

	public REGEX endNamedCapture() {
		return endGroup();
	}
	
}
