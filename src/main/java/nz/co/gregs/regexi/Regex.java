/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
		return new UnescapedSequence("");
	}

	/**
	 * Create a new empty regular expression.
	 *
	 * @return a new empty regular expression
	 */
	public static PartialRegex empty() {
		return new UnescapedSequence("");
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
	 * {@code Regex toRegex =  Regex.startOrGroup().literal("A").or().literal("B").endOrGroup().toRegex();
 } produces "(A|B)".
	 *
	 * @return a new regular expression
	 */
	public static OrGroup<PartialRegex> startOrGroup() {
		return empty().beginOrGroup();
	}

	private final PartialRegex partial;

	protected Regex(PartialRegex partial) {
		this.partial = partial;
	}

	public String getRegex() {
		return partial.getRegex();
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
		List<String> strings =  new ArrayList<>();
		strings.addAll(testAgainstEntireString(testStr, partials));
		strings.addAll(testAgainstAnywhereInString(testStr, partials));
		strings.addAll(testAgainstBeginningOfString(testStr, partials));
		strings.addAll(testAgainstEndOfString(testStr, partials));
		return strings;
	}

	private List<String> testAgainstEndOfString(String testStr, List<PartialRegex> patterns) {
		List<String> strings = new ArrayList<String>(0);
		strings.add("TESTING: " + getRegex());
		strings.add("AT END OF: " + testStr);
		for (PartialRegex pattern : patterns) {
			try {
				strings.add("TESTING: "+pattern.getRegex());
				final Regex regex = pattern.toRegex();
				final boolean result = regex.matchesEndOf(testStr);
				strings.add("RESULT: " + (result ? "found" : "FAILED"));
			} catch (Exception ex) {
				strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
			}
		}
		return strings;
	}

	private List<String> testAgainstBeginningOfString(String testStr, List<PartialRegex> patterns) {
		List<String> strings = new ArrayList<String>(0);
		strings.add("TESTING: " + getRegex());
		strings.add("FROM START OF: " + testStr);
		for (PartialRegex pattern : patterns) {
			try {
				strings.add("TESTING: "+pattern.getRegex());
				final Regex regex = pattern.toRegex();
				final boolean result = regex.matchesBeginningOf(testStr);
				strings.add("RESULT: " + (result ? "found" : "FAILED"));
			} catch (Exception ex) {
				strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
			}
		}
		return strings;
	}

	private List<String> testAgainstAnywhereInString(String testStr, List<PartialRegex> patterns) {
		List<String> strings = new ArrayList<String>(0);
		strings.add("TESTING: " + getRegex());
		strings.add("ANYWHERE IN: " + testStr);
		for (PartialRegex pattern : patterns) {
			try {
				strings.add("TESTING: "+pattern.getRegex());
				final Regex regex = pattern.toRegex();
				final boolean result = regex.matchesWithinString(testStr);
				strings.add("RESULT: " + (result ? "found" : "FAILED"));
			} catch (Exception ex) {
				strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
			}
		}
		return strings;
	}

	private List<String> testAgainstEntireString(String testStr, List<PartialRegex> patterns) {
		List<String> strings = new ArrayList<String>(0);
		strings.add("TESTING: " + getRegex());
		strings.add("AGAINST ALL OF: " + testStr);
		for (PartialRegex pattern : patterns) {
			try {
				strings.add("TESTING: "+pattern.getRegex());
				final Regex regex = pattern.toRegex();
				final boolean result = regex.matchesEntireString(testStr);
				strings.add("RESULT: " + (result ? "found" : "FAILED"));
			} catch (Exception ex) {
				strings.add("Skipping invalid regex: " + ex.getLocalizedMessage());
			}
		}
		return strings;
	}

}
