/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

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
public class Regex {

	/**
	 * Create a new empty regular expression.
	 *
	 * @return a new empty regular expression
	 */
	public static PartialRegex startingAnywhere() {
		return new UntestableSequence("");
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

	public String getRegex() {
		return partial.toRegexString();
	}

	public boolean matchesEntireString(String string) {
		return partial.matchesEntireString(string);
	}

	public boolean matchesBeginningOf(String string) {
		return partial.matchesBeginningOf(string);
	}

	public boolean matchesEndOf(String string) {
		return partial.matchesEndOf(string);
	}

	public boolean matchesWithinString(String string) {
		return partial.matchesWithinString(string);
	}

	public Stream<MatchResult> getMatchResultsStream(String string) {
		return partial.getMatchResultsStream(string);
	}

	public Matcher getMatcher(String string) {
		return partial.getMatcher(string);
	}

	public MatchResult getMatchResult(String string) {
		return partial.getMatchResult(string);
	}

	public HashMap<String, String> getAllNamedCapturesOfFirstMatchWithinString(String string) {
		return partial.getAllNamedCapturesOfFirstMatchWithinString(string);
	}

	public List<Match> getAllMatches(String string) {
		return partial.getAllMatches(string);
	}

	public Optional<Match> getFirstMatchFrom(String string) {
		return partial.getFirstMatchFrom(string);
	}

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

	public RegexReplacement replaceWith() {
		return new RegexReplacement(this);
	}

	public RegexValueFinder returnValueFrom(String previouslyDefinedNamedCapture) {
		return new RegexValueFinder(this, previouslyDefinedNamedCapture);
	}
}
