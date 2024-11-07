/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.io.Serializable;
import nz.co.gregs.regexi.internal.UntestableSequence;
import nz.co.gregs.regexi.internal.UnescapedSequence;
import nz.co.gregs.regexi.internal.PartialRegex;
import nz.co.gregs.regexi.internal.OrGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nz.co.gregs.regexi.internal.*;

/**
 *
 * @author gregorygraham
 */
public class Regex implements Serializable {
	

	/**
	 * Create a new empty regular expression.
	 *
	 * @return a new empty regular expression
	 */
	public static PartialRegex startingAnywhere() {
		return new UntestableSequence("");
	}

	/**
	 * Create a new empty regular expression with support for MULTILINE matches
	 * and DOTALL.
	 *
	 * <p>
	 * Normal Regex's assume that the new line characters (newline, carriage
	 * return, etc) separate the matching attempts. Adding multi-line support
	 * allows the Regex to match across new line characters by treating new line
	 * characters as normal characters.</p>
	 *
	 * <p>
	 * Additionally the DOTALL flag is included so that anyCharacter() etc.
	 * will match new line characters just like every other character.</p>
	 *
	 * @return a new empty regular expression
	 */
	public static PartialRegex multiline() {
		return new MultilinePartialRegex();
	}

	/**
	 * Create a new empty regular expression.
	 *
	 * @return a new empty regular expression
	 */
	public static PartialRegex empty() {
		return new UntestableSequence("");
	}

