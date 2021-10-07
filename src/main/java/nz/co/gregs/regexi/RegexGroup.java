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
package nz.co.gregs.regexi;

import java.util.List;

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
		return getOrigin().unescaped(this.getRegex());
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS notPrecededBy(String literalValue) {
		current = getCurrent().notPrecededBy(literalValue);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS noneOfTheseCharacters(String literals) {
		current = getCurrent().noneOfTheseCharacters(literals);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS noCharacterBetween(Character lowest, Character highest) {
		current = getCurrent().noCharacterBetween(lowest, highest);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS anyCharacterBetween(Character lowest, Character highest) {
		current = getCurrent().anyCharacterBetween(lowest, highest);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS anyCharacterIn(String literals) {
		current = getCurrent().anyCharacterIn(literals);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS anyOf(String literal, String... literals) {
		current = current.anyOf(literal, literals);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS number() {
		current = getCurrent().number();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS integer() {
		current = getCurrent().integer();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS positiveInteger() {
		current = getCurrent().positiveInteger();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS negativeInteger() {
		current = getCurrent().negativeInteger();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS capture(PartialRegex regexp) {
		current = getCurrent().capture(regexp);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS space() {
		current = getCurrent().space();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS nonWhitespace() {
		current = getCurrent().nonWhitespace();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS whitespace() {
		current = getCurrent().whitespace();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS nonWordCharacter() {
		current = getCurrent().nonWordCharacter();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS gapBetweenWords() {
		current = getCurrent().gapBetweenWords();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS word() {
		current = getCurrent().word();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS wordCharacter() {
		current = getCurrent().wordCharacter();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS nondigits() {
		current = getCurrent().nondigits();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS nondigit() {
		current = getCurrent().nondigit();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS digits() {
		current = getCurrent().digits();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS digit() {
		current = getCurrent().digit();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS endOfTheString() {
		current = getCurrent().endOfTheString();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS optionalMany() {
		current = getCurrent().optionalMany();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS zeroOrMore() {
		current = getCurrent().zeroOrMore();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS oneOrMore() {
		current = getCurrent().oneOrMore();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS atLeastOnce() {
		current = getCurrent().atLeastOnce();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS onceOrNotAtAll() {
		current = getCurrent().onceOrNotAtAll();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS atLeastXAndNoMoreThanYTimes(int x, int y) {
		current = getCurrent().atLeastXAndNoMoreThanYTimes(x, y);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS atLeastThisManyTimes(int x) {
		current = getCurrent().atLeastThisManyTimes(x);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS once() {
		current = getCurrent().once();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS anyCharacter() {
		current = getCurrent().anyCharacter();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS controlCharacter(String x) {
		current = getCurrent().controlCharacter(x);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS escapeCharacter() {
		current = getCurrent().escapeCharacter();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS bell() {
		current = getCurrent().bell();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS formfeed() {
		current = getCurrent().formfeed();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS carriageReturn() {
		current = getCurrent().carriageReturn();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CaseInsensitiveSection<THIS> beginCaseInsensitiveSection() {
		return new CaseInsensitiveSection<>((THIS) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS newline() {
		current = getCurrent().newline();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS tab() {
		current = getCurrent().tab();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS backslash() {
		current = getCurrent().backslash();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS literal(String literals) {
		current = getCurrent().literal(literals);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS literal(Character character) {
		current = getCurrent().literal(character);
		return (THIS) this;
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

	@Override
	@SuppressWarnings("unchecked")
	public THIS wordBoundary() {
		current = getCurrent().wordBoundary();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS nonWordBoundary() {
		current = getCurrent().nonWordBoundary();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS theBeginningOfTheInput() {
		current = getCurrent().theBeginningOfTheInput();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS theEndOfThePreviousMatch() {
		current = getCurrent().theEndOfThePreviousMatch();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS theEndOfTheInput() {
		current = getCurrent().theEndOfTheInput();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS theEndOfTheInputButForTheFinalTerminator() {
		current = getCurrent().theEndOfTheInputButForTheFinalTerminator();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS notPrecededBy(PartialRegex literalValue) {
		current = getCurrent().notPrecededBy(literalValue);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS notFollowedBy(String literalValue) {
		current = getCurrent().notFollowedBy(literalValue);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS notFollowedBy(PartialRegex literalValue) {
		current = getCurrent().notFollowedBy(literalValue);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS numberLike() {
		current = getCurrent().numberLike();
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS literalCaseInsensitive(String literals) {
		current = getCurrent().literalCaseInsensitive(literals);
		return (THIS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public THIS unescaped(String unescapedSequence) {
		current = getCurrent().unescaped(unescapedSequence);
		return (THIS) this;
	}
}
