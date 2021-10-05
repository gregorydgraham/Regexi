package nz.co.gregs.regexi.api;

/*
 * Copyright 2019 Gregory Graham.
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
import java.util.*;
import java.util.stream.Collectors;
import org.junit.Assert;
import nz.co.gregs.regexi.Match;
import nz.co.gregs.regexi.MatchedGroup;
import nz.co.gregs.regexi.Regex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

/**
 *
 * @author gregorygraham
 */
public class RegexTest {

	public RegexTest() {
	}

	@Test
	public void testFindingANegativeNumber() {
		Regex negativeInteger = Regex.startingAnywhere().negativeInteger();
		Assert.assertTrue(negativeInteger.matchesEntireString("-1"));
		Assert.assertTrue(negativeInteger.matchesWithinString("-1"));
		Assert.assertFalse(negativeInteger.matchesEntireString("1"));
		Assert.assertFalse(negativeInteger.matchesWithinString("1"));
		Assert.assertFalse(negativeInteger.matchesEntireString("below zero there are negative and -1 is the first"));
		Assert.assertTrue(negativeInteger.matchesWithinString("below zero there are negative and -1 is the first"));
	}

	@Test
	public void testFindingAPositiveNumber() {
		Regex positiveInteger = Regex.startingAnywhere().positiveInteger();
		assertThat(positiveInteger.matchesEntireString("-1"), is(false));
		assertThat(positiveInteger.matchesWithinString("-1"), is(false));
		assertThat(positiveInteger.matchesEntireString("1"), is(true));
		assertThat(positiveInteger.matchesWithinString("1"), is(true));
		assertThat(positiveInteger.matchesEntireString("+1"), is(true));
		assertThat(positiveInteger.matchesWithinString("+1"), is(true));
		assertThat(positiveInteger.matchesEntireString("below zero there are negatives and -1 is the first"), is(false));
		assertThat(positiveInteger.matchesWithinString("below zero there are negatives and -1 is the first"), is(false));
		assertThat(positiveInteger.matchesEntireString("above zero there are positives and 1 is the first"), is(false));
		assertThat(positiveInteger.matchesWithinString("above zero there are positives and 1 is the first"), is(true));
		assertThat(positiveInteger.matchesEntireString("above zero there are positives and +1 is the first"), is(false));
		assertThat(positiveInteger.matchesWithinString("above zero there are positives and +1 is the first"), is(true));
	}

	@Test
	public void testFindingPostgresIntervalValues() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		//(-?[0-9]+)([^-0-9]+)(-?[0-9]+):(-?[0-9]+):(-?[0-9]+)(\.\d+)?

		final Regex allowedValue
				= Regex.startingAnywhere()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce();

		final Regex allowedSeconds
				= allowedValue.add(
						Regex.startingAnywhere().literal(".").digits()
				).onceOrNotAtAll();

		final Regex separator
				= Regex.startingAnywhere().beginRange('0', '9').includeMinus().negated().endRange().atLeastOnce();

		Regex pattern
				= Regex.startingAnywhere()
						.add(allowedValue).add(separator)
						.add(allowedValue).literal(':')
						.add(allowedValue).literal(':')
						.add(allowedSeconds);

