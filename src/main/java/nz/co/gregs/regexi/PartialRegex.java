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
public abstract class PartialRegex implements HasRegexFunctions<PartialRegex> {

	private Pattern compiledVersion;

	protected PartialRegex() {
	}

	/**
	 * Transforms the PartialRegex into a full Regex ready for use with matching.
	 *
	 * @return a Regex
	 */
	public Regex toRegex() {
		return new Regex(this);
	}

	@Override
	public abstract String getRegex() ;
	
	@Override
	public abstract List<PartialRegex> getRegexParts() ;

	/**
	 * Adds the regular expression to the end of current expression as a new
	 * group.
	 *
	 * <p>
 For example
 PartialRegex.startingAnywhere().addGroup(allowedValue).addGroup(separator) will addGroup
 the "separator" regular expression to the "allowedValue" expression (the
 rest of the instruction adds nothing). Assuming that allowedValue is
 "[0-9]" and separator is ":", the full regexp will be "([0-9])(:)".
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
			.digit().zeroOrMore()
			.or().literal('0').oneOrMore().notFollowedBy(Regex.startingAnywhere().digit())
			.endOrGroup();

	protected final Pattern getPattern() {
		if (compiledVersion == null) {
			final String regexp = this.getRegex();
			compiledVersion = Pattern.compile(regexp);
		}
		return compiledVersion;
	}

	protected boolean matchesEntireString(String string) {
		return getMatcher(string).matches();
	}

	/**
	 * Tests whether the supplied string matches this regex at the beginning of
	 * the string.
	 *
	 * The method works by combining {@link PartialRegex#startingFromTheBeginning() ()
	 * } with this toRegex and calling {@link #matchesWithinString(java.lang.String)
	 * }.
	 *
	 * @param string the string to test with this toRegex
	 * @return true if the beginning of the string matches this toRegex.
	 */
	protected boolean matchesBeginningOf(String string) {
		return Regex.startingFromTheBeginning().add(this).matchesWithinString(string);
	}

	/**
	 * Tests whether the supplied string matches this regex at the end of the
	 * string.
	 *
	 * The method works by combining this regex with {@link #endOfTheString() }
	 * and calling {@link #matchesWithinString(java.lang.String) }.
	 *
	 * @param string the string to test with this toRegex
	 * @return true if the end of the string matches this toRegex.
	 */
	protected boolean matchesEndOf(String string) {
		return endOfTheString().matchesWithinString(string);
	}

	protected boolean matchesWithinString(String string) {
		return getMatcher(string).find();
	}

	protected Stream<MatchResult> getMatchResultsStream(String string) {
		return getMatcher(string).results();
	}

	protected Matcher getMatcher(String string) {
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
	protected MatchResult getMatchResult(String string) {
		return getMatcher(string).toMatchResult();
	}

	protected HashMap<String, String> getAllNamedCapturesOfFirstMatchWithinString(String string) {
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
			Logger.getLogger(PartialRegex.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(PartialRegex.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(PartialRegex.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalArgumentException ex) {
			Logger.getLogger(PartialRegex.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvocationTargetException ex) {
			Logger.getLogger(PartialRegex.class.getName()).log(Level.SEVERE, null, ex);
		}
		return resultMap;
	}

	protected List<Match> getAllMatches(String string) {
		Matcher matcher = getMatcher(string);
		List<Match> matches = matcher.results().map(
				m -> Match.from(this, m)
		).collect(Collectors.toList());
		return matches;
	}

	protected Optional<Match> getFirstMatchFrom(String string) {
		Matcher matcher = getMatcher(string);
		if (matcher.find()) {
			MatchResult result = matcher.toMatchResult();
			return Optional.of(Match.from(this, result));
		}
		return Optional.empty();
	}
}
