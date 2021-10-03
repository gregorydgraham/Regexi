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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author gregorygraham
 */
public abstract class Regex implements HasRegexFunctions<Regex> {

	private Pattern compiledVersion;

	protected Regex() {
	}

	@Override
	public abstract String getRegex();

	/**
	 * Create a new empty regular expression.
	 *
	 * @return a new empty regular expression
	 */
	public static Regex startingAnywhere() {
		return new UnescapedSequence("");
	}

	/**
	 * Create a new regular expression that includes a test for the start of the
	 * string.
	 *
	 * @return a new regular expression
	 */
	public static Regex startingFromTheBeginning() {
		return new UnescapedSequence("^");
	}

	/**
	 * Create a regular expression that includes all the regexps supplied within
	 * an OR grouping.
	 *
	 * <p>
	 * for instance, use this to generate "(FRED|EMILY|GRETA|DONALD)".
	 *
	 * <p>
	 * {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endGroup();
	 * } produces "(A|B)".
	 *
	 * @return a new regular expression
	 */
	public static OrGroup<Regex> startOrGroup() {
		return startingAnywhere().beginOrGroup();
	}

	/**
	 * Adds the regular expression to the end of current expression as a new
	 * group.
	 *
	 * <p>
	 * For example Regex.startingAnywhere().add(allowedValue).add(separator) will
	 * add the "separator" regular expression to the "allowedValue" expression
	 * (the rest of the instruction adds nothing). Assuming that allowedValue is
	 * "[0-9]" and separator is ":", the full regexp will be "([0-9])(:)".
	 *
	 * @param second the regular expression to add to this regular expression
	 * @return a new regular expression consisting of the current expression and
	 * the supplied expression added together
	 */
	@Override
	public Regex add(HasRegexFunctions<?> second) {
		return new RegexpCombination(this, second.groupEverythingBeforeThis());
	}

	/**
	 * Adds the regular expression to the end of current expression without
	 * grouping it.
	 *
	 * <p>
	 * Not grouping the added regular expression can produce counter-intuitive
	 * results and breaks encapsulation so use it carefully. In Particular
	 * extend(myRegex).onceOrNotAtAll() will only apply the "onceOrNotAtAll" to
	 * last element of myRegex and not the entire expression. Using
	 * digit.extend(Regex.startAnywhere().dot().digits()).onceOrNotAtAll() will
	 * match "0." and "0.5" but not "0". If you want grouping use add() instead.
	 *
	 * <p>
	 * For example Regex.startingAnywhere().extend(allowedValue).extend(separator)
	 * will add the "separator" regular expression to the "allowedValue"
	 * expression (the rest of the instruction adds nothing). Assuming that
	 * allowedValue is "[0-9]" and separator is ":", the full regexp will be
	 * "[0-9]:".
	 *
	 * @param second the regular expression to extend this regular expression with
	 * @return a new regular expression consisting of the current expression and
	 * the supplied expression added together
	 */
	@Override
	public Regex extend(HasRegexFunctions<?> second) {
		return new RegexpCombination(this, second);
	}

	@Override
	public Regex literal(Character character) {
		return extend(new LiteralSequence("" + character));
	}

	/**
	 * Adds a literal string to the regexp without grouping it.
	 *
	 *
	 * @param literals the literal value to add to this regex
	 * @return a new regexp
	 */
	@Override
	public Regex literal(String literals) {
		return extend(new LiteralSequence(literals));
	}

	/**
	 * Adds a unescaped sequence to the regexp without grouping it.
	 *
	 *
	 * @param literals the literal value to add to this regex
	 * @return a new regexp
	 */
	@Override
	public Regex unescaped(String literals) {
		return extend(new UnescapedSequence(literals));
	}