	/**
	 * Create a new regular expression that includes a test for the start of the
	 * string.
	 *
	 * @return a new regular expression
	 */
	public static PartialRegex startingFromTheBeginning() {
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
	 * {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex();
	 * } produces "(A|B)".
	 *
	 * @return a new regular expression
	 */
	public static OrGroup<PartialRegex> startOrGroup() {
		return empty().beginOrGroup();
	}

	private final PartialRegex partial;

	public Regex(PartialRegex partial) {
		this.partial = partial;
	}

	/**
	 * Converts the internal state of this Regex into a regular expression string.
	 *
	 * @return a regular expression string
	 */
	public String getRegex() {
		return partial.toRegexString();
	}

	/**
	 * Converts the internal state of this Regex into a regular expression string.
	 *
	 * @return a regular expression string
	 */
	@Override
	public String toString() {
		return getRegex();
	}

	/**
	 * Checks the string against the regular expression and returns true if the
	 * string matches the expression.
	 *
	 * <p>
	 * For instance {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex().matches("AB");
	 * } produces TRUE where as ...matches("CD") would not.</p>
	 *
	 * <p>
	 * Please note: This is like the functionality of {@link Matcher#find() } as
	 * it provides more useful behaviour than {@link Matcher#matches()}. In
	 * particular it allows for regexes specifying end points, as well as regexes
	 * that don't {@link #matches(java.lang.String) Matches(string)} may be
	 * equivalent to {@link #matchesBeginningOf(java.lang.String) },
	 * {@link #matchesEntireString(java.lang.String) }, or
	 * {@link #matchesEndOf(java.lang.String) } depending on the Regex. This is a
	 * feature, not a bug.</p>
	 *
	 * @param string the source text
	 * @return true if the regular expression matches the source text
	 */
	public boolean matches(String string) {
		return partial.matchesWithinString(string);
	}

	/**
	 * Checks the string against the regular expression and returns true if the
	 * string matches the expression.
	 *
	 * <p>
	 * For instance {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex().matches("AB");
	 * } produces TRUE where as ...matches("CD") would not.</p>
	 *
	 * <p>
	 * Please note: This is like the functionality of {@link Matcher#find() } as
	 * it provides more useful behaviour than {@link Matcher#matches()}. In
	 * particular it allows for regexes specifying end points, as well as regexes
	 * that don't {@link #matches(java.lang.String) Matches(string)} may be
	 * equivalent to {@link #matchesBeginningOf(java.lang.String) },
	 * {@link #matchesEntireString(java.lang.String) }, or
	 * {@link #matchesEndOf(java.lang.String) } depending on the Regex. This is a
	 * feature, not a bug.</p>
	 *
	 * @param string the source text to test against
	 * @param regexes all the regexes to test with
	 * @return true if the regular expression matches the source text
	 */
	public static boolean matchesAny(String string, Regex... regexes) {
		return loopForMatches(regexes, string, false, true);
	}

	/**
	 * Checks the string against the regular expression and returns true if the
	 * string matches the expression.
	 *
	 * <p>
	 * For instance {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex().matches("AB");
	 * } produces TRUE where as ...matches("CD") would not.</p>
	 *
	 * <p>
	 * Please note: This is like the functionality of {@link Matcher#find() } as
	 * it provides more useful behaviour than {@link Matcher#matches()}. In
	 * particular it allows for regexes specifying end points, as well as regexes
	 * that don't {@link #matches(java.lang.String) Matches(string)} may be
	 * equivalent to {@link #matchesBeginningOf(java.lang.String) },
	 * {@link #matchesEntireString(java.lang.String) }, or
	 * {@link #matchesEndOf(java.lang.String) } depending on the Regex. This is a
	 * feature, not a bug.</p>
	 *
	 * @param string the source text
	 * @param regexes the regexes to test with
	 * @return true if the regular expression matches the source text
	 */
	public static boolean matchesAll(String string, Regex... regexes) {
		boolean result = loopForMatches(regexes, string, true, false);
		return result;
	}

	protected static boolean loopForMatches(Regex[] regexes, String string, boolean initialAssumption, boolean watchFor) {
		boolean result = initialAssumption;
		boolean continueLoop = true;
		int i = 0;
		while (continueLoop) {
			if (i < regexes.length) {
				if (regexes[i].matches(string) == watchFor) {
					result = !initialAssumption;
					continueLoop = false;
				}
			} else {
				continueLoop = false;
			}
			i++;
		}
		return result;
	}

	/**
	 * Checks the string against the regular expression and returns true if the
	 * entire string matches the expression.
	 *
	 * <p>
	 * For instance {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex().matchesEntireString("A");
	 * } produces TRUE where as ...matchesEntireString("AB") would not.
	 *
	 * @param string the source text
	 * @return true if the regular expression matches the entire source text
	 */
	public boolean matchesEntireString(String string) {
		return partial.matchesEntireString(string);
	}

	/**
	 * Checks the string against the regular expression and returns true if the
	 * beginning of the string matches the expression.
	 *
	 * <p>
	 * For instance {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex().matchesBeginningOf("AB");
	 * } produces TRUE where as ...matchesBeginningOf("CB") would not.
	 *
	 * @param string the source text
	 * @return true if the regular expression matches the start of the source text
	 */
	public boolean matchesBeginningOf(String string) {
		return partial.matchesBeginningOf(string);
	}

	/**
	 * Checks the string against the regular expression and returns true if the
	 * end of the string matches the expression.
	 *
	 * <p>
	 * For instance {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex().matchesEndOf("AB");
	 * } produces TRUE where as ...matchesEndOf("AC") would not.
	 *
	 * @param string the source text
	 * @return true if the regular expression matches the end of the source text
	 */
	public boolean matchesEndOf(String string) {
		return partial.matchesEndOf(string);
	}

	/**
	 * Checks the string against the regular expression and returns true if the
	 * any part of the string matches the expression.
	 *
	 * <p>
	 * For instance {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex().matchesWithinString("CAD");
	 * } produces TRUE where as ...matchesWithinString("CDE") would not.
	 *
	 * @param string the source text
	 * @return true if the regular expression matches any part of the source text
	 */
	public boolean matchesWithinString(String string) {
		return partial.matchesWithinString(string);
	}

	public Stream<MatchResult> getMatchResultsStream(String string) {
		return partial.getMatchResultsStream(string);
	}

	public Matcher getMatcher(String string) {
		return partial.getMatcher(string);
	}

	/**
	 * Return a java.util.MatchResult.
	 *
	 * @param string the source text
	 * @return the MatchResult
	 */
	public MatchResult getMatchResult(String string) {
		return partial.getMatchResult(string);
	}

	public HashMap<String, String> getAllNamedCapturesOfFirstMatchWithinString(String string) {
		return partial.getAllNamedCapturesOfFirstMatchWithinString(string);
	}

	/**
	 * Returns a list of all the matches found within the source text.
	 *
	 * <p>
	 * Match includes the entire match, all the groups within the match, and all
	 * the named captures.
	 * </p>
	 *
	 * @param string the source text.
	 * @return every Match found in the source text.
	 */
	public List<Match> getAllMatches(String string) {
		return partial.getAllMatches(string);
	}

	/**
	 * Returns the first match found within the source text.
	 *
	 * <p>
	 * Match includes the entire match, all the groups within the match, and all
	 * the named captures.
	 * </p>
	 *
	 * @param string the source text.
	 * @return the first Match found in the source text.
	 */
	public Optional<Match> getFirstMatchFrom(String string) {
		return partial.getFirstMatchFrom(string);
	}

	/**
	 * Test all parts of this Regex against the test string and return the
	 * results.
	 *
	 * <p>
	 * While tricky to interpret, this is a good way to work out why your Regex
	 * isn't working.</p>
	 *
	 * @param testStr the test text to use
	 * @return a list of the results of testing the parts of the Regex.
	 */
	public List<String> testAgainst(String testStr) {
		List<PartialRegex> partials = new ArrayList<>();
		partials.add(partial);
		partials.addAll(this.partial.getRegexParts());
		List<String> strings = new ArrayList<>();
		strings.addAll(testAgainstAnywhereInString(testStr, partials));
		return strings;
	}

	private List<String> testAgainstGeneric(String testStr, List<PartialRegex> patterns, String descriptionStart, BiFunction<Regex, String, Boolean> matcher) {
		List<String> strings = new ArrayList<String>(0);
		strings.add("TESTING: " + getRegex());
		strings.add(descriptionStart + ": " + testStr);
		for (PartialRegex pattern : patterns) {
			try {
				strings.add("TESTING: " + pattern.toRegexString());
				final Regex regex = pattern.toRegex();
				final boolean result = matcher.apply(regex, testStr);
				strings.add("RESULT: " + (result ? "found" : "FAILED"));
				if (result) {
					strings.addAll(regex.getAllMatches(testStr).stream().map(Match::toString).collect(Collectors.toList()));
					regex.getAllMatches(testStr).stream().forEachOrdered(m -> m.allGroups().stream().forEachOrdered(g -> strings.add(g.toString())));
				}

			} catch (Exception ex) {
				strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
			}
		}
		return strings;
	}

	public List<String> testAgainstEndOfString(String testStr, List<PartialRegex> patterns) {
		return testAgainstGeneric(testStr, patterns, "AT END OF", (r, s) -> r.matchesEndOf(s));
	}

	public List<String> testAgainstBeginningOfString(String testStr, List<PartialRegex> patterns) {
		return testAgainstGeneric(testStr, patterns, "FROM START OF", (r, s) -> r.matchesBeginningOf(s));
	}

	public List<String> testAgainstAnywhereInString(String testStr, List<PartialRegex> patterns) {
		return testAgainstGeneric(testStr, patterns, "WITHIN", (r, s) -> r.matchesWithinString(s));
	}

	public List<String> testAgainstEntireString(String testStr, List<PartialRegex> patterns) {
		return testAgainstGeneric(testStr, patterns, "MATCHES ENTIRE STRING", (r, s) -> r.matchesEntireString(s));
	}

	/**
	 * Create a RegexReplacement from this Regex.
	 *
	 * @return a RegexReplacement
	 */
	public RegexReplacement replaceWith() {
		return new RegexReplacement(this);
	}

	/**
	 * Create a RegexValueFinder from this Regex, returning the specified named
	 * capture.
	 *
	 * @param previouslyDefinedNamedCapture the named capture to return when
	 * requested.
	 * @return a RegexValueFinder
	 */
	public RegexValueFinder returnValueFrom(String previouslyDefinedNamedCapture) {
		return new RegexValueFinder(this, previouslyDefinedNamedCapture);
	}

	/**
	 * Creates a RegexSplitter from this Regex.
	 *
	 * @return a RegexSplitter
	 */
	public RegexSplitter toSplitter() {
		return new RegexSplitter(this);
	}

	public boolean doesNotMatchTheBeginningOf(String sourceText) {
		return !matchesBeginningOf(sourceText);
	}

	public boolean doesNotMatchTheEndOf(String sourceText) {
		return !matchesEndOf(sourceText);
	}

	public boolean doesNotMatchTheEntireString(String sourceText) {
		return !matchesEntireString(sourceText);
	}

	public boolean doesNotMatchWithin(String sourceText) {
		return !matchesWithinString(sourceText);
	}
}