		assertThat(pattern.matchesWithinString("-2 days 00:00:00"), is(true));
		assertThat(pattern.matchesWithinString("2 days 00:00:00"), is(true));
		assertThat(pattern.matchesWithinString("2 days 00:00:00.0"), is(true));
		assertThat(pattern.matchesWithinString("1 days 00:00:5.5"), is(true));
		assertThat(pattern.matchesWithinString("2 days 00:00:00"), is(true));
		assertThat(pattern.matchesWithinString("1 days 00:00:5.5"), is(true));
		assertThat(pattern.matchesWithinString("0 days 00:00:-5.5"), is(true));
		assertThat(pattern.matchesWithinString("0 00:00:-5.5"), is(true));
		assertThat(pattern.matchesWithinString("00:00:-5.5"), is(false));
		assertThat(pattern.matchesWithinString("-2"), is(false));

	}

	@Test
	public void testFindingPostgresIntervalValuesWithAOneliner() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		//-?[0-9]+([^-0-9])+-?[0-9]+:{1}-?[0-9]+:{1}-?[0-9]+(\.\d+)?
		Regex pattern
				= Regex.startingAnywhere()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce()
						.beginRange('0', '9')
						.includeMinus()
						.negated()
						.endRange()
						.atLeastOnce()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce()
						.literal(':').once()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce()
						.literal(':').once()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce().add(Regex.startingAnywhere().literal(".").digits()
				).onceOrNotAtAll();

		assertThat(pattern.matchesWithinString("-2 days 00:00:00"), is(true));
		assertThat(pattern.matchesWithinString("2 days 00:00:00"), is(true));
		assertThat(pattern.matchesWithinString("2 days 00:00:00.0"), is(true));
		assertThat(pattern.matchesWithinString("1 days 00:00:5.5"), is(true));
		assertThat(pattern.matchesWithinString("2 days 00:00:00"), is(true));
		assertThat(pattern.matchesWithinString("1 days 00:00:5.5"), is(true));
		assertThat(pattern.matchesWithinString("0 days 00:00:-5.5"), is(true));
		assertThat(pattern.matchesWithinString("0 00:00:-5.5"), is(true));
		assertThat(pattern.matchesWithinString("00:00:-5.5"), is(false));
		assertThat(pattern.matchesWithinString("-2"), is(false));

	}

	@Test
	public void testGroupBuilding() {

		Regex regex
				= Regex.startOrGroup().literal("Amy").or().literal("Bob").or().literal("Charlie").endOrGroup();

		assertThat(regex.matchesWithinString("Amy"), is(true));
		assertThat(regex.matchesWithinString("Bob"), is(true));
		assertThat(regex.matchesWithinString("Charlie"), is(true));
		assertThat(regex.matchesEntireString("Amy"), is(true));
		assertThat(regex.matchesEntireString("Bob"), is(true));
		assertThat(regex.matchesEntireString("Charlie"), is(true));
		assertThat(regex.matchesWithinString("David"), is(false));
		assertThat(regex.matchesWithinString("Emma"), is(false));
		assertThat(regex.matchesWithinString("Try with Amy in the middle"), is(true));
		assertThat(regex.matchesWithinString("End it with Bob"), is(true));
		assertThat(regex.matchesWithinString("Charlie at the start"), is(true));
		assertThat(regex.matchesEntireString("Try with Amy in the middle"), is(false));
		assertThat(regex.matchesEntireString("End it with Bob"), is(false));
		assertThat(regex.matchesEntireString("Charlie at the start"), is(false));
		assertThat(regex.matchesWithinString("Still can't find David"), is(false));
		assertThat(regex.matchesWithinString("Emma doesn't do any better"), is(false));

		// Check that Regex.anyOf() is the same
		regex = Regex.startingAnywhere().anyOf("Amy", "Bob", "Charlie");

		assertThat(regex.matchesWithinString("Amy"), is(true));
		assertThat(regex.matchesWithinString("Bob"), is(true));
		assertThat(regex.matchesWithinString("Charlie"), is(true));
		assertThat(regex.matchesEntireString("Amy"), is(true));
		assertThat(regex.matchesEntireString("Bob"), is(true));
		assertThat(regex.matchesEntireString("Charlie"), is(true));
		assertThat(regex.matchesWithinString("David"), is(false));
		assertThat(regex.matchesWithinString("Emma"), is(false));
		assertThat(regex.matchesWithinString("Try with Amy in the middle"), is(true));
		assertThat(regex.matchesWithinString("End it with Bob"), is(true));
		assertThat(regex.matchesWithinString("Charlie at the start"), is(true));
		assertThat(regex.matchesEntireString("Try with Amy in the middle"), is(false));
		assertThat(regex.matchesEntireString("End it with Bob"), is(false));
		assertThat(regex.matchesEntireString("Charlie at the start"), is(false));
		assertThat(regex.matchesWithinString("Still can't find David"), is(false));
		assertThat(regex.matchesWithinString("Emma doesn't do any better"), is(false));

	}

	@Test
	public void testNumberElement() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex pattern
				= Regex.startingAnywhere()
						.number().once();
		System.out.println(pattern.getRegex());
		assertThat(pattern.getRegex(), is("(([-+]?\\b([1-9]+\\d*|0+(?!\\d)))(\\.{1}(\\d+))?){1}"));

		assertThat(pattern.matchesWithinString("before -1 after"), is(true));
		assertThat(pattern.matchesWithinString("before -1m"), is(true));
		assertThat(pattern.matchesWithinString("before 2 after"), is(true));
		assertThat(pattern.matchesWithinString("before -234 after"), is(true));
		assertThat(pattern.matchesWithinString("before +4 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4 after"), is(true));
		assertThat(pattern.matchesWithinString("before 4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.05 after"), is(true));

		assertThat(pattern.matchesWithinString("before 2E10"), is(true));
		assertThat(pattern.getAllMatches("before 2E10").size(), is(1));
		assertThat(pattern.getAllMatches("before 2E10").get(0).getEntireMatch(), is("2"));

		assertThat(pattern.matchesWithinString("before 2*E10"), is(true));
		assertThat(pattern.getAllMatches("before 2*E10").size(), is(1));
		assertThat(pattern.getAllMatches("before 2*E10").get(0).getEntireMatch(), is("2"));

		assertThat(pattern.matchesWithinString("before 0 after"), is(true));

		assertThat(pattern.matchesWithinString("before 0.0 after"), is(true));
		assertThat(pattern.getAllMatches("before 0.0 after").size(), is(1));
		assertThat(pattern.getAllMatches("before 0.0 after").get(0).getEntireMatch(), is("0.0"));

		assertThat(pattern.matchesWithinString("before 02 after"), is(false));
		assertThat(pattern.matchesWithinString("before -0234 after"), is(false));
		assertThat(pattern.matchesWithinString("before 004 after"), is(false));
		assertThat(pattern.matchesWithinString("before _4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4after"), is(false));

	}

	@Test
	public void testCharactersWrappedBy() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex pattern
				= Regex.startingAnywhere()
						.charactersWrappedBy('\'').once();
		System.out.println(pattern.getRegex());

		assertThat(pattern.matchesWithinString("'-1' after"), is(true));
		assertThat(pattern.matchesWithinString("before '-1m'"), is(true));
		assertThat(pattern.matchesWithinString("before '2' after"), is(true));
		assertThat(pattern.matchesWithinString("before '-234' after"), is(true));
		assertThat(pattern.matchesWithinString("before 'mid' after"), is(true));
		assertThat(pattern.matchesWithinString("before 'mid' 'mid again' after"), is(true));

		assertThat(pattern.matchesEntireString("'mid'"), is(true));

		assertThat(pattern.matchesWithinString("'02 after"), is(false));
		assertThat(pattern.matchesWithinString("-0234' after"), is(false));
		assertThat(pattern.matchesWithinString("before '004 after"), is(false));
		assertThat(pattern.matchesWithinString("before _4' after"), is(false));
		assertThat(pattern.matchesWithinString("before \"A4\" after"), is(false));
		assertThat(pattern.matchesWithinString("before A4after"), is(false));

		assertThat(pattern.matchesEntireString("'mid'  "), is(false));
		assertThat(pattern.matchesEntireString("  'mid'"), is(false));
		assertThat(pattern.matchesEntireString("  'mid'  "), is(false));
		assertThat(pattern.matchesEntireString("'beginning''end'"), is(false));
	}

	@Test
	public void testCharactersWrappedByWithStarterAndEnder() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex pattern
				= Regex.startingAnywhere()
						.charactersWrappedBy('(', ')').once();
		System.out.println(pattern.getRegex());

		assertThat(pattern.matchesWithinString("(-1) after"), is(true));
		assertThat(pattern.matchesWithinString("before (-1m)"), is(true));
		assertThat(pattern.matchesWithinString("before (2) after"), is(true));
		assertThat(pattern.matchesWithinString("before (-234) after"), is(true));
		assertThat(pattern.matchesWithinString("before (mid) after"), is(true));
		assertThat(pattern.matchesWithinString("before (mid) (mid again) after"), is(true));

		assertThat(pattern.matchesEntireString("(mid)"), is(true));

		assertThat(pattern.matchesWithinString("(02 after"), is(false));
		assertThat(pattern.matchesWithinString("-0234) after"), is(false));
		assertThat(pattern.matchesWithinString("before (004 after"), is(false));
		assertThat(pattern.matchesWithinString("before _4) after"), is(false));
		assertThat(pattern.matchesWithinString("before \"A4\" after"), is(false));
		assertThat(pattern.matchesWithinString("before A4after"), is(false));

		assertThat(pattern.matchesEntireString("(mid)  "), is(false));
		assertThat(pattern.matchesEntireString("  (mid)"), is(false));
		assertThat(pattern.matchesEntireString("  (mid)  "), is(false));
		assertThat(pattern.matchesEntireString("(beginning)(end)"), is(false));
	}

	@Test
	public void testCharactersWrappedByWithStringStarterAndEnder() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex regex
				= Regex.startingAnywhere()
						.charactersWrappedBy("<<", ">>").once();
		final String pattern = regex.getRegex();
		System.out.println(pattern);
		assertThat(pattern, is("(<<(((?!>>).)*)>>){1}"));

		assertThat(regex.matchesWithinString("<<-1>> after"), is(true));
		assertThat(regex.matchesWithinString("before <<-1m>>"), is(true));
		assertThat(regex.matchesWithinString("before <<2>> after"), is(true));
		assertThat(regex.matchesWithinString("before <<-234>> after"), is(true));
		assertThat(regex.matchesWithinString("before <<mid>> after"), is(true));
		assertThat(regex.matchesWithinString("before <<mid>> <<mid again>> after"), is(true));

		assertThat(regex.matchesEntireString("<<mid>>"), is(true));

		assertThat(regex.matchesWithinString("<<02 after"), is(false));
		assertThat(regex.matchesWithinString("-0234>> after"), is(false));
		assertThat(regex.matchesWithinString("before <<004 after"), is(false));
		assertThat(regex.matchesWithinString("before _4>> after"), is(false));
		assertThat(regex.matchesWithinString("before \"A4\" after"), is(false));
		assertThat(regex.matchesWithinString("before A4after"), is(false));

		assertThat(regex.matchesEntireString("<<mid>>  "), is(false));
		assertThat(regex.matchesEntireString("  <<mid>>"), is(false));
		assertThat(regex.matchesEntireString("  <<mid>>  "), is(false));
		regex.testAgainst("<<beginning>><<end>>").stream().forEachOrdered(s -> System.out.println(s));
		assertThat(regex.matchesEntireString("<<beginning>><<end>>"), is(false));
	}

	@Test
	public void testOrGroup() {
		Regex regex = Regex.empty().beginOrGroup().literal("alice").or().literal("bob").or().literal("carol").or().literal("dan").endOrGroup();

		final String expectedRegex = "(alice|bob|carol|dan)";
		assertThat(regex.getRegex(), is(expectedRegex));
	}

	@Test
	public void testLiteralEscapesRegularExpressionCharacters() {
		final String literal = "[a-b]*$(alice|bob).^?";
		Regex regex = Regex.empty().literal(literal).once();

		assertThat(regex.getRegex(), is("\\[a-b]\\*\\$\\(alice|bob\\)\\.\\^\\?{1}"));

		assertThat(regex.matchesWithinString(literal), is(true));
		assertThat(regex.matchesWithinString("abcboba^?"), is(false));
		assertThat(regex.matchesWithinString("a"), is(false));
	}

	@Test
	public void testNumberIncludingScientificNotationElement() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex pattern
				= Regex.startingAnywhere()
						.numberIncludingScientificNotation().once();
		System.out.println(pattern.getRegex());
