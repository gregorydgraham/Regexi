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

import java.util.List;
import nz.co.gregs.regexi.Regex;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the PartialRegex to return to after ending the group
 */
public interface HasRegexFunctions<REGEX extends HasRegexFunctions<REGEX>> {

	/**
	 * Converts the Regex into a string.
	 *
	 * <p>
	 * For instance
	 * {@code PartialRegex.startingAnywhere().charactersWrappedBy("<<",">>").once()}
	 * might produce {@code "(<<(((?!>>).)*)>>){1}" }
	 *
	 * @return the PartialRegex converted to a string pattern
	 */
	public String toRegexString();

	/**
	 * Return a list of partial regex patterns in this regex pattern
	 *
	 * @return a list of sub patterns
	 */
	public List<PartialRegex> getRegexParts();

	/**
	 * Adds the regular expression to the end of current expression without
	 * grouping it.
	 *
	 * <p>
	 * Not grouping the added regular expression can produce counter-intuitive
	 * results and breaks encapsulation so use it carefully. In Particular
	 * add(myRegex).onceOrNotAtAll() will only apply the "onceOrNotAtAll" to last
	 * element of myRegex and not the entire expression. Using
	 * digit.add(PartialRegex.startAnywhere().dot().digits()).onceOrNotAtAll()
	 * will match "0." and "0.5" but not "0". If you want grouping use addGroup()
	 * instead.
	 *
	 * <p>
	 * For example
	 * PartialRegex.startingAnywhere().add(allowedValue).add(separator) will
	 * addGroup the "separator" regular expression to the "allowedValue"
	 * expression (the rest of the instruction adds nothing). Assuming that
	 * allowedValue is "[0-9]" and separator is ":", the full regexp will be
	 * "[0-9]:".
	 *
	 * @param second the regular expression to add this regular expression with
	 * @return a new regular expression consisting of the current expression and
	 * the supplied expression added together
	 */
	REGEX add(PartialRegex second);

	/**
	 * Adds the regular expression to the end of current expression as a new
	 * group.
	 *
	 * <p>
	 * For example
	 * PartialRegex.startingAnywhere().addGroup(allowedValue).addGroup(separator)
	 * will addGroup the "separator" regular expression to the "allowedValue"
	 * expression (the rest of the instruction adds nothing). Assuming that
	 * allowedValue is "[0-9]" and separator is ":", the full regexp will be
	 * "([0-9])(:)".
	 *
	 * @param newGroup the regular expression to addGroup to this regular
	 * expression
	 * @return a new regular expression consisting of the current expression and
	 * the supplied expression added together
	 */
	REGEX addGroup(PartialRegex newGroup);

	/**
	 * Adds a check for a positive or negative integer to the regular expression
	 * without grouping.
	 *
	 * <p>
	 * Will capture the plus or minus so watch out for that in your calculator
	 * application.
	 *
	 * @return a new regexp
	 */
	public default REGEX integer() {
		return addGroup(PartialRegex.INTEGER_REGEX);
	}

	/**
	 * Adds a standard pattern that will match any valid number to the pattern as
	 * a grouped element.
	 *
	 * <p>
	 * A valid number is any sequence of digits not starting with zero, optionally
	 * preceded with a plus or minus, and optionally followed by a decimal point
	 * and a sequence of digits, that is clearly separated from other characters.
	 *
	 * <p>
	 * A number may be followed by characters as "12tonnes" is a valid number.
	 *
	 * <p>
	 * Scientific notation is not supported but " 2E16" will match as "2" is a
	 * valid number by this definition.
	 *
	 * <p>
	 * An example of a valid number would be +2.345.
	 *
	 * <p>
	 * Invalid numbers include 02.345, A4, _234, 2*E10, and 5678ABC.
	 *
	 * @return the current regex with a number matching pattern added to it
	 */
	public default REGEX number() {
		return add(Regex.startingAnywhere()
				.beginGroup()
				.integer()
				.addGroup(Regex.startingAnywhere()
						.literal(".").once()
						.digits()
				).onceOrNotAtAll()
				.endGroup()
		);
	}

	/**
	 * Adds a standard pattern that will match any number-like sequence to the
	 * pattern as a grouped element.
	 *
	 * <p>
	 * A number-like sequence is any sequence of digits, optionally preceded with
	 * a plus or minus, and optionally followed by a decimal point and a sequence
	 * of digits.
	 *
	 * <p>
	 * It differs from a number in that zero can be the first digit and the
	 * sequence doesn't need to be clearly separated from the surrounding
	 * characters.
	 *
	 * <p>
	 * It differs from digits in that leading +/- and a middle decimal point are
	 * included.
	 *
	 * <p>
	 * A valid match would occur for the following +2.345, 02.345, A4, _234,
	 * _234.5, 2*E10, and 5678ABC.
	 *
	 * @return the current regex with a number matching pattern added to it
	 */
	public default REGEX numberLike() {
		return add(Regex.startOrGroup()
				.anyCharacterIn("-+").onceOrNotAtAll()
				.digit().atLeastOnce().notFollowedBy(Regex.startingAnywhere().digit())
				.addGroup(Regex.startingAnywhere()
						.literal(".").once()
						.digit().oneOrMore().notFollowedBy(Regex.startingAnywhere().digit())
				).onceOrNotAtAll()
				.endGroup()
		);
	}

