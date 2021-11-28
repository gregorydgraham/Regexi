/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the type returned by {@link #endSet() }
 */
public class CharacterSetIncluding<REGEX extends HasRegexFunctions<REGEX>> extends CharacterSet<REGEX> {

	public CharacterSetIncluding(REGEX regex) {
		super(regex);
	}

	public final CharacterSetIncluding<REGEX> includeRange(Character lowest, Character highest) {
		return (CharacterSetIncluding<REGEX>) extendWithRange(lowest, highest);
	}

	public final CharacterSetIncluding<REGEX> includeLiterals(String literals) {
		return (CharacterSetIncluding<REGEX>) extendWithLiterals(literals);
	}

	public CharacterSetIncluding<REGEX> includeMinus() {
		return (CharacterSetIncluding<REGEX>) extendWithHyphen();
	}

	public CharacterSetIncluding<REGEX> includeBackslash() {
		return (CharacterSetIncluding<REGEX>) extendWithBackslash();
	}

	public CharacterSetIncluding<REGEX> includeCaret() {
		return (CharacterSetIncluding<REGEX>) extendWithCaret();
	}

	public CharacterSetIncluding<REGEX> includeCloseBracket() {
		return (CharacterSetIncluding<REGEX>) extendWithCloseBracket();
	}

	public CharacterSetIncluding<REGEX> includeDigits() {
		return includeRange('0', '9');
	}

	public CharacterSetIncluding<REGEX> includeUppercase() {
		return includeRange('A', 'Z');
	}

	public CharacterSetIncluding<REGEX> includeLowercase() {
		return includeRange('a', 'z');
	}

	public CharacterSetIncluding<REGEX> includeLetters() {
		return includeUppercase().includeLowercase();
	}

	public CharacterSetIncluding<REGEX> includeDot() {
		return includeLiterals(".");
	}
}
