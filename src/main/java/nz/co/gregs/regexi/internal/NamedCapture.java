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
 * @param <REGEX> the type return by {@link #endNamedCapture() }
 */
public class NamedCapture<REGEX extends AbstractHasRegexFunctions<REGEX>> extends RegexGroup<NamedCapture<REGEX>, REGEX> {

	private final String name;

	protected NamedCapture(REGEX original, String name) {
		super(original);
		this.name = name;
		original.registerNamedGroup(name);
	}

	@Override
	public String toRegexString() {
		final String regexp = getCurrent().toRegexString();
		return "(?<" + name + ">" + regexp + ")";
	}

	public REGEX endNamedCapture() {
		return endGroup();
	}

	@Override
	public List<String> getNamedGroups() {
		ArrayList<String> arrayList = new ArrayList<String>(1);
		arrayList.add(name);
		return arrayList;
	}

}
