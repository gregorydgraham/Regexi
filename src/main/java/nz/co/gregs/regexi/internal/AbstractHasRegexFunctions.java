/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.co.gregs.regexi.internal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the PartialRegex to return
 */
public abstract class AbstractHasRegexFunctions<REGEX extends AbstractHasRegexFunctions<REGEX>> implements HasRegexFunctions<REGEX> {

	private final List<String> namedGroups = new ArrayList<String>(0);

	protected void registerNamedGroup(String name) {
		this.namedGroups.add(name);
	}

	protected final void registerAllNamedGroups(List<String> names) {
		for (String name : names) {
			registerNamedGroup(name);
		}
	}

	@Override
	public List<String> getNamedGroups() {
		return new ArrayList<String>(this.namedGroups);
	}
}
