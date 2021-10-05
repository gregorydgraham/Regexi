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
	 * Create a new empty regular expression.
	 *
	 * @return a new empty regular expression
	 */
	public static Regex empty() {
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

	@Override
	public Regex extend(HasRegexFunctions<?> second) {
		return new RegexpCombination(this, second);
	}

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