//		assertThat(pattern.getRegex(),is("((([-+]?\\b([1-9]+\\d*|0+(?!\\d)))(\\.{1}(\\d+))?){1}([Ee][-+]?([1-9]+\\d*|0+(?!\\d)){1}(\\.{1}(\\d+))?)?){1}"));

		assertThat(pattern.matchesWithinString("before -1 after"), is(true));
		assertThat(pattern.matchesWithinString("before 2 after"), is(true));
		assertThat(pattern.matchesWithinString("before -234 after"), is(true));
		assertThat(pattern.matchesWithinString("before +4 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4 after"), is(true));
		assertThat(pattern.matchesWithinString("before 4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.05after"), is(true));
		assertThat(pattern.matchesWithinString("before 2*E10"), is(true)); // bit of an odd one: the 2 matches
		//2E10, -2.89E-7.98, or 1.37e+15
		assertThat(pattern.getAllMatches("before 2*E10").size(), is(1));
		assertThat(pattern.getAllMatches("before 2*E10").get(0).getEntireMatch(), is("2"));

		assertThat(pattern.matchesWithinString("before 2E10 after"), is(true));
		assertThat(pattern.getAllMatches("before 2E10 after").size(), is(1));
		assertThat(pattern.getAllMatches("before 2E10 after").get(0).getEntireMatch(), is("2E10"));

		assertThat(pattern.matchesWithinString("before -2.89E-7.98 after"), is(true));
		assertThat(pattern.getAllMatches("before -2.89E-7.98 after").size(), is(1));
		assertThat(pattern.getAllMatches("before -2.89E-7.98 after").get(0).getEntireMatch(), is("-2.89E-7.98"));

		assertThat(pattern.matchesWithinString("before 1.37e+15 after"), is(true));
		assertThat(pattern.getAllMatches("before 1.37e+15 after").size(), is(1));
		assertThat(pattern.getAllMatches("before 1.37e+15 after").get(0).getEntireMatch(), is("1.37e+15"));

		assertThat(pattern.matchesWithinString("before 2E10"), is(true));
		assertThat(pattern.matchesWithinString("before -2.89E-7.98"), is(true));
		assertThat(pattern.matchesWithinString("before 1.37e+15"), is(true));
		assertThat(pattern.matchesWithinString("2E10 after"), is(true));
		assertThat(pattern.matchesWithinString("-2.89E-7.98 after"), is(true));
		assertThat(pattern.matchesWithinString("1.37e+15 after"), is(true));

		assertThat(pattern.matchesWithinString("before 02 after"), is(false));
		assertThat(pattern.matchesWithinString("before -0234 after"), is(false));
		assertThat(pattern.matchesWithinString("before 004 after"), is(false));
		assertThat(pattern.matchesWithinString("before _4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4after"), is(false));

		assertThat(pattern.matchesWithinString("INTERVAL -1.999999999946489E-6 SECOND"), is(true));
		final List<Match> allMatches = pattern.getAllMatches("INTERVAL -1.999999999946489E-6 SECOND");
		assertThat(allMatches.size(), is(1));
		final Double value = Double.valueOf(allMatches.get(0).getEntireMatch());
		assertThat(value, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(value * 1000000), is(-2L));

	}

	@Test
	public void testAllGroups() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5

		Regex pattern
				= Regex.startingFromTheBeginning()
						.literalCaseInsensitive("interval").once()
						.space().once()
						.numberIncludingScientificNotation().once()
						.space().once()
						.beginOrGroup().word().endOrGroup()
						.endOfInput();
		System.out.println(pattern.getRegex());
		// ^((?i)interval(?-i)){1} {1}((([-+]?\b([1-9]+\d*|0+(?!\d)))(\.{1}(\d+))?){1}([Ee][-+]?([1-9]+\d*|0+(?!\d)){1}(\.{1}(\d+))?)?){1} {1}((\w+))$
		// ^^((?i)interval(?-i)){1} {1}((([-+]?\b([1-9]+\d*|0+(?!\d)))(\.{1}(\d+))?){1}([Ee][-+]?([1-9]+\d*|0+(?!\d)){1}(\.{1}(\d+))?)?){1} {1}((\w+))$		
		assertThat(pattern.getRegex(), is("^((?i)interval(?-i)){1} {1}((([-+]?\\b([1-9]+\\d*|0+(?!\\d)))(\\.{1}(\\d+))?){1}([Ee][-+]?([1-9]+\\d*|0+(?!\\d)){1}(\\.{1}(\\d+))?)?){1} {1}((\\w+))$"));

		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND";
		assertThat(pattern.matchesEntireString(intervalString), is(true));

		final Optional<Match> firstMatch = pattern
				.getFirstMatchFrom(intervalString);

		if (!firstMatch.isPresent()) {
			Assert.fail("Match not found");
		} else {
			List<MatchedGroup> allGroups = firstMatch.get().allGroups();
			allGroups.stream().forEachOrdered(s -> System.out.println(s));
			assertThat(allGroups.size(), is(13));

			final String contents = allGroups.get(2).getContents();
			assertThat(contents, is("-1.999999999946489E-6"));
			final Double value = Double.valueOf(contents);
			assertThat(value, is(Double.valueOf("-1.999999999946489E-6")));
			assertThat(Math.round(value * 1000000), is(-2L));
		}
	}

	@Test
	public void testNamedCaptures() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ^(?<interval>((?i)interval(?-i)){1}) {1}(?<value>([-+]?\b[1-9]+\d*(\.{1}\d+)?(((?i)E(?-i)){1}[-+]?[1-9]+\d*(\.{1}\d+)?)?(?!\S)){1}) {1}(?<unit>\w+)$
		Regex intervalRegex
				= Regex.startingFromTheBeginning()
						.beginNamedCapture("interval").literalCaseInsensitive("interval").once().endNamedCapture()
						.space().once()
						.beginNamedCapture("value").numberIncludingScientificNotation().once().endNamedCapture()
						.space().once()
						.beginNamedCapture("unit").word().endNamedCapture()
						.endOfInput();

		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND";
		assertThat(intervalRegex.matchesEntireString(intervalString), is(true));

		final HashMap<String, String> allGroups = intervalRegex.getAllNamedCapturesOfFirstMatchWithinString(intervalString);
		assertThat(allGroups.size(), is(3));

		final Double value = Double.valueOf(allGroups.get("value"));
		assertThat(value, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(value * 1000000), is(-2L));

		final HashMap<String, String> allNamedCaptures = intervalRegex.getAllMatches(intervalString).get(0).getAllNamedCaptures();
		final String intervalStr = allNamedCaptures.get("interval");
		assertThat(intervalStr, is("INTERVAL"));
		final String valueStr = allNamedCaptures.get("value");
		assertThat(valueStr, is("-1.999999999946489E-6"));
		final String unitStr = allNamedCaptures.get("unit");
		assertThat(unitStr, is("SECOND"));

	}

	@Test
	public void testNamedBackReferences() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ^(?<interval>((?i)interval(?-i)){1}) {1}(?<value>([-+]?\b[1-9]+\d*(\.{1}\d+)?(((?i)E(?-i)){1}[-+]?[1-9]+\d*(\.{1}\d+)?)?(?!\S)){1}) {1}(?<unit>\w+)$
		Regex intervalRegex
				= Regex.startingFromTheBeginning()
						.beginNamedCapture("interval").literalCaseInsensitive("interval").once().endNamedCapture()
						.space().once()
						.beginNamedCapture("value").numberIncludingScientificNotation().once().endNamedCapture()
						.space().once()
						.beginNamedCapture("unit").word().endNamedCapture()
						.space().once()
						.beginNamedCapture("secondValue").namedBackReference("value").endNamedCapture()
						.endOfInput();

		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND";
		assertThat(intervalRegex.matchesEntireString(intervalString), is(false));

		intervalString = "INTERVAL -1.999999999946489E-6 SECOND -1.999999999946489E-6";
		assertThat(intervalRegex.matchesEntireString(intervalString), is(true));

		final HashMap<String, String> allGroups = intervalRegex.getAllNamedCapturesOfFirstMatchWithinString(intervalString);
		assertThat(allGroups.size(), is(4));

		final Double value = Double.valueOf(allGroups.get("value"));
		assertThat(value, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(value * 1000000), is(-2L));

		final Double secondValue = Double.valueOf(allGroups.get("secondValue"));
		assertThat(secondValue, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(secondValue * 1000000), is(-2L));
	}

	@Test
	public void testNumberedBackReferencesWithSimpleCase() {
		final String VALUE_NAME = "value";
		final String INTERVAL_NAME = "interval";
		final String UNIT_NAME = "unit";
		final String BACKREFERENCE_NAME = "backReference";
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ^(?<interval>((?i)interval(?-i)){1}) {1}(?<value>([-+]?\b[1-9]+\d*(\.{1}\d+)?(((?i)E(?-i)){1}[-+]?[1-9]+\d*(\.{1}\d+)?)?(?!\S)){1}) {1}(?<unit>\w+)$
		Regex intervalRegex
				= Regex.startingFromTheBeginning()
						.beginNamedCapture(INTERVAL_NAME).literalCaseInsensitive("interval").once().endNamedCapture()
						.space().once()
						.beginNamedCapture(VALUE_NAME).numberIncludingScientificNotation().once().endNamedCapture()
						.space().once()
						.beginNamedCapture(UNIT_NAME).word().endNamedCapture()
						.space().once()
						.beginNamedCapture(BACKREFERENCE_NAME).numberedBackReference(1).endNamedCapture()
						.endOfInput();

		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND";
		assertThat(intervalRegex.matchesEntireString(intervalString), is(false));
		intervalString = "INTERVAL -1.999999999946489E-6 SECOND INTERVAL";
		assertThat(intervalRegex.matchesEntireString(intervalString), is(true));

		final HashMap<String, String> allNamedCaptures = intervalRegex.getAllNamedCapturesOfFirstMatchWithinString(intervalString);
		assertThat(allNamedCaptures.size(), is(4));

		final String firstValue = allNamedCaptures.get(INTERVAL_NAME);
		assertThat(firstValue, is("INTERVAL"));

		final String fourthValue = allNamedCaptures.get(BACKREFERENCE_NAME);
		assertThat(fourthValue, is("INTERVAL"));

		final Double secondValue = Double.valueOf(allNamedCaptures.get(VALUE_NAME));
		assertThat(secondValue, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(secondValue * 1000000), is(-2L));
	}

	@Test
	public void testNumberedBackReferencesWithManyGroups() {
		Regex regex
				= Regex.startingFromTheBeginning()
						.beginNamedCapture("interval").literalCaseInsensitive("interval").once().endNamedCapture()
						.space().once()
						.beginNamedCapture("value").numberIncludingScientificNotation().once().endNamedCapture()
						.space().once()
						.beginNamedCapture("unit").word().endNamedCapture()
						.space().once()
						.beginNamedCapture("secondValue").numberedBackReference(4).endNamedCapture().optionalMany()
						.endOfInput();

		String string = "INTERVAL -1.999999999946489E-6 SECOND -1.999999999946489E-6";
		assertThat(regex.matchesEntireString(string), is(true));

		final HashMap<String, String> allGroups = regex.getAllNamedCapturesOfFirstMatchWithinString(string);
		assertThat(allGroups.size(), is(4));

		final Double value = Double.valueOf(allGroups.get("value"));
		assertThat(value, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(value * 1000000), is(-2L));

		final Double secondValue = Double.valueOf(allGroups.get("secondValue"));
		assertThat(secondValue, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(secondValue * 1000000), is(-2L));
	}

	@Test
	public void testNumberLike() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex pattern
				= Regex.startingAnywhere()
						.numberLike().once();

		assertThat(pattern.matchesWithinString("before -1 after"), is(true));
		assertThat(pattern.matchesWithinString("before 2 after"), is(true));
		assertThat(pattern.matchesWithinString("before -234 after"), is(true));
		assertThat(pattern.matchesWithinString("before +4 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4 after"), is(true));
		assertThat(pattern.matchesWithinString("before 4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before 02 after"), is(true));
		assertThat(pattern.matchesWithinString("before -0234 after"), is(true));
		assertThat(pattern.matchesWithinString("before 004 after"), is(true));
		assertThat(pattern.matchesWithinString("before _4 after"), is(true));
		assertThat(pattern.matchesWithinString("before A4 after"), is(true));
		assertThat(pattern.matchesWithinString("before A4after"), is(true));
		assertThat(pattern.matchesWithinString("before 2*E10"), is(true));
		assertThat(pattern.matchesWithinString("before"), is(false));

	}

	@Test
	public void testGetAllMatches() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex numberlikeRegex
				= Regex.startingAnywhere()
						.numberLike().once();

		List<Match> matches = numberlikeRegex.getAllMatches("-1 2 -234 +4 -4 4.5 FAIL 02 -0234 004 _4 A4");
		assertThat(matches.size(), is(11));
		for (Match match : matches) {
			assertThat(match.getEntireMatch(), isOneOf("-1", "2", "-234", "+4", "-4", "4.5", "02", "-0234", "004", "4", "4"));
		}
		List<String> collected = matches.stream().map(v -> v.getEntireMatch()).collect(Collectors.toList());
		assertThat(collected.get(0), is("-1"));
		assertThat(collected.get(1), is("2"));
		assertThat(collected.get(2), is("-234"));
		assertThat(collected.get(3), is("+4"));
		assertThat(collected.get(4), is("-4"));
		assertThat(collected.get(5), is("4.5"));
		assertThat(collected.get(6), is("02"));
		assertThat(collected.get(7), is("-0234"));
		assertThat(collected.get(8), is("004"));
		assertThat(collected.get(9), is("4"));
		assertThat(collected.get(10), is("4"));

		Regex numberRegex
				= Regex.startingAnywhere()
						.number().once();

		matches = numberRegex.getAllMatches("-1 2 -234 +4 -4 4.5 0 0.0 FAIL 02 -0234 004 _4 A4");
		assertThat(matches.size(), is(8));

		for (Match match : matches) {
			assertThat(match.getEntireMatch(), isOneOf("-1", "2", "-234", "+4", "-4", "4.5", "0", "0.0"));
		}
		collected = matches.stream().map(v -> v.getEntireMatch()).collect(Collectors.toList());
		assertThat(collected.get(0), is("-1"));
		assertThat(collected.get(1), is("2"));
		assertThat(collected.get(2), is("-234"));
		assertThat(collected.get(3), is("+4"));
		assertThat(collected.get(4), is("-4"));
		assertThat(collected.get(5), is("4.5"));
		assertThat(collected.get(6), is("0"));
		assertThat(collected.get(7), is("0.0"));
	}

	@Test
	public void testCaseInsensitiveAndEndOfString() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex regex
				= Regex.startingAnywhere()
						.wordBoundary()
						.beginCaseInsensitiveSection()
						.literal("day").once()
						.literal("s").onceOrNotAtAll()
						.endCaseInsensitiveSection()
						.wordBoundary()
						.endOfTheString();

		assertThat(regex.matchesWithinString("day"), is(true));
		assertThat(regex.matchesWithinString("days"), is(true));
		assertThat(regex.matchesWithinString("DAY"), is(true));
		assertThat(regex.matchesWithinString("DAYS"), is(true));
		assertThat(regex.matchesWithinString("before day"), is(true));
		assertThat(regex.matchesWithinString("before days"), is(true));
		assertThat(regex.matchesWithinString("before middleday"), is(false));
		assertThat(regex.matchesWithinString("before middledays"), is(false));
		assertThat(regex.matchesWithinString("before day after"), is(false));
		assertThat(regex.matchesWithinString("before days after"), is(false));
		assertThat(regex.matchesWithinString("day after"), is(false));
		assertThat(regex.matchesWithinString("days after"), is(false));
		assertThat(regex.matchesWithinString("before"), is(false));
	}

	@Test
	public void testLiteralCaseInsensitive() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex regex
				= Regex.startingAnywhere()
						.wordBoundary()
						.literalCaseInsensitive("day").once()
						.literalCaseInsensitive("s").onceOrNotAtAll()
						.wordBoundary()
						.endOfTheString();
		System.out.println(regex.getRegex());
		assertThat(regex.getRegex(), is("\\b((?i)day(?-i)){1}((?i)s(?-i))?\\b$"));

		assertThat(regex.matchesWithinString("day"), is(true));
		assertThat(regex.matchesWithinString("days"), is(true));
		assertThat(regex.matchesWithinString("DAY"), is(true));
		assertThat(regex.matchesWithinString("DAYS"), is(true));
		assertThat(regex.matchesWithinString("before day"), is(true));
		assertThat(regex.matchesWithinString("before days"), is(true));
		assertThat(regex.matchesWithinString("before middleday"), is(false));
		assertThat(regex.matchesWithinString("before middledays"), is(false));
		assertThat(regex.matchesWithinString("before day after"), is(false));
		assertThat(regex.matchesWithinString("before days after"), is(false));
		assertThat(regex.matchesWithinString("day after"), is(false));
		assertThat(regex.matchesWithinString("days after"), is(false));
		assertThat(regex.matchesWithinString("before"), is(false));
	}

	@Test
	public void testLotsOfMatchesAndNamedGroups() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ^(?<interval>((?i)interval(?-i)){1}) {1}(?<value>([-+]?\b[1-9]+\d*(\.{1}\d+)?(((?i)E(?-i)){1}[-+]?[1-9]+\d*(\.{1}\d+)?)?(?!\S)){1}) {1}(?<unit>\w+)$
		Regex regex
				= Regex.startingAnywhere()
						.beginNamedCapture("interval").literalCaseInsensitive("interval").once().endNamedCapture()
						.space().once()
						.beginNamedCapture("value").numberIncludingScientificNotation().once().endNamedCapture()
						.space().once()
						.beginNamedCapture("unit")
						.beginCaseInsensitiveSection()
						.anyOf("DAY", "HOUR", "MINUTE", "SECOND").once().literal("S").onceOrNotAtAll()
						.endCaseInsensitiveSection()
						.endNamedCapture();

		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND, INTERVAL 4 YEARS, INTERVAL 2 DAY, interval -34 hour, interval 56 minutes";
		assertThat(regex.matchesWithinString(intervalString), is(true));

		final List<Match> allMatches = regex.getAllMatches(intervalString);
		assertThat(allMatches.size(), is(4));

		for (var match : allMatches) {
			final HashMap<String, String> allNamedCaptures = match.getAllNamedCaptures();
			assertThat(allNamedCaptures.size(), is(3));
			final Double value = Double.valueOf(allNamedCaptures.get("value"));
			assertThat(Math.abs(Math.round(value * 1000000)), isOneOf(2L, 2000000L, 34000000L, 56000000L));
			assertThat(match.getNamedCapture("unit").toLowerCase(), isOneOf("second", "day", "hour", "minutes"));
		}
	}

	@Test
	public void testEasyEndsWithMethod() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		var regex
				= Regex.startingAnywhere()
						.wordBoundary()
						.beginCaseInsensitiveSection()
						.literal("day").once()
						.literal("s").onceOrNotAtAll()
						.endCaseInsensitiveSection()
						.wordBoundary();

		assertThat(regex.matchesEndOf("day"), is(true));
		assertThat(regex.matchesEndOf("days"), is(true));
		assertThat(regex.matchesEndOf("DAY"), is(true));
		assertThat(regex.matchesEndOf("DAYS"), is(true));
		assertThat(regex.matchesEndOf("before day"), is(true));
		assertThat(regex.matchesEndOf("before days"), is(true));
		assertThat(regex.matchesEndOf("before middleday"), is(false));
		assertThat(regex.matchesEndOf("before middledays"), is(false));
		assertThat(regex.matchesEndOf("before day after"), is(false));
		assertThat(regex.matchesEndOf("before days after"), is(false));
		assertThat(regex.matchesEndOf("day after"), is(false));
		assertThat(regex.matchesEndOf("days after"), is(false));
		assertThat(regex.matchesEndOf("before"), is(false));
	}

	@Test
	public void testEasyBeginsWithMethod() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		var regex
				= Regex.startingAnywhere()
						.wordBoundary()
						.beginCaseInsensitiveSection()
						.literal("day").once()
						.literal("s").onceOrNotAtAll()
						.endCaseInsensitiveSection()
						.wordBoundary();

		assertThat(regex.matchesBeginningOf("day"), is(true));
		assertThat(regex.matchesBeginningOf("days"), is(true));
		assertThat(regex.matchesBeginningOf("DAY"), is(true));
		assertThat(regex.matchesBeginningOf("DAYS"), is(true));
		assertThat(regex.matchesBeginningOf("before day"), is(false));
		assertThat(regex.matchesBeginningOf("before days"), is(false));
		assertThat(regex.matchesBeginningOf("before middleday"), is(false));
		assertThat(regex.matchesBeginningOf("before middledays"), is(false));
		assertThat(regex.matchesBeginningOf("before day after"), is(false));
		assertThat(regex.matchesBeginningOf("before days after"), is(false));
		assertThat(regex.matchesBeginningOf("day after"), is(true));
		assertThat(regex.matchesBeginningOf("days after"), is(true));
		assertThat(regex.matchesBeginningOf("daysmiddle after"), is(false));
		assertThat(regex.matchesBeginningOf("before"), is(false));
	}

	@Test
	public void testShouldMatch() {
		Regex regex
				= Regex.startingAnywhere()
						.beginCaseInsensitiveSection().literal("INTERVAL ").endCaseInsensitiveSection().onceOrNotAtAll()
						.literal("'").onceOrNotAtAll()
						.beginNamedCapture("days").numberLike().onceOrNotAtAll().endNamedCapture()
						.beginGroup().space().once()
						.beginCaseInsensitiveSection().literal("day").once().literal('s').onceOrNotAtAll().endCaseInsensitiveSection()
						.onceOrNotAtAll().space().onceOrNotAtAll().endGroup().onceOrNotAtAll()
						.beginNamedCapture("hours").numberLike().once().endNamedCapture()
						.literal(":")
						.beginNamedCapture("minutes").numberLike().once().endNamedCapture()
						.literal(":")
						.beginNamedCapture("seconds").numberLikeIncludingScientificNotation().once().endNamedCapture()
						.beginNamedCapture("nanos").number().onceOrNotAtAll().endNamedCapture()
						.literal("'").onceOrNotAtAll();

		shouldMatchTests(regex, "0 -2:0:0.0 DAY TO SECOND", "0", "-2", "0", "0.0", "");
		shouldMatchTests(regex, "0 day -2:0:0.0 DAY TO SECOND", "0", "-2", "0", "0.0", "");
		shouldMatchTests(regex, "0 DAYS -2:0:0.0 DAY TO SECOND", "0", "-2", "0", "0.0", "");
		shouldMatchTests(regex, "INTERval -1 DAYS 2:3:4.5 DAY TO SECOND", "-1", "2", "3", "4.5", "");
		shouldMatchTests(regex, "-2:0:0.0 DAY TO SECOND", "", "-2", "0", "0.0", "");
		shouldMatchTests(regex, "INTERVAL 0 -2:0:0.0 DAY TO SECOND", "0", "-2", "0", "0.0", "");
		shouldMatchTests(regex, "INTERVAL -20 0:0:0.0 DAY TO SECOND", "-20", "0", "0", "0.0", "");
		shouldMatchTests(regex, "INTERVAL -20:0:0:0 DAY TO SECOND", "", "-20", "0", "0", "");
		shouldMatchTests(regex, "INTERVAL 0:-2:0 DAY TO SECOND", "", "0", "-2", "0", "");
		shouldMatchTests(regex, "INTERVAL 0:0:-0.1 DAY TO SECOND", "", "0", "0", "-0.1", "");
		shouldMatchTests(regex, "INTERVAL 0:0:-2e-9.2 DAY TO SECOND", "", "0", "0", "-2e-9.2", "");
		shouldMatchTests(regex, "INTERVAL '-0 00:02:00' DAY TO SECOND", "-0", "00", "02", "00", "");
		shouldMatchTests(regex, "INTERVAL '5 04:03:02' DAY TO SECOND", "5", "04", "03", "02", "");

	}

	private void shouldMatchTests(final Regex regex, String testStr, String days, String hours, String minutes, String seconds, String nanos) {
//		assertThat(regex.matchesWithinString(testStr), is(true));
		if (regex.matchesWithinString(testStr)) {
			Optional<Match> optional = regex.getFirstMatchFrom(testStr);
			if (optional.isPresent()) {
				Match match = optional.get();

				assertThat(match.getNamedCapture("days"), is(days));
				assertThat(match.getNamedCapture("hours"), is(hours));
				assertThat(match.getNamedCapture("minutes"), is(minutes));
				assertThat(match.getNamedCapture("seconds"), is(seconds));
				assertThat(match.getNamedCapture("nanos"), is(nanos));
			} else {
				regex.testAgainst(testStr).stream().forEachOrdered(s -> System.out.println(s));
				Assert.fail("Match Failed");
			}
		} else {
			regex.testAgainst(testStr).stream().forEachOrdered(s -> System.out.println(s));
			Assert.fail("Match Failed");
		}
	}
}
