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

import nz.co.gregs.regexi.RegexValueFinder;
import nz.co.gregs.regexi.Match;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nz.co.gregs.regexi.*;

/**
 *
 * @author gregorygraham
 */
public abstract class PartialRegex extends AbstractHasRegexFunctions<PartialRegex> {

	private Pattern compiledVersion;
	private int flags = 0;

	protected PartialRegex() {
	}

	protected final void inheritStoredState(PartialRegex first) {
		this.registerAllNamedGroups(first.getNamedGroups());
		addFlags(first.flags);
	}

	protected PartialRegex addFlags(int flags) {
		this.flags |= flags;
		return this;
	}

	/**
	 * Transforms the PartialRegex into a full Regex ready for use with matching.
	 *
	 * @return a Regex
	 */
	public Regex toRegex() {
		return new Regex(this);
	}

	/**
	 * Transforms the PartialRegex into a full Regex ready for use with matching.
	 *
	 * @return a Regex
	 */
	public Regex endRegex() {
		return toRegex();
	}

	/**
	 * Transforms the PartialRegex into a RegexReplacement
	 *
	 * @return a RegexReplacement
	 */
	public RegexReplacement replaceWith() {
		return toRegex().replaceWith();
	}

	public RegexReplacement remove() {
		return toRegex().replaceWith().nothing();
	}

	/**
	 * Transforms the PartialRegex into a RegexValueFinder.
	 *
	 * RegexValueFinder is designed to be a convenient way to use named captures
	 * to return values from within strings. For instance
	 * Regex.empty().beginNamedCapture("NumberValue").number().endNamedCapture().returnValueFor("NumberValue").getValueFrom("this
	 * that 215 another").orElse("") will return "215".
	 *
	 * @param previouslyDefinedNamedCapture the named capture to return the value
	 * of.
	 * @return a RegexValueFinder
	 */
	public RegexValueFinder returnValueFor(String previouslyDefinedNamedCapture) {
		return toRegex().returnValueFrom(previouslyDefinedNamedCapture);
	}

	@Override
	public abstract String toRegexString();
	
	@Override
	public String toString(){
		return toRegexString();
	}

	@Override
	public abstract List<PartialRegex> getRegexParts();

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
	 * @param second the regular expression to addGroup to this regular expression
	 * @return a new regular expression consisting of the current expression and
	 * the supplied expression added together
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PartialRegex addGroup(PartialRegex second) {
		return new RegexCombination(this, Regex.empty().beginGroup().add(second).endGroup());
	}

	@Override
	public PartialRegex add(PartialRegex second) {
		return new RegexCombination(this, second);
	}

	protected static final PartialRegex INTEGER_REGEX = Regex.startingAnywhere()
			.anyCharacterIn("-+").onceOrNotAtAll()
			.wordBoundary()
			.beginOrGroup()
			.anyCharacterBetween('1', '9').atLeastOnce()
			.digit().zeroOrMoreGreedy()
			.or().literal('0').oneOrMore().notFollowedBy(Regex.startingAnywhere().digit())
			.endOrGroup();

	protected synchronized final Pattern getPattern() {
		if (compiledVersion == null) {
			final String regex = this.toRegexString();
			compiledVersion = Pattern.compile(regex, flags);
		}
		return compiledVersion;
	}

	/**
	 * Tests whether the supplied string matches this regex in it's entirety.
	 *
	 * The method works by combining {@link Regex#startingFromTheBeginning() } and
	 * {@link PartialRegex#endOfTheString()} with this regex and calling
	 * {@link Matcher#matches()}.
	 *
	 * @param string the string to test with this regex
	 * @return true if the beginning of the string matches this regex.
	 */
	public synchronized boolean matchesEntireString(String string) {
		String test = checkForNull(string);
		return getMatcher(test).matches();
	}

	/**
	 * Tests whether the supplied string matches this regex at the beginning of
	 * the string.
	 *
	 * The method works by combining {@link Regex#startingFromTheBeginning()
	 * } with this regex and calling {@link #matchesWithinString(java.lang.String)
	 * }.
	 *
	 * @param string the string to test with this regex
	 * @return true if the beginning of the string matches this regex.
	 */
	public synchronized boolean matchesBeginningOf(String string) {
		String test = checkForNull(string);
		boolean result = Regex.startingFromTheBeginning().add(this).matchesWithinString(test);
		return result;
	}

	private String checkForNull(String string) {
		return (string != null ? string : "");
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
	public synchronized boolean matchesEndOf(String string) {
		String test = checkForNull(string);
		boolean result = endOfTheString().matchesWithinString(test);
		return result;
	}

	public synchronized boolean matchesWithinString(String string) {
		String test = checkForNull(string);
		return getMatcher(test).find();
	}

	public synchronized Stream<MatchResult> getMatchResultsStream(String string) {
		return getMatcher(string).results();
	}

	public synchronized Matcher getMatcher(String string) {
		return getPattern().matcher(string);
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
	public synchronized MatchResult getMatchResult(String string) {
		return getMatcher(string).toMatchResult();
	}

	public synchronized HashMap<String, String> getAllNamedCapturesOfFirstMatchWithinString(String string) {
		HashMap<String, String> resultMap = new HashMap<String, String>(0);
		Matcher matcher = getMatcher(string);
		if (matcher.find()) {
			for (String name : getNamedGroups()) {
				final String group = matcher.group(name);
				if (group != null) {
					resultMap.put(name, group);
				}
			}
		}
		return resultMap;
	}

	public synchronized List<Match> getAllMatches(String string) {
		Matcher matcher = getMatcher(string);
		List<Match> matches = matcher.results().map(
				m -> Match.from(this, m)
		).collect(Collectors.toList());
		return matches;
	}

	public synchronized Optional<Match> getFirstMatchFrom(String string) {
		Matcher matcher = getMatcher(string);
		if (matcher.find()) {
			MatchResult result = matcher.toMatchResult();
			return Optional.of(Match.from(this, result));
		}
		return Optional.empty();
	}

	public RegexSplitter toSplitter() {
		return toRegex().toSplitter();
	}
}