	/**
	 * Adds a literal backslash(\) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex backslash() {
		return extend(new UnescapedSequence("\\\\"));
	}

	/**
	 * Adds a literal carat (^) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex carat() {
		return extend(new UnescapedSequence("\\^"));
	}

	/**
	 * Adds a literal dollar sign($) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex dollarSign() {
		return extend(new UnescapedSequence("\\$"));
	}

	/**
	 * Adds a literal dot(.) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex dot() {
		return extend(new UnescapedSequence("\\."));
	}

	/**
	 * Adds a literal question mark(?) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex questionMark() {
		return extend(new UnescapedSequence("\\?"));
	}

	/**
	 * Adds a literal plus sign(+) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex plus() {
		return extend(new UnescapedSequence("\\+"));
	}

	/**
	 * Adds a literal star(*) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex star() {
		return extend(new UnescapedSequence("\\*"));
	}

	/**
	 * Adds a literal asterisk(*) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex asterisk() {
		return extend(new UnescapedSequence("\\*"));
	}

	/**
	 * Adds a literal pipe(|) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex pipe() {
		return extend(new UnescapedSequence("\\|"));
	}

	/**
	 * Adds a literal square bracket([) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex squareBracket() {
		return extend(new UnescapedSequence("\\["));
	}

	/**
	 * Adds a literal bracket, i.e. "(", to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex bracket() {
		return extend(new UnescapedSequence("\\("));
	}

	/**
	 * Adds a tab character(\t) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex tab() {
		return extend(new UnescapedSequence("\\t"));
	}

	/**
	 * Adds a newline character(\n) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex newline() {
		return extend(new UnescapedSequence("\\n"));
	}

	/**
	 * Adds a carriage return character(\r) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex carriageReturn() {
		return extend(new UnescapedSequence("\\r"));
	}

	/**
	 * Adds a formfeed character(\f) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex formfeed() {
		return extend(new UnescapedSequence("\\f"));
	}

	/**
	 * Adds a bell character(\a) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex bell() {
		return extend(new UnescapedSequence("\\a"));
	}

	/**
	 * Adds a escape character(\e) to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex escapeCharacter() {
		return extend(new UnescapedSequence("\\e"));
	}

	/**
	 * Adds a control character(\cX) to the regexp without grouping it.
	 *
	 * @param x the control character
	 * @return a new regexp
	 */
	@Override
	public Regex controlCharacter(String x) {
		return extend(new UnescapedSequence("\\c" + x));
	}

	/**
	 * Adds a match for any single character to the regexp without grouping it.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex anyCharacter() {
		return extend(new UnescapedSequence("."));
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position exactly once.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex once() {
		return extend(new UnescapedSequence("{1}"));
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position exactly X number of times.
	 *
	 * @param x the number of times the previous match must occur
	 * @return a new regexp
	 */
	@Override
	public Regex thisManyTimes(int x) {
		return extend(new UnescapedSequence("{" + x + "}"));
	}

