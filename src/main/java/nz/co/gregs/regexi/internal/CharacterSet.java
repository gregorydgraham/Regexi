/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.co.gregs.regexi.internal;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the class returned by {@link #endSet() }
 */
public abstract class CharacterSet<REGEX extends AbstractHasRegexFunctions<REGEX>> {

	protected final REGEX origin;
	protected String literals = "";
	protected boolean includeMinus = false;
	protected boolean includeOpenBracket = false;
	protected boolean includeCloseBracket = false;
	protected boolean includeCaret = false;
	protected boolean includeBackslash = false;
	private final boolean exclude;

	protected CharacterSet(REGEX regex) {
		this(regex, false);
	}

	protected CharacterSet(REGEX regex, boolean exclude) {
		this.origin = regex;
		this.exclude = exclude;
	}

	protected CharacterSet<REGEX> extendWithRange(Character lowest, Character highest) {
		this.literals += lowest + "-" + highest;
		return this;
	}

	protected CharacterSet<REGEX> extendWithLiterals(String literals1) {
		this.literals += literals1.replaceAll("-", "").replaceAll("]", "");
		this.includeMinus = this.includeMinus || literals1.contains("-");
		this.includeOpenBracket = includeOpenBracket || literals1.contains("[");
		this.includeCloseBracket = includeCloseBracket || literals1.contains("]");
		this.includeCaret = includeCaret || literals1.contains("^");
		this.includeBackslash = includeBackslash || literals1.contains("\\");
		return this;
	}

	protected CharacterSet<REGEX> extendWithLiteral(Character literal) {
		return extendWithLiterals(""+literal);
	}

	protected CharacterSet<REGEX> extendWithHyphen() {
		includeMinus = true;
		return this;
	}

	protected CharacterSet<REGEX> extendWithCloseBracket() {
		includeCloseBracket = true;
		return this;
	}

	protected CharacterSet<REGEX> extendWithCaret() {
		includeCaret = true;
		return this;
	}

	protected CharacterSet<REGEX> extendWithBackslash() {
		includeBackslash = true;
		return this;
	}

	public CharacterSet<REGEX> and(Character lowest, Character highest) {
		return extendWithRange(lowest, highest);
	}

	public CharacterSet<REGEX> and(String literals) {
		return extendWithLiterals(literals);
	}

	public final String encloseInBrackets() {
		return "[" + (exclude ? "^" : "") + (includeMinus ? "-" : "") + (includeOpenBracket ? "\\[" : "") + (includeCloseBracket ? "\\]" : "") + (includeCaret ? "\\^" : "") + (includeBackslash ? "\\\\" : "") + literals + "]";
	}

	public REGEX endSet() {
		return origin.add(new UnescapedSequence(encloseInBrackets()));
	}

}