	/**
	 * Adds a standard pattern that will match any valid number to the pattern as
	 * a grouped element.
	 *
	 * <p>
	 * A valid number is any sequence of digits not starting with zero, optionally
	 * preceded with a plus or minus, and optionally followed by a decimal point
	 * and a sequence of digits, that is clearly separated from other characters.
	 *
	 * <p>
	 * Examples of a valid number would be +2.345, 2E10, -2.89E-7.98, or 1.37e+15.
	 *
	 * <p>
	 * Invalid numbers include 02.345, A4, _234, 2*E10, and 5678ABC.
	 *
	 * @return the current regex with a number matching pattern added to it
	 */
	public default REGEX numberIncludingScientificNotation() {
		return addGroup(Regex.startingAnywhere()
				// it's just a number
				.number().once()
				.beginGroup()
				// possibly followed by an E
				.anyCharacterIn("Ee")
				// followed by another number (but without the wordboundary in the middle)
				.anyCharacterIn("-+").onceOrNotAtAll()
				.beginOrGroup()
				.anyCharacterBetween('1', '9').atLeastOnce()
				.digit().zeroOrMore()
				.or().literal('0').oneOrMore().notFollowedBy(Regex.startingAnywhere().digit())
				.endOrGroup().once()
				.beginGroup()
				.literal(".").once()
				.digits()
				.endGroup().onceOrNotAtAll()
				.endGroup().onceOrNotAtAll()
		);
	}

	/**
	 * Adds a standard pattern that will match any valid number to the pattern as
	 * a grouped element.
	 *
	 * <p>
	 * A valid number is any sequence of digits not starting with zero, optionally
	 * preceded with a plus or minus, and optionally followed by a decimal point
	 * and a sequence of digits, that is clearly separated from other characters.
	 *
	 * <p>
	 * Examples of a valid number would be +2.345, 2E10, -2.89E-7.98, or 1.37e+15.
	 *
	 * <p>
	 * Invalid numbers include 02.345, A4, _234, 2*E10, and 5678ABC.
	 *
	 * @return the current regex with a number matching pattern added to it
	 */
	public default REGEX numberLikeIncludingScientificNotation() {
		return addGroup(Regex.startingAnywhere()
				// it's just a number
				.numberLike().once()
				.beginGroup()
				// possibly followed by an E
				.anyCharacterIn("Ee")
				// followed by another number (but without the wordboundary in the middle)
				.numberLike()
				.endGroup().onceOrNotAtAll()
		);
	}

	/**
	 * Adds a check for a simple range to the regular expression without grouping.
	 *
	 * <p>
	 * To addGroup more complex ranges use .addGroup(new
	 * PartialRegex.Range(lowest, highest)).
	 *
	 * @param lowest the (inclusive) start of the character range
	 * @param highest the (inclusive) end of the character range
	 * @return a new regexp
	 */
	default REGEX anyCharacterBetween(Character lowest, Character highest) {
		return add(Regex.startingAnywhere().range(lowest, highest));
	}

	/**
	 * Adds a match for any single character to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	default REGEX anyCharacter() {
		return add(new UntestableSequence("."));
	}

	/**
	 * Adds a match for zero or more characters to the regexp without grouping it.
	 *
	 * <p>
	 * This is the equivalent of adding ".*" to the Pattern.</p>
	 *
	 * @return a new regexp
	 */
	default REGEX anything() {
		return this.anyCharacter().optionalMany();
	}

	/**
	 * Adds a check for a simple range to the regular expression without grouping.
	 *
	 * <p>
	 * To addGroup more complex ranges use .addGroup(new
	 * PartialRegex.Range(rangeItems)).
	 *
	 * @param literals all the characters to be included in the range, for example
	 * "abcdeABCDE"
	 * @return a new regexp
	 */
	default REGEX anyCharacterIn(String literals) {
		return range(literals);
	}

