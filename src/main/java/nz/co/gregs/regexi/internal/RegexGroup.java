/*
 * Copyright 2020 Gregory Graham.
 *
 * Commercial licenses are available, please contact info@gregs.co.nz for details.
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/ 
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * You are free to:
 *     Share - copy and redistribute the material in any medium or format
 *     Adapt - remix, transform, and build upon the material
 * 
 *     The licensor cannot revoke these freedoms as long as you follow the license terms.               
 *     Under the following terms:
 *                 
 *         Attribution - 
 *             You must give appropriate credit, provide a link to the license, and indicate if changes were made. 
 *             You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
 *         NonCommercial - 
 *             You may not use the material for commercial purposes.
 *         ShareAlike - 
 *             If you remix, transform, or build upon the material, 
 *             you must distribute your contributions under the same license as the original.
 *         No additional restrictions - 
 *             You may not apply legal terms or technological measures that legally restrict others from doing anything the 
 *             license permits.
 * 
 * Check the Creative Commons website for any details, legalese, and updates.
 */
package nz.co.gregs.regexi.internal;

import nz.co.gregs.regexi.internal.PartialRegex;
import nz.co.gregs.regexi.internal.HasRegexFunctions;
import java.util.List;
import nz.co.gregs.regexi.Regex;

/**
 *
 * @author gregorygraham
 * @param <THIS> the class of this PartialRegex, returned by most methods to
 * maintain type safety
 * @param <REGEX> the regex to return to after ending this group
 */
public abstract class RegexGroup<THIS extends RegexGroup<THIS, REGEX>, REGEX extends HasRegexFunctions<REGEX>> implements HasRegexFunctions<THIS> {

	private final REGEX origin;
	private PartialRegex current = Regex.empty();

	public RegexGroup(REGEX original) {
		this.origin = original;
	}

	@Override
	public List<PartialRegex> getRegexParts() {
		return getCurrent().getRegexParts();
	}

	/**
	 * @return the current
	 */
	public PartialRegex getCurrent() {
		return current;
	}

	/**
	 * @return the origin
	 */
	public REGEX getOrigin() {
		return origin;
	}

	protected REGEX endGroup() {
		return getOrigin().unescaped(this.toRegexString());
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS add(PartialRegex second) {
		current = getCurrent().add(second);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS addGroup(PartialRegex second) {
		current = getCurrent().addGroup(second);
		return (THIS) this;
	}
}
