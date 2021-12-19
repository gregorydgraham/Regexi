/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

import nz.co.gregs.regexi.Regex;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the type returned by {@link #endRange() }
 * @deprecated use the {@link CharacterSetExcluding} and
 * {@link CharacterSetIncluding} instead
 */
@Deprecated
public class RangeBuilder<REGEX extends AbstractHasRegexFunctions<REGEX>> {

	private final REGEX origin;
	private String literals = "";
	private boolean negated = false;
	private boolean includeMinus = false;
	private boolean includeOpenBracket = false;
	private boolean includeCloseBracket = false;

	public RangeBuilder(REGEX original) {
		this.origin = original;
	}

	private RangeBuilder(REGEX original, Character lowest, Character highest) {
		this(original);
		addRange(lowest, highest);
	}

	private RangeBuilder(REGEX original, String literals) {
		this(original);
		addLiterals(literals);
	}

	public final RangeBuilder<REGEX> addRange(Character lowest, Character highest) {
		this.literals += lowest + "-" + highest;
		return this;
	}

	public final RangeBuilder<REGEX> addLiterals(String literals) {
		this.literals += literals.replaceAll("-", "").replaceAll("]", "");
		this.includeMinus = this.includeMinus || literals.contains("-");
		this.includeOpenBracket = includeOpenBracket || literals.contains("[");
		this.includeCloseBracket = includeCloseBracket || literals.contains("]");
		return this;
	}

	public RangeBuilder<REGEX> not() {
		this.negated = true;
		return this;
	}

	public RangeBuilder<REGEX> negated() {
		return not();
	}

	public RangeBuilder<REGEX> includeMinus() {
		includeMinus = true;
		return this;
	}

	public RangeBuilder<REGEX> and(Character lowest, Character highest) {
		return addRange(lowest, highest);
	}

	public RangeBuilder<REGEX> and(String literals) {
		return addLiterals(literals);
	}

	public RangeBuilder<REGEX> excluding(Character lowest, Character highest) {
		excluding(new RangeBuilder<>(Regex.startingAnywhere(), lowest, highest));
		return this;
	}

	public RangeBuilder<REGEX> excluding(String literals) {
		excluding(new RangeBuilder<>(Regex.startingAnywhere(), literals));
		return this;
	}

	public RangeBuilder<REGEX> excluding(RangeBuilder<?> newRange) {
		this.literals = this.literals + "-" + newRange.encloseInBrackets();
		return this;
	}

	public String encloseInBrackets() {
		return "[" + (negated ? "^" : "") + (includeMinus ? "-" : "") + (includeOpenBracket ? "\\[" : "") + (includeCloseBracket ? "\\]" : "") + literals + "]";
	}

	public REGEX endRange() {
		return origin.add(new UnescapedSequence(encloseInBrackets()));
	}

}