	/**
	 * Alters the previous element in the regexp so that it only matches if the
	 * element appears in that position X times or more.
	 *
	 * @param x the minimum number of times the previous match must occur
	 * @return a new regexp
	 */
	@Override
	public Regex atLeastThisManyTimes(int x) {
		return extend(new UnescapedSequence("{" + x + ",}"));
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
	@Override
	public Regex atLeastXAndNoMoreThanYTimes(int x, int y) {
		return extend(new UnescapedSequence("{" + x + "," + y + "}"));
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
	@Override
	public Regex onceOrNotAtAll() {
		return extend(new UnescapedSequence("?"));
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
	@Override
	public Regex atLeastOnce() {
		return extend(new UnescapedSequence("+"));
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
	@Override
	public Regex oneOrMore() {
		return atLeastOnce();
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
	@Override
	public Regex zeroOrMore() {
		return extend(new UnescapedSequence("*"));
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
	@Override
	public Regex optionalMany() {
		return zeroOrMore();
	}

	/**
	 * Adds a check for the end of the string to the regular expression without
	 * grouping.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex endOfTheString() {
		return extend(new UnescapedSequence("$"));
	}

	/**
	 * Adds a check for a digit(0123456789) to the regular expression without
	 * grouping.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex digit() {
		return extend(new UnescapedSequence("\\d"));
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
	@Override
	public Regex digits() {
		return beginGroup().digit().oneOrMore().endGroup();
	}

	/**
	 * Adds a check for anything other than a digit(0123456789) to the regular
	 * expression without grouping.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex nondigit() {
		return extend(new UnescapedSequence("\\D"));
	}

	/**
	 * Adds a check for one or more of anything other than a digit(0123456789) to
	 * the regular expression without grouping.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex nondigits() {
		return nondigit().oneOrMore();
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
	@Override
	public Regex wordCharacter() {
		return extend(new UnescapedSequence("\\w"));
	}

	/**
	 * Adds a check for one or more word characters(\w+) to the regular expression
	 * without grouping.
	 *
	 * <p>
	 * A word character is any letter A-Z, upper or lowercase, any digit, or the
	 * underscore character.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex word() {
		return beginGroup().wordCharacter().oneOrMore().endGroup();
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
	@Override
	public Regex gapBetweenWords() {
		return nonWordCharacter().oneOrMore();
	}

	/**
	 * Adds a check for a non-word character(\w) to the regular expression without
	 * grouping.
	 *
	 * <p>
	 * A word character is any letter A-Z, upper or lowercase, any digit, or the
	 * underscore character. A non-word character is any other character.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex nonWordCharacter() {
		return extend(new UnescapedSequence("\\W"));
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
	@Override
	public Regex whitespace() {
		return extend(new UnescapedSequence("\\s"));
	}

	/**
	 * Adds a check for a non-whitespace character(\w) to the regular expression
	 * without grouping.
	 *
	 * <p>
	 * A whitespace character is [ \t\n\x0B\f\r], that is a space, tab, newline,
	 * char(11), form-feed, or carriage return. A non-whitespace character is
	 * anything else.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex nonWhitespace() {
		return extend(new UnescapedSequence("\\S"));
	}

	@Override
	public Regex wordBoundary() {
		return extend(new UnescapedSequence("\\b"));
	}

	@Override
	public Regex nonWordBoundary() {
		return extend(new UnescapedSequence("\\B"));
	}

	@Override
	public Regex theBeginningOfTheInput() {
		return extend(new UnescapedSequence("\\A"));
	}

	@Override
	public Regex theEndOfThePreviousMatch() {
		return extend(new UnescapedSequence("\\G"));
	}

	@Override
	public Regex theEndOfTheInput() {
		return extend(new UnescapedSequence("\\z"));
	}

	@Override
	public Regex theEndOfTheInputButForTheFinalTerminator() {
		return extend(new UnescapedSequence("\\Z"));
	}

	/**
	 * Adds a check for a space character( ) to the regular expression without
	 * grouping.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex space() {
		return extend(new UnescapedSequence(" "));
	}

	/**
	 * Places the regular expression in a capturing group.
	 *
	 * <p>
	 * capturing and grouping are the same, there are methods of both names to
	 * capture the intent.
	 *
	 * @param regexp the regex to add to this regex as a new capturing group
	 * @return a new regexp
	 */
	@Override
	public Regex capture(Regex regexp) {
		return this.beginGroup().extend(regexp).endGroup();
//		return new UnescapedSequence("(" + regexp.getRegex() + ")");
	}

	/**
	 * Adds a check for a negative integer to the regular expression without
	 * grouping.
	 *
	 * <p>
	 * Will capture the minus so watch out for that in your calculator
	 * application.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex negativeInteger() {
		return extend(
				literal('-')
						.beginOrGroup()
						.anyCharacterBetween('1', '9').once().digit().zeroOrMore()
						.or()
						.literal('0').notFollowedBy(Regex.startingAnywhere().digit())
						.endOrGroup()
		);
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
	@Override
	public Regex positiveInteger() {
		return extend(
				Regex.startingAnywhere()
						.notPrecededBy("-")
						.plus().onceOrNotAtAll()
						.beginOrGroup()
						.anyCharacterBetween('1', '9').once()
						.digit().zeroOrMore()
						.or()
						.literal('0').oneOrMore().notFollowedBy(Regex.startingAnywhere().digit())
						.endOrGroup()
		);
	}

	/**
	 * Adds a check for a simple range to the regular expression without grouping.
	 *
	 * <p>
	 * To add more complex ranges use .add(new Regex.Range(lowest, highest)).
	 *
	 * @param lowest the (inclusive) start of the character range
	 * @param highest the (inclusive) end of the character range
	 * @return a new regexp
	 */
	@Override
	public Regex anyCharacterBetween(Character lowest, Character highest) {
		return extend(Regex.startingAnywhere().beginRange(lowest, highest).endRange());
	}

	/**
	 * Adds a check for a simple range to the regular expression without grouping.
	 *
	 * <p>
	 * To add more complex ranges use .add(new Regex.Range(rangeItems)).
	 *
	 * @param literals all the characters to be included in the range, for example
	 * "abcdeABCDE"
	 * @return a new regexp
	 */
	@Override
	public Regex anyCharacterIn(String literals) {
		return extend(Regex.startingAnywhere().beginRange(literals).endRange());
	}

	@Override
	public Regex anyOf(String literal, String... literals) {
		nz.co.gregs.regexi.OrGroup<nz.co.gregs.regexi.Regex> temp = beginOrGroup().literal(literal);
		for (String literal1 : literals) {
			temp = temp.or().literal(literal1);
		}
		return temp.endOrGroup();
	}

	/**
	 * Adds a check to exclude a simple range from the regular expression without
	 * grouping.
	 *
	 * <p>
	 * To add more complex ranges use .add(new Regex.Range(lowest, highest)).
	 *
	 * @param lowest the (inclusive) start of the character range
	 * @param highest the (inclusive) end of the character range
	 * @return a new regexp
	 */
	@Override
	public Regex noCharacterBetween(Character lowest, Character highest) {
		return extend(Regex.startingAnywhere().beginRange(lowest, highest).negated().endRange());
	}

	/**
	 * Adds a check to exclude a simple range from the regular expression without
	 * grouping.
	 *
	 * <p>
	 * To add more complex ranges use .add(new Regex.Range(rangeItems)).
	 *
	 * @param literals all the characters to be included in the range, for example
	 * "abcdeABCDE"
	 * @return a new regexp
	 */
	@Override
	public Regex noneOfTheseCharacters(String literals) {
		return extend(Regex.startingAnywhere().beginRange(literals).negated().endRange());
	}

	/**
	 * Places the regular expression in a group and adds it as one element for the
	 * next instruction.
	 *
	 * <p>
	 * capturing and grouping are the same, there are methods of both names to
	 * capture the intent.
	 *
	 * @return a new regexp
	 */
	@Override
	public Regex groupEverythingBeforeThis() {
		return Regex.startingAnywhere().beginGroup().extend(this).endGroup();
	}

	protected static final Regex INTEGER_REGEX = Regex.startingAnywhere()
			.anyCharacterIn("-+").onceOrNotAtAll()
			.wordBoundary()
			.beginOrGroup()
			.anyCharacterBetween('1', '9').atLeastOnce()
			.digit().zeroOrMore()
			.or().literal('0').oneOrMore().notFollowedBy(Regex.startingAnywhere().digit())
			.endOrGroup();

	/**
	 * Places the regular expression in a group and add it as one element for the
	 * next instruction.
	 *
	 * <p>
	 * capturing and grouping are the same, there are methods of both names to
	 * capture the intent.
	 *
	 * @param regex the expression to add as a group
	 * @return a new regexp
	 */
	@Override
	public Regex addGroup(HasRegexFunctions<?> regex) {
		return this.extend(regex.groupEverythingBeforeThis());
	}

	protected final Pattern getCompiledVersion() {
		return getPattern();
	}

	protected final Pattern getPattern() {
		if (compiledVersion == null) {
			final String regexp = this.getRegex();
			compiledVersion = Pattern.compile(regexp);
		}
		return compiledVersion;
	}

	public boolean matchesEntireString(String string) {
		return getMatcher(string).matches();
	}

	/**
	 * Tests whether the supplied string matches this regex at the beginning of
	 * the string.
	 *
	 * The method works by combining {@link Regex#startingFromTheBeginning() ()
	 * } with this regex and calling {@link #matchesWithinString(java.lang.String)
	 * }.
	 *
	 * @param string the string to test with this regex
	 * @return true if the beginning of the string matches this regex.
	 */
	public boolean matchesBeginningOf(String string) {
		return Regex.startingFromTheBeginning().extend(this).matchesWithinString(string);
	}

	/**
	 * Tests whether the supplied string matches this regex at the end of the
	 * string.
	 *
	 * The method works by combining this regex with {@link #endOfTheString() }
	 * and calling {@link #matchesWithinString(java.lang.String) }.
	 *
	 * @param string the string to test with this regex
	 * @return true if the end of the string matches this regex.
	 */
	public boolean matchesEndOf(String string) {
		return endOfTheString().matchesWithinString(string);
	}

	public boolean matchesWithinString(String string) {
		return getMatcher(string).find();
	}

	@Override
	public CaseInsensitiveSection<Regex> beginCaseInsensitiveSection() {
		return new CaseInsensitiveSection<>(this);
	}

	@Override
	public NamedCapture<Regex> beginNamedCapture(String name) {
		return new NamedCapture<>(this, name);
	}

	public Stream<MatchResult> getMatchResultsStream(String string) {
		return getMatcher(string).results();
	}

	protected Matcher getMatcher(String string) {
		return getCompiledVersion().matcher(string);
	}

	/**
	 *
	 * Convenient access to Matcher.group().
	 *
	 * Deprecated: use {@link #getFirstMatchFrom(java.lang.String) }
	 *
	 * Returns the input subsequence matched by the previous match.
	 *
	 * <p>
	 * For a matcher <i>m</i> with input sequence <i>s</i>, the expressions
	 * <i>m.</i>{@code group()} and
	 * <i>s.</i>{@code substring(}<i>m.</i>{@code start(),}&nbsp;<i>m.</i>{@code end())}
	 * are equivalent.  </p>
	 *
	 * <p>
	 * Note that some patterns, for example {@code a*}, match the empty string.
	 * This method will return the empty string when the pattern successfully
	 * matches the empty string in the input.  </p>
	 *
	 * @return The (possibly empty) subsequence matched by the previous match, in
	 * string form
	 *
	 * @throws IllegalStateException If no match has yet been attempted, or if the
	 * previous match operation failed
	 * @param string the string to match
	 * @deprecated use {@link #getFirstMatchFrom(java.lang.String) }
	 */
	@Deprecated
	public String getFirstMatch(String string) {
		return getMatcher(string).group();
	}

	/**
	 * Convenient access to Matcher.toMatchResult.
	 * <p>
	 * Returns the match state of this matcher as a {@link MatchResult}. The
	 * result is unaffected by subsequent operations performed upon this matcher.
	 *
	 * @return a {@code MatchResult} with the state of this matcher
	 * @param string the string to generate the MatchResult for
	 * @since 1.5
	 */
	public MatchResult getMatchResult(String string) {
		return getMatcher(string).toMatchResult();
	}

	@Deprecated
	public Optional<String> getNamedCapture(String name, String string) {
		final String found = getMatcher(string).group(name);
		if (found == null) {
			return Optional.empty();
		} else {
			return Optional.of(found);
		}
	}

	public HashMap<String, String> getAllNamedCapturesOfFirstMatchWithinString(String string) {
		HashMap<String, String> resultMap = new HashMap<String, String>(0);
		try {
			Matcher matcher = getMatcher(string);
			if (matcher.find()) {
				Class<? extends Pattern> patternClass = getPattern().getClass();
				Method method = patternClass.getDeclaredMethod("namedGroups");
				method.setAccessible(true);
				Object invoke = method.invoke(getPattern());
				@SuppressWarnings("unchecked")
				Map<String, Integer> map = (Map<String, Integer>) invoke;
				if (map.size() > 0) {
					for (String name : map.keySet()) {
						final String group = matcher.group(name);
						if (group != null) {
							resultMap.put(name, group);
						}
					}
				}
			}
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(Regex.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(Regex.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(Regex.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalArgumentException ex) {
			Logger.getLogger(Regex.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvocationTargetException ex) {
			Logger.getLogger(Regex.class.getName()).log(Level.SEVERE, null, ex);
		}
		return resultMap;
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
	@Override
	public Regex notPrecededBy(String literalValue) {
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
	@Override
	public Regex notPrecededBy(Regex literalValue) {
		return this
				.extend(new UnescapedSequence("(?<!"))
				.extend(literalValue)
				.extend(new UnescapedSequence(")"));
	}

	/**
	 * Adds a check for a that the next element does not have the literal value
	 * immediately after it.
	 *
	 * <p>
	 * For instance to match words but not e-mail addresses you might use
	 * Regex.startingAnywhere().word().notFollowedBy("@").
	 *
	 * @param literalValue the literal string that cannot come after this regex
	 * @return a new regexp
	 */
	@Override
	public Regex notFollowedBy(String literalValue) {
		return this.notFollowedBy(new LiteralSequence(literalValue));
	}

	/**
	 * Adds a check for a that the next element does not have the literal value
	 * immediately after it.
	 *
	 * <p>
	 * For instance to match words but not e-mail addresses you might use
	 * Regex.startingAnywhere().word().notFollowedBy("@").
	 *
	 * @param literalValue the literal string that cannot come after this regex
	 * @return a new regexp
	 */
	@Override
	public Regex notFollowedBy(Regex literalValue) {
		return this
				.extend(new UnescapedSequence("(?!"))
				.extend(literalValue)
				.extend(new UnescapedSequence(")"));
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
	 * @param lowest the first value to include in the range
	 * @param highest the last value to include in the range
	 * @return the start of a range.
	 */
	@Override
	public RangeBuilder<Regex> beginRange(char lowest, char highest) {
		return new RangeBuilder<>(this, lowest, highest);
	}

	@Override
	public RangeBuilder<Regex> beginRange(String literals) {
		return new RangeBuilder<>(this, literals);
	}

	public List<Match> getAllMatches(String string) {
		Matcher matcher = getMatcher(string);
		List<Match> matches = matcher.results().map(
				m -> Match.from(this, m)
		).collect(Collectors.toList());
		return matches;
	}

	public Optional<Match> getFirstMatchFrom(String string) {
		Matcher matcher = getMatcher(string);
		if (matcher.find()) {
			MatchResult result = matcher.toMatchResult();
			return Optional.of(Match.from(this, result));
		}
		return Optional.empty();
	}

	/**
	 * Use {@link #getFirstMatchFrom(java.lang.String) } and {@link Match#allGroups()
	 * }
	 *
	 * @param string the string to match
	 * @return a list of all groups in all matches within string
	 * @deprecated Use {@link #getFirstMatchFrom(java.lang.String) } and {@link Match#allGroups()
	 * }
	 */
	@Deprecated
	public List<String> getAllGroups(String string) {
		Matcher matcher = getMatcher(string);
		List<String> groups = new ArrayList<>(0);
		while (matcher.find()) {
			int count = matcher.groupCount();
			for (int i = 0; i < count; i++) {
				final String foundGroup = matcher.group(i);
				if (foundGroup != null) {
					groups.add(foundGroup);
				}
			}
		}
		return groups;
	}

	@Override
	public Regex literalCaseInsensitive(String literal) {
		return this
				.addGroup(Regex.startingAnywhere()
						.beginCaseInsensitiveSection()
						.literal(literal)
						.endCaseInsensitiveSection()
				);
	}

	@Override
	public List<String> testAgainst(String testStr) {
		List<String> strings = new ArrayList<>(2);
		testAgainstEntireString(strings, testStr);
		testAgainstAnywhereInString(testStr, strings);
		testAgainstBeginningOfString(testStr, strings);
		testAgainstEndOfString(testStr, strings);
		return strings;
	}

	private void testAgainstEndOfString(String testStr, List<String> strings) {
		try {
			final boolean result = this.matchesEndOf(testStr);
			strings.add("RESULT: " + (result ? "found" : "FAILED"));
			if (result) {
				final List<Match> allMatches = this.getAllMatches(testStr);
				allMatches.stream().forEach(v -> strings.add("MATCHED: " + v.getEntireMatch()));
			}
		} catch (Exception ex) {
			strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
		}
	}

	private void testAgainstBeginningOfString(String testStr, List<String> strings) {
		try {
			final boolean result = this.matchesBeginningOf(testStr);
			strings.add("RESULT: " + (result ? "found" : "FAILED"));
			if (result) {
				final List<Match> allMatches = this.getAllMatches(testStr);
				allMatches.stream().forEach(v -> strings.add("MATCHED: " + v.getEntireMatch()));
			}
		} catch (Exception ex) {
			strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
		}
		strings.add("TESTING: " + getRegex());
		strings.add("AT END OF: " + testStr);
	}

	private void testAgainstAnywhereInString(String testStr, List<String> strings) {
		try {
			final boolean result = this.matchesWithinString(testStr);
			strings.add("RESULT: " + (result ? "found" : "FAILED"));
			if (result) {
				final List<Match> allMatches = this.getAllMatches(testStr);
				allMatches.stream().forEach(v -> strings.add("MATCHED: " + v.getEntireMatch()));
			}
		} catch (Exception ex) {
			strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
		}
		strings.add("TESTING: " + getRegex());
		strings.add("AT BEGINNING OF: " + testStr);
	}

	private void testAgainstEntireString(List<String> strings, String testStr) {
		strings.add("TESTING: " + getRegex());
		strings.add("AGAINST ALL OF: " + testStr);
		try {
			final boolean result = this.matchesEntireString(testStr);
			strings.add("RESULT: " + (result ? "found" : "FAILED"));
			if (result) {
				final List<Match> allMatches = this.getAllMatches(testStr);
				allMatches.stream().forEach(v -> strings.add("MATCHED: " + v.getEntireMatch()));
			}
		} catch (Exception ex) {
			strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
		}
		strings.add("TESTING: " + getRegex());
		strings.add("WITHIN: " + testStr);
	}
}