	/**
	 * adds the literals as a series of options.
	 *
	 * <p>
	 * For instance
	 * {@code PartialRegex.startingAnywhere().anyOf("Amy", "Bob", "Charlie")}
	 * should produce "(Amy|Bob|Charlie)".</p>
	 *
	 * @param literal the first string
	 * @param literals the subsequent strings
	 * @return the regex extended with the strings as options for the next match.
	 */
	default REGEX anyOf(String literal, String... literals) {
		OrGroup<REGEX> temp = beginOrGroup().literal(literal);
		for (String literal1 : literals) {
			temp = temp.or().literal(literal1);
		}
		return temp.endOrGroup();
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position exactly once or not at all.
	 *
	 * <p>
	 * literal('a').literal('b)'.atLeastOnce() will match "ab" or "abb", but not
	 * "a"
	 *
	 * @return a new regexp
	 */
	default REGEX atLeastOnce() {
		return unescaped("+");
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position X times or more.
	 *
	 * @param x the minimum number of times the previous match must occur
	 * @return a new regexp
	 */
	default REGEX atLeastThisManyTimes(int x) {
		return unescaped("{" + x + ",}");
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position X or more times but no more than Y times.
	 *
	 * <p>
	 * literal('a').atLeastXAndNoMoreThanYTimes(2,3) will match "aa" and "aaa" but
	 * not "aa" nor "aaaa".
	 *
	 * @param x the minimum number of times the previous match must occur
	 * @param y the maximum number of times the previous match must occur
	 * @return a new regexp
	 */
	default REGEX atLeastXAndNoMoreThanYTimes(int x, int y) {
		return add(new UntestableSequence("{" + x + "," + y + "}"));
	}

	/**
	 * Adds a literal backslash(\) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	default REGEX backslash() {
		return unescaped("\\\\");
	}

	/**
	 * Adds a bell character(\a) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	default REGEX bell() {
		return unescaped("\\a");
	}

	/**
	 * Places the regular expression in a capturing group.
	 *
	 * <p>
	 * capturing and grouping are the same, there are methods of both names to
	 * capture the intent.</p>
	 *
	 * <p>
	 * You may want to use {@link #beginNamedCapture(java.lang.String)} as it is
	 * more robust and reliable method for most uses of capture.</p>
	 *
	 * @param regexp the regex to addGroup to this regex as a new capturing group
	 * @return a new regexp
	 */
	default REGEX capture(PartialRegex regexp) {
		return this.beginGroup().add(regexp).endGroup();
	}

	/**
	 * Adds a literal carriage return (\r).
	 *
	 * @return the regex extended with a carriage return.
	 */
	default REGEX carriageReturn() {
		return add(new UnescapedSequence("\\r"));
	}

	/**
	 * Extends the regular expression with group that ignores the whether letters
	 * are upper or lower case
	 *
	 * @return an extended regular expression
	 */
	@SuppressWarnings("unchecked")
	default CaseInsensitiveSection<REGEX> caseInsensitiveSection() {
		return beginCaseInsensitiveSection();
	}

	/**
	 * Extends the regular expression with group that ignores the whether letters
	 * are upper or lower case
	 *
	 * @return an extended regular expression
	 */
	@SuppressWarnings("unchecked")
	default CaseInsensitiveSection<REGEX> beginCaseInsensitiveSection() {
		return new CaseInsensitiveSection<>((REGEX) this);
	}

	/**
	 * Adds a control character(\cX) to the regexp without grouping it.
	 *
	 * @param x the control character
	 * @return a new regexp
	 */
	default REGEX controlCharacter(String x) {
		return add(new UnescapedSequence("\\c" + x));
	}

	/**
	 * Adds a check for a digit(0123456789) to the regular expression without
	 * grouping.
	 *
	 * @return a new regexp
	 */
	default REGEX digit() {
		return add(new UnescapedSequence("\\d"));
	}

	/**
	 * Adds a check for one or more digits to the regular expression as a grouped
	 * element.
	 *
	 * <p>
	 * Please note that digits is not the same as a valid integer or number, use {@link #positiveInteger() }, {@link #negativeInteger() }, {@link #integer()
	 * }, or {@link #number()} instead.
	 *
	 * @return a new regexp
	 */
	default REGEX digits() {
		return beginGroup().digit().oneOrMore().endGroup();
	}

	/**
	 * Adds a check for the end of the string to the regular expression without
	 * grouping.
	 *
	 * @return a new regexp
	 */
	default REGEX endOfInput() {
		return endOfTheString();
	}

	/**
	 * Adds a check for the end of the string to the regular expression without
	 * grouping.
	 *
	 * @return a new regexp
	 */
	default REGEX endOfTheString() {
		return add(new UnescapedSequence("$"));
	}

	/**
	 * Adds a escape character(\e) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	default REGEX escapeCharacter() {
		return add(new UnescapedSequence("\\e"));
	}

	/**
	 * Adds a formfeed character(\f) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	default REGEX formfeed() {
		return add(new UnescapedSequence("\\f"));
	}

	/**
	 * Adds a check for one or more non-word characters(\w) to the regular
	 * expression without grouping.
	 *
	 * <p>
	 * A word character is any letter A-Z, upper or lowercase, any digit, or the
	 * underscore character. A non-word character is any other character.
	 *
	 * @return a new regexp
	 */
	default REGEX gapBetweenWords() {
		return nonWordCharacter().oneOrMore();
	}

	/**
	 * Adds a literal string to the regexp grouping it so subsequent operators
	 * work on the entire literal.
	 *
	 *
	 * @param literals the literal value to addGroup to this regex
	 * @return a new regexp
	 */
	default REGEX literal(String literals) {
		return addGroup(new LiteralSequence(literals));
	}

	/**
	 * Adds a literal string to the regexp without grouping it.
	 *
	 *
	 * @param character the literal value to addGroup to this regex
	 * @return a new regexp
	 */
	default REGEX literal(Character character) {
		return add(new LiteralSequence("" + character));
	}

	/**
	 * adds the literal expression within a case insensitive section so that, for
	 * instance, "one", "One", and "ONE" would be equivalent.
	 *
	 * @param literals the literal character expected to found in the match
	 * @return this regular expression
	 */
	default REGEX literalCaseInsensitive(String literals) {
		return this.beginCaseInsensitiveSection().literal(literals).endCaseInsensitiveSection();
	}

	/**
	 * adds the literal expression within a case insensitive section so that, for
	 * instance, "one", "One", and "ONE" would be equivalent.
	 *
	 * @param literal the literal character expected to be found in the match
	 * @return this regular expression
	 */
	default REGEX literalCaseInsensitive(Character literal) {
		return literalCaseInsensitive("" + literal);
	}

	/**
	 * Adds a check for a negative integer to the regular expression without
	 * grouping.
	 *
	 * <p>
	 * Will capture the minus so watch out for that in your calculator
	 * application.
	 *
	 * @return this regexp extended with a negative integer
	 */
	default REGEX negativeInteger() {
		return addGroup(
				Regex.startingAnywhere()
						.literal('-')
						.beginOrGroup()
						.anyCharacterBetween('1', '9').once().digit().zeroOrMore()
						.or()
						.literal('0').notFollowedBy(Regex.startingAnywhere().digit())
						.endOrGroup()
		);
	}

	/**
	 * Adds a newline character(\n) to the regexp without grouping it.
	 *
	 * @return this regexp extended with a newline
	 */
	default REGEX newline() {
		return add(new UnescapedSequence("\\n"));
	}

	/**
	 * Starts a capturing group that is named.
	 *
	 * <p>
	 * Use similarly to
	 * {@code PartialRegex.empty().beginNamedCapture("unit").word().endNamedCapture()}</p>
	 *
	 * <p>
	 * Named captures can be used with named back references and can be retrieved
	 * with {@link PartialRegex#getAllNamedCapturesOfFirstMatchWithinString(java.lang.String)
	 * } and {@code Regex.getAllMatches(target).get(index).getAllNamedCaptures()}
	 * </p>
	 *
	 * @param name the name for this capture so it can be used in back references
	 * and processing of the parts of the match
	 * @return a NamedCapture regexp
	 */
	@SuppressWarnings("unchecked")
	public default NamedCapture<REGEX> namedCapture(String name) {
		return beginNamedCapture(name);
	}

	/**
	 * Starts a capturing group that is named.
	 *
	 * <p>
	 * Use similarly to
	 * {@code PartialRegex.empty().beginNamedCapture("unit").word().endNamedCapture()}</p>
	 *
	 * <p>
	 * Named captures can be used with named back references and can be retrieved
	 * with {@link PartialRegex#getAllNamedCapturesOfFirstMatchWithinString(java.lang.String)
	 * } and {@code Regex.getAllMatches(target).get(index).getAllNamedCaptures()}
	 * </p>
	 *
	 * @param name the name for this capture so it can be used in back references
	 * and processing of the parts of the match
	 * @return a NamedCapture regexp
	 */
	@SuppressWarnings("unchecked")
	public default NamedCapture<REGEX> beginNamedCapture(String name) {
		return new NamedCapture<REGEX>((REGEX) this, name);
	}

	/**
	 * Adds a check for a non-whitespace character(\\S) to the regular expression
	 * without grouping.
	 *
	 * <p>
	 * A whitespace character is [ \t\n\x0B\f\r], that is a space, tab, newline,
	 * char(11), form-feed, or carriage return. A non-whitespace character is
	 * anything else.
	 *
	 * @return a new regexp
	 */
	default REGEX nonWhitespace() {
		return add(new UnescapedSequence("\\S"));
	}

	/**
	 * Adds a check for a non-word boundary character(\\B) to the regular
	 * expression without grouping.
	 *
	 * @return a new regexp
	 */
	default REGEX nonWordBoundary() {
		return add(new UnescapedSequence("\\B"));
	}

	/**
	 * Adds a check for a non-word character(\W) to the regular expression without
	 * grouping.
	 *
	 * <p>
	 * A word character is any letter A-Z, upper or lowercase, any digit, or the
	 * underscore character. A non-word character is any other character.
	 *
	 * @return a new regexp
	 */
	default REGEX nonWordCharacter() {
		return add(new UnescapedSequence("\\W"));
	}

	/**
	 * Adds a check for anything other than a digit(0123456789) to the regular
	 * expression without grouping.
	 *
	 * @return a new regexp
	 */
	default REGEX nondigit() {
		return add(new UnescapedSequence("\\D"));
	}

	/**
	 * Adds a check for one or more of anything other than a digit(0123456789) to
	 * the regular expression without grouping.
	 *
	 * @return a new regexp
	 */
	default REGEX nondigits() {
		return nondigit().oneOrMore();
	}

	/**
	 * Adds a check to exclude a simple range from the regular expression without
	 * grouping.
	 *
	 * <p>
	 * To addGroup more complex ranges use .addGroup(new
	 * PartialRegex.Range(rangeItems)).
	 *
	 * @param literals all the characters to be included in the range, for example
	 * "abcdeABCDE"
	 * @return a new regexp
	 */
	default REGEX noneOfTheseCharacters(String literals) {
		return add(Regex.startingAnywhere().beginSetExcluding().excludeLiterals(literals).endSet());
	}

	/**
	 * Adds a check for a that the next element does not have the literal value
	 * immediately after it.
	 *
	 * <p>
	 * For instance to match words but not e-mail addresses you might use
	 * PartialRegex.startingAnywhere().word().notFollowedBy("@").
	 *
	 * @param literalValue the literal string that cannot come after this regex
	 * @return a new regexp
	 */
	default REGEX notFollowedBy(String literalValue) {
		return this.notFollowedBy(new LiteralSequence(literalValue));
	}

	/**
	 * Adds a check for a that the next element does not have the literal value
	 * immediately after it.
	 *
	 * <p>
	 * For instance to match words but not e-mail addresses you might use
	 * PartialRegex.startingAnywhere().word().notFollowedBy("@").
	 *
	 * @param literalValue the literal string that cannot come after this regex
	 * @return a new regexp
	 */
	default REGEX notFollowedBy(PartialRegex literalValue) {
		return this.unescaped("(?!").add(literalValue).unescaped(")");
	}

	/**
	 * Adds a check for a that the next element does not have the literal value
	 * before it.
	 *
	 * <p>
	 * For instance a positive integer is an integer that may have a plus in front
	 * of it but definitely isn't preceded by a minus. So it uses a notPrecededBy:
	 * startingAnywhere().notPrecededBy("-").plus().onceOrNotAtAll()...
	 *
	 * @param literalValue the literal string that cannot come before this regex
	 * @return a new regexp
	 */
	default REGEX notPrecededBy(String literalValue) {
		return this.notPrecededBy(new LiteralSequence(literalValue));
	}

	/**
	 * Adds a check for a that the next element does not have the literal value
	 * before it.
	 *
	 * <p>
	 * For instance a positive integer is an integer that may have a plus in front
	 * of it but definitely isn't preceded by a minus. So it uses a notPrecededBy:
	 * startingAnywhere().notPrecededBy("-").plus().onceOrNotAtAll()...
	 *
	 * @param literalValue the literal string that cannot come before this regex
	 * @return a new regexp
	 */
	default REGEX notPrecededBy(PartialRegex literalValue) {
		return negativeLookBehind().add(literalValue).endLookahead();
	}

	@SuppressWarnings("unchecked")
	default NegativeLookbehind<REGEX> negativeLookBehind() {
		return new NegativeLookbehind<REGEX>((REGEX) this);
	}

	@SuppressWarnings("unchecked")
	default PositiveLookbehind<REGEX> positiveLookBehind() {
		return new PositiveLookbehind<REGEX>((REGEX) this);
	}

	/**
	 * Adds a check to exclude a simple range from the regular expression without
	 * grouping.
	 *
	 * <p>
	 * To addGroup more complex ranges use .addGroup(new
	 * PartialRegex.Range(lowest, highest)).
	 *
	 * @param lowest the (inclusive) start of the character range
	 * @param highest the (inclusive) end of the character range
	 * @return a new regexp
	 */
	default REGEX noCharacterBetween(Character lowest, Character highest) {
		return add(Regex.startingAnywhere().beginSetExcluding().excludeRange(lowest, highest).endSet());
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position exactly once.
	 *
	 * @return a new regexp
	 */
	default REGEX once() {
		return add(new UntestableSequence("{1}"));
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position exactly once or not at all.
	 *
	 * <p>
	 * literal('a').literal('b)'.onceOrNotAtAll() will match "a" or "ab", but not
	 * "abb"
	 *
	 * @return a new regexp
	 */
	default REGEX onceOrNotAtAll() {
		return add(new UntestableSequence("?"));
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position exactly once or not at all.
	 *
	 * <p>
	 * literal('a').literal('b)'.atLeastOnce() will match "ab" or "abb", but not
	 * "a"
	 *
	 * @return a new regexp
	 */
	default REGEX oneOrMore() {
		return atLeastOnce();
	}

	/**
	 * Make a character range.
	 *
	 * @param lowest the first character to be included in the range
	 * @param highest the last character to be included in the range
	 * @return the start of a range.
	 */
	@SuppressWarnings("unchecked")
	default REGEX range(char lowest, char highest) {
		return beginSetIncluding().includeRange(lowest, highest).endSet();
	}

	/**
	 * Make a character set.
	 *
	 * @param literals all of the characters you would like included in the range
	 * @return the start of a range.
	 * @deprecated use {@link #characterSet(java.lang.String) } instead
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	default REGEX range(String literals) {
		return beginSetIncluding().includeLiterals(literals).endSet();
	}

	/**
	 * Make a character set (also known as a character class).
	 *
	 * @param literals all of the characters you would like included in the set
	 * @return the start of a set.
	 */
	@SuppressWarnings("unchecked")
	default REGEX characterSet(String literals) {
		return beginSetIncluding().includeLiterals(literals).endSet();
	}

	/**
	 * Make a character range to exclude characters.
	 *
	 * @param lowest the first character to be included in the range
	 * @param highest the last character to be included in the range
	 * @return the start of a range.
	 */
	@SuppressWarnings("unchecked")
	default REGEX excludeRange(char lowest, char highest) {
		return beginSetExcluding().excludeRange(lowest, highest).endSet();
	}

	/**
	 * Make a character set (also known as a character class) that matches characters outside the set.
	 *
	 * @param literals all of the characters you would like included in the set
	 * @return the start of a set.
	 */
	@SuppressWarnings("unchecked")
	default REGEX excludeSet(String literals) {
		return beginSetExcluding().excludeLiterals(literals).endSet();
	}

	/**
	 * Make a character range to exclude characters.
	 *
	 * @param literals all of the characters you would like included in the range
	 * @return the start of a range.
	 * @deprecated use {@link #excludeSet(java.lang.String) } instead
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	default REGEX excludeRange(String literals) {
		return beginRange().excluding(literals).endRange();
	}

	/**
	 * Starts making a character range, use {@link RangeBuilder#endRange() } to
	 * return to the regex.
	 *
	 * <p>
	 * This provides more options than the {@link #anyCharacterBetween(java.lang.Character, java.lang.Character)
	 * } and {@link #anyCharacterIn(java.lang.String) } methods for creating
	 * ranges.
	 *
	 * @return the start of a range.
	 * @deprecated use {@link #beginSetIncluding() } instead.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	default RangeBuilder<REGEX> range() {
		return new RangeBuilder<REGEX>((REGEX) this);
	}

	/**
	 * Starts making a character range, use {@link RangeBuilder#endRange() } to
	 * return to the regex.
	 *
	 * <p>
	 * This provides more options than the {@link #anyCharacterBetween(java.lang.Character, java.lang.Character)
	 * } and {@link #anyCharacterIn(java.lang.String) } methods for creating
	 * ranges.
	 *
	 * @return the start of a range.
	 * @deprecated use {@link #beginSetIncluding()  } instead
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	default RangeBuilder<REGEX> beginRange() {
		return new RangeBuilder<REGEX>((REGEX) this);
	}

	/**
	 * Starts making a character class that matches all the characters NOT in the
	 * class, use {@link RangeBuilder#endRange() } to return to the regex.
	 *
	 * <p>
	 * This provides more options than the {@link #anyCharacterBetween(java.lang.Character, java.lang.Character)
	 * } and {@link #anyCharacterIn(java.lang.String) } methods for creating
	 * ranges.
	 *
	 * @return the start of a class.
	 */
	@SuppressWarnings("unchecked")
	default CharacterSetExcluding<REGEX> beginSetExcluding() {
		return new CharacterSetExcluding<REGEX>((REGEX) this);
	}

	/**
	 * Starts making a character class that matches all the characters NOT in the
	 * class, use {@link RangeBuilder#endRange() } to return to the regex.
	 *
	 * <p>
	 * This provides more options than the {@link #anyCharacterBetween(java.lang.Character, java.lang.Character)
	 * } and {@link #anyCharacterIn(java.lang.String) } methods for creating
	 * ranges.
	 *
	 * @return the start of a class.
	 */
	@SuppressWarnings("unchecked")
	default CharacterSetIncluding<REGEX> beginSetIncluding() {
		return new CharacterSetIncluding<REGEX>((REGEX) this);
	}

	/**
	 * Starts making a character range, use {@link RangeBuilder#endRange() } to
	 * return to the regex.
	 *
	 * <p>
	 * This provides more options than the {@link #anyCharacterBetween(java.lang.Character, java.lang.Character)
	 * } and {@link #anyCharacterIn(java.lang.String) } methods for creating
	 * ranges.
	 *
	 * @return the start of a range.
	 * @deprecated use {@link #beginSetExcluding() } instead
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	default RangeBuilder<REGEX> excludeRange() {
		return beginRange().negated();
	}

	/**
	 * Alters the previous element in the regexp so that it matches if the element
	 * appears in that position or not.
	 *
	 * <p>
	 * literal('a').literal('b)'.zeroOrMore().literal('c') will match "ac" or
	 * "abc".</p>
	 *
	 * @return a new regexp
	 */
	default REGEX optionalMany() {
		return zeroOrMore();
	}

	/**
	 * Alters the previous element in the regexp so that it matches if the element
	 * appears in that position or not.
	 *
	 * <p>
	 * literal('a').literal('b)'.zeroOrMore().literal('c') will match "ac" or
	 * "abc".
	 *
	 * @return a new regexp
	 */
	default REGEX zeroOrMore() {
		return add(new UntestableSequence("*"));
	}

	/**
	 * Adds a check for a positive integer to the regular expression without
	 * grouping.
	 *
	 * <p>
	 * Will capture the plus so watch out for that in your calculator application.
	 *
	 * @return a new regexp
	 */
	default REGEX positiveInteger() {
		return add(Regex.startingAnywhere()
				.notPrecededBy("-")
				.literal("+").onceOrNotAtAll()
				.beginOrGroup()
				.anyCharacterBetween('1', '9').once()
				.digit().zeroOrMore()
				.or()
				.literal('0').oneOrMore().notFollowedBy(Regex.startingAnywhere().digit())
				.endOrGroup()
		);
	}

	/**
	 * Extends this regular expression with an OR grouping.
	 *
	 * <p>
	 * for instance, use this to generate "(FRED|EMILY|GRETA|DONALD)".
	 *
	 * <p>
	 * {@code Regex regex =  Regex.startAnywhere().literal("Project ").startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex();
	 * } produces "Project (A|B)".
	 *
	 * @return a new regular expression
	 */
	@SuppressWarnings("unchecked")
	public default OrGroup<REGEX> orGroup() {
		return beginOrGroup();
	}

	/**
	 * Extends this regular expression with an OR grouping.
	 *
	 * <p>
	 * for instance, use this to generate "(FRED|EMILY|GRETA|DONALD)".
	 *
	 * <p>
	 * {@code Regex regex =  Regex.startAnywhere().literal("Project ").startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex();
	 * } produces "Project (A|B)".
	 *
	 * @return a new regular expression
	 */
	@SuppressWarnings("unchecked")
	public default OrGroup<REGEX> beginOrGroup() {
		return new OrGroup<>((REGEX) this);
	}

	/**
	 * Start the creation of a new group that collects one or more regular
	 * expression elements into a single element.
	 *
	 * <p>
	 * for instance, use this to generate "(\d*\.\d)?".
	 *
	 * <p>
	 * {@code PartialRegex regex =  PartialRegex.startAnywhere().literal("Project ").startGroup().literal("A").literal("B").endGroup().once();
	 * } produces "Project (AB){1}" and will find "Project AB" but not "Project
	 * A", "Project B", nor "Project ABAB".
	 *
	 * @return a new regular expression that is an incomplete group
	 */
	@SuppressWarnings("unchecked")
	public default Group<REGEX> group() {
		return beginGroup();
	}

	/**
	 * Start the creation of a new group that collects one or more regular
	 * expression elements into a single element.
	 *
	 * <p>
	 * for instance, use this to generate "(\d*\.\d)?".
	 *
	 * <p>
	 * {@code PartialRegex regex =  PartialRegex.startAnywhere().literal("Project ").startGroup().literal("A").literal("B").endGroup().once();
	 * } produces "Project (AB){1}" and will find "Project AB" but not "Project
	 * A", "Project B", nor "Project ABAB".
	 *
	 * @return a new regular expression that is an incomplete group
	 */
	@SuppressWarnings("unchecked")
	public default Group<REGEX> beginGroup() {
		return new Group<>((REGEX) this);
	}

	/**
	 * Adds a check for a space character( ) to the regular expression without
	 * grouping.
	 *
	 * @return a new regexp
	 */
	default REGEX space() {
		return unescaped(" ");
	}

	/**
	 * Adds a check for a space character( ) to the regular expression without
	 * grouping.
	 *
	 * <p>
	 * Synonym for {@link #space() }.</p>
	 *
	 * @return a new regexp
	 */
	default REGEX blank() {
		return space();
	}

	/**
	 * Adds a tab character(\t) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	default REGEX tab() {
		return unescaped("\\t");
	}

	/**
	 * Adds the beginning of input character (\A)
	 *
	 * @return this regular expression extends with the beginning of input
	 * character
	 */
	default REGEX theBeginningOfTheInput() {
		return unescaped("\\A");
	}

	/**
	 * Adds the end of input character (\z)
	 *
	 * @return this regular expression extends with the end of input character
	 */
	default REGEX theEndOfTheInput() {
		return unescaped("\\z");
	}

	/**
	 * Adds the end of input character (\Z)
	 *
	 * @return this regular expression extends with the end of input character
	 */
	default REGEX theEndOfTheInputButForTheFinalTerminator() {
		return unescaped("\\Z");
	}

	/**
	 * Adds the end of the previous match character (\G)
	 *
	 * @return this regular expression extends with the end of the previous match
	 * character
	 */
	default REGEX theEndOfThePreviousMatch() {
		return unescaped("\\G");
	}

	/**
	 * Adds an unescaped sequence to the regexp without grouping it.
	 *
	 *
	 * @param rawRegexCharacters the literal value to addGroup to this regex
	 * @return a new regexp
	 */
	default REGEX unescaped(String rawRegexCharacters) {
		return add(new UnescapedSequence(rawRegexCharacters));
	}

	/**
	 * Adds a check for a whitespace character(\w) to the regular expression
	 * without grouping.
	 *
	 * <p>
	 * A whitespace character is [ \t\n\x0B\f\r], that is a space, tab, newline,
	 * char(11), formfeed, or carriage return.
	 *
	 * @return a new regexp
	 */
	default REGEX whitespace() {
		return unescaped("\\s");
	}

	/**
	 * Adds a check for one or more word characters(\w+) to the regular
	 * expression.
	 *
	 * <p>
	 * A word character is any letter A-Z, upper or lowercase, any digit, or the
	 * underscore character.</p>
	 *
	 * <p>
	 * Equivalent to:
	 * {@code this.beginGroup().wordCharacter().oneOrMore().endGroup()}</p>
	 *
	 * @return a new regexp
	 */
	default REGEX word() {
		return this.beginGroup().wordCharacter().oneOrMore().endGroup();
	}

	default REGEX wordBoundary() {
		return unescaped("\\b");
	}

	/**
	 * Adds a check for a word character(\w) to the regular expression without
	 * grouping.
	 *
	 * <p>
	 * A word character is any letter A-Z, upper or lowercase, any digit, or the
	 * underscore character.
	 *
	 * @return a new regexp
	 */
	default REGEX wordCharacter() {
		return unescaped("\\w");
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position exactly once or not at all.
	 *
	 * <p>
	 * literal('a').literal('b)'.onceOrNotAtAll() will match "a" or "ab", but not
	 * "abb"
	 *
	 * @return a new regexp
	 */
	public default REGEX zeroOrOnce() {
		return onceOrNotAtAll();
	}

	public default REGEX namedBackReference(String name) {
		return add(new NamedBackReference(name));
	}

	/**
	 * Numbered back references are unreliable, use named back references instead.
	 *
	 * @param number the group numbered from the start of the matching that should
	 * appear again
	 * @return a new regex
	 */
	public default REGEX numberedBackReference(int number) {
		return add(new NumberedBackReference(number));
	}

	/**
	 * Add a match for any number of characters (.*) that are enclosed in the
	 * literal.
	 *
	 * <p>
	 * This is most useful for capturing the words in quotes and similar
	 * things.</p>
	 *
	 * <p>
	 * {@code charactersWrappedBy('~')} will return "~[^~]*~"</p>
	 *
	 * @param literal the character to be found either side of a block of random
	 * characters
	 * @return a new regex
	 */
	public default REGEX charactersWrappedBy(Character literal) {
		return this.addGroup(Regex.startingAnywhere().literal(literal).noneOfThisCharacter(literal).optionalMany().literal(literal));
	}

	/**
	 * Add a match for any number of characters (.*) that are enclosed in the
	 * literal.
	 *
	 * <p>
	 * This is most useful for capturing the words in quotes and similar
	 * things.</p>
	 *
	 * <p>
	 * {@code charactersWrappedBy('(', ')')} will return "\([^\)]*\)"</p>
	 *
	 * @param starter the character expected at the start of a block of random
	 * characters
	 * @param ender the character expected at the end of a block of random
	 * characters
	 * @return a new regex
	 */
	public default REGEX charactersWrappedBy(Character starter, Character ender) {
		return this.addGroup(Regex.startingAnywhere().literal(starter).noneOfThisCharacter(ender).optionalMany().literal(ender));
	}

	/**
	 * Add a match for any number of characters (.*) that are enclosed in the
	 * literal.
	 *
	 * <p>
	 * This is most useful for capturing the words in quotes and similar
	 * things.</p>
	 *
	 * <p>
	 * {@code charactersWrappedBy('(', ')')} will return "\([^\)]*\)"</p>
	 *
	 * @param starter the character expected at the start of a block of random
	 * characters
	 * @param ender the character expected at the end of a block of random
	 * characters
	 * @return a new regex
	 */
	public default REGEX charactersWrappedBy(String starter, String ender) {
		return charactersWrappedBy(new LiteralSequence(starter), new LiteralSequence(ender));
	}

	/**
	 * Add a match for any number of characters (.*) that are enclosed in the
	 * literal.
	 *
	 * <p>
	 * This is most useful for capturing the words in quotes and similar
	 * things.</p>
	 *
	 * <p>
	 * {@code charactersWrappedBy('(', ')')} will return "\([^\)]*\)"</p>
	 *
	 * @param starter the regex expected at the start of a block of random
	 * characters
	 * @param ender the regex expected at the end of a block of random characters
	 * @return a new regex
	 */
	public default REGEX charactersWrappedBy(PartialRegex starter, PartialRegex ender) {
		return this.addGroup(Regex.startingAnywhere().add(starter).anythingButThis(ender).add(ender));
	}

	/**
	 * Negative lookahead looks past the current match to check that it is NOT
	 * followed by the lookahead expression.
	 *
	 * <p>
	 * This is similar to adding an excluding expression, @{code "[^~].} for
	 * instance, but doesn't include the excluded expression in the match. Used
	 * with named captures, this is very useful to strip delimiters. For example {@code literal("{").beginNamedCapture("cap").anyCharacter().xeroOrMore().negativeLookAhead().literal("}").endLookAhead().endNamedCapture()
	 * }
	 * </p>
	 *
	 * @return the start of a negative lookahead.
	 */
	@SuppressWarnings("unchecked")
	public default NegativeLookahead<REGEX> negativeLookAhead() {
		return new NegativeLookahead<REGEX>((REGEX) this);
	}

	public default REGEX negativeLookAhead(String ender) {
		return this.negativeLookAhead().literal(ender).endLookahead();
	}

	@SuppressWarnings("unchecked")
	public default REGEX negativeLookAhead(PartialRegex ender) {
		return negativeLookAhead().add(ender).endLookahead();
	}

	public default REGEX anythingButThis(String ender) {
		return anythingButThis(Regex.empty().literal(ender));
	}

	public default REGEX anythingButThis(PartialRegex ender) {
		return this.addGroup(
				Regex.empty().addGroup(
						Regex.empty().negativeLookAhead().add(ender).endGroup().anyCharacter()
				).optionalMany()
		);
	}

	public default REGEX positiveLookAhead(String ender) {
		return this.positiveLookAhead().literal(ender).endLookahead();
	}

	/**
	 * Implements positive lookahead, only matches if the previous pattern is
	 * followed by the lookahead pattern.
	 *
	 * <p>
	 * Note that the positive lookahead pattern is not included in the match.</p>
	 *
	 * @param ender the pattern that needs to follow the previous pattern.
	 * @return a new regular expression based on the current regex extended with
	 * the positive lookahead
	 */
	public default REGEX positiveLookAhead(PartialRegex ender) {
		return positiveLookAhead().add(ender).endLookahead();
	}

	/**
	 * Implements positive lookahead, only matches if the previous pattern is
	 * followed by the lookahead pattern.
	 *
	 * <p>
	 * Note that the positive lookahead pattern is not included in the match.</p>
	 *
	 * @return a new regular expression based on the current regex extended with
	 * the positive lookahead
	 */
	@SuppressWarnings("unchecked")
	default PositiveLookahead<REGEX> positiveLookAhead() {
		return new PositiveLookahead<REGEX>((REGEX) this);
	}

	/**
	 * Uses positive lookahead to match the ender without capturing it.
	 *
	 * <p>
	 * Particularly useful when deconstructing a list as it excludes the separator
	 * from the value.</p>
	 *
	 * @param ender the pattern that needs to follow the previous pattern.
	 * @return a new regular expression based on the current regex extended with
	 * the positive lookahead
	 */
	public default REGEX followedBy(String ender) {
		return this.positiveLookAhead(ender);
	}

	/**
	 * Adds a check to exclude a character from the regular expression without
	 * grouping.
	 *
	 * <p>
	 * To addGroup more complex ranges use .addGroup(new
	 * PartialRegex.Range(rangeItems)).</p>
	 *
	 * @param literal a character to be excluded, for example "a"
	 * @return a new regexp
	 */
	public default REGEX noneOfThisCharacter(Character literal) {
		return add(Regex.empty().beginSetExcluding().excludeLiterals("" + literal).endSet());
	}

}
