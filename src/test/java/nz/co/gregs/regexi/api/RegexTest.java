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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nz.co.gregs.regexi.Match;
import nz.co.gregs.regexi.Regex;
import nz.co.gregs.regexi.RegexBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Assert;
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
		Regex negativeInteger = RegexBuilder.startingAnywhere().negativeInteger();
		Assert.assertTrue(negativeInteger.matchesEntireString("-1"));
		Assert.assertTrue(negativeInteger.matchesWithinString("-1"));
		Assert.assertFalse(negativeInteger.matchesEntireString("1"));
		Assert.assertFalse(negativeInteger.matchesWithinString("1"));
		Assert.assertFalse(negativeInteger.matchesEntireString("below zero there are negative and -1 is the first"));
		Assert.assertTrue(negativeInteger.matchesWithinString("below zero there are negative and -1 is the first"));
	}

	@Test
	public void testFindingAPositiveNumber() {
		Regex positiveInteger = RegexBuilder.startingAnywhere().positiveInteger();
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
				= RegexBuilder.startingAnywhere()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce();

		final Regex allowedSeconds
				= allowedValue.add(
						RegexBuilder.startingAnywhere().dot().digits()
				).onceOrNotAtAll();

		final Regex separator
				= RegexBuilder.startingAnywhere().beginRange('0', '9').includeMinus().negated().endRange().atLeastOnce();

		Regex pattern
				= RegexBuilder.startingAnywhere()
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
				= RegexBuilder.startingAnywhere()
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
						.anyCharacterBetween('0', '9').atLeastOnce().add(RegexBuilder.startingAnywhere().dot().digits()
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
				= RegexBuilder.startOrGroup().literal("Amy").or().literal("Bob").or().literal("Charlie").endOrGroup();

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
		regex = RegexBuilder.startingAnywhere().anyOf("Amy", "Bob", "Charlie");

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
				= RegexBuilder.startingAnywhere()
						.number().once();

		System.out.println("REGEX: " + pattern.getRegex());
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
		assertThat(pattern.matchesWithinString("before 2*E10"), is(true));
		assertThat(pattern.matchesWithinString("before 0 after"), is(true));
		assertThat(pattern.matchesWithinString("before 0.0 after"), is(true));

		assertThat(pattern.matchesWithinString("before 02 after"), is(false));
		assertThat(pattern.matchesWithinString("before -0234 after"), is(false));
		assertThat(pattern.matchesWithinString("before 004 after"), is(false));
		assertThat(pattern.matchesWithinString("before _4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4after"), is(false));

	}

	@Test
	public void testNumberIncludingScientificNotationElement() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex pattern
				= RegexBuilder.startingAnywhere()
						.numberIncludingScientificNotation().once();

		System.out.println("REGEX: " + pattern.getRegex());
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

		assertThat(pattern.matchesWithinString("before 2E10 after"), is(true));
		assertThat(pattern.matchesWithinString("before -2.89E-7.98 after"), is(true));
		assertThat(pattern.matchesWithinString("before 1.37e+15 after"), is(true));
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
		//
		//  ^((?i)interval(?-i)){1} {1}([-+]?\b[1-9]+\d*(\.{1}\d+)?(((?i)E(?-i)){1}[-+]?[1-9]+\d*(\.{1}\d+)?)?(?!\S)){1} {1}(\w+)$
		Regex pattern
				= RegexBuilder.startingFromTheBeginning()
						.literalCaseInsensitive("interval").once()
						.space().once()
						.numberIncludingScientificNotation().once()
						.space().once()
						.beginOrGroup().word().endOrGroup()
						.endOfInput();

		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND";
		assertThat(pattern.matchesEntireString(intervalString), is(true));

		final Optional<Match> firstMatch = pattern
				.getFirstMatchFrom(intervalString);

		if (!firstMatch.isPresent()) {
			Assert.fail("Match not found");
		} else {
			List<String> allGroups = firstMatch.get().allGroups();
			allGroups.stream().forEach(s -> System.out.println("GROUP: " + s));
			assertThat(allGroups.size(), is(13));

			final Double value = Double.valueOf(allGroups.get(3));
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
				= RegexBuilder.startingFromTheBeginning()
						.beginNamedCapture("interval").literalCaseInsensitive("interval").once().endNamedCapture()
						.space().once()
						.beginNamedCapture("value").numberIncludingScientificNotation().once().endNamedCapture()
						.space().once()
						.beginNamedCapture("unit").word().endNamedCapture()
						.endOfInput();

		System.out.println("REGEX: " + intervalRegex);
		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND";
		assertThat(intervalRegex.matchesEntireString(intervalString), is(true));

		final HashMap<String, String> allGroups = intervalRegex.getAllNamedCapturesOfFirstMatchWithinString(intervalString);
		assertThat(allGroups.size(), is(3));

		final Double value = Double.valueOf(allGroups.get("value"));
		assertThat(value, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(value * 1000000), is(-2L));
	}

	@Test
	public void testNumberLike() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ([-+]?\b[1-9]+\d*(\.{1}\d+)?){1}
		Regex pattern
				= RegexBuilder.startingAnywhere()
						.numberLike().once();

//		System.out.println("REGEX: " + pattern.getRegex());
		//-1 2 -234 +4 -4 4.5 FAIL 02 -0234 004 _4 A4
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
				= RegexBuilder.startingAnywhere()
						.numberLike().once();

//		System.out.println("REGEX: " + pattern.getRegex());
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
				= RegexBuilder.startingAnywhere()
						.number().once();

		System.out.println("REGEX: " + numberRegex.getRegex());
		matches = numberRegex.getAllMatches("-1 2 -234 +4 -4 4.5 0 0.0 FAIL 02 -0234 004 _4 A4");
		assertThat(matches.size(), is(8));
//		System.out.println("MATCHES_SIZE: " + matches.size());
//		assertThat(matches.size(), is(11));
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
				= RegexBuilder.startingAnywhere()
						.wordBoundary()
						.beginCaseInsensitiveSection()
						.literal("day").once()
						.literal("s").onceOrNotAtAll()
						.endCaseInsensitiveSection()
						.wordBoundary()
						.endOfTheString();

		System.out.println("REGEX: " + regex.getRegex());

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
				= RegexBuilder.startingAnywhere()
						.wordBoundary()
						.literalCaseInsensitive("day").once()
						.literalCaseInsensitive("s").onceOrNotAtAll()
						.wordBoundary()
						.endOfTheString();

		System.out.println("REGEX: " + regex.getRegex());

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
				= RegexBuilder.startingAnywhere()
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
		allMatches.stream().forEach(t -> System.err.println("MATCH: " + t.getEntireMatch()));
		assertThat(allMatches.size(), is(4));

		for (var match : allMatches) {
			System.out.println("MATCH: " + match.getEntireMatch());
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
				= RegexBuilder.startingAnywhere()
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
				= RegexBuilder.startingAnywhere()
						.wordBoundary()
						.beginCaseInsensitiveSection()
						.literal("day").once()
						.literal("s").onceOrNotAtAll()
						.endCaseInsensitiveSection()
						.wordBoundary();

		System.out.println("REGEX: " + regex.getRegex());

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
//		final Regex daysCapture = RegexBuilder.startingAnywhere()
//				.beginCaseInsensitiveSection().literal("INTERVAL ").endCaseInsensitiveSection().onceOrNotAtAll()
//				.beginNamedCapture("days").number().onceOrNotAtAll().endNamedCapture();
//		final Regex literalDaysBetweenDaysAndHours = RegexBuilder.startingAnywhere().beginGroup().space().once().word().onceOrNotAtAll().space().onceOrNotAtAll().endGroup().onceOrNotAtAll();
//		final Regex hoursCapture = RegexBuilder.startingAnywhere().beginNamedCapture("hours").number().once().endNamedCapture();
//		final Regex minutesCapture = RegexBuilder.startingAnywhere().beginNamedCapture("minutes").number().once().endNamedCapture();
//		final Regex secondsCapture = RegexBuilder.startingAnywhere().beginNamedCapture("seconds").numberIncludingScientificNotation().once().endNamedCapture();
//		final Regex nanosCapture = RegexBuilder.startingAnywhere().beginNamedCapture("nanos").number().onceOrNotAtAll().endNamedCapture();
		Regex regex
				//				= daysCapture
				//						.extend(literalDaysBetweenDaysAndHours)
				//						.extend(hoursCapture)
				//						.literal(":")
				//						.extend(minutesCapture)
				//						.literal(":")
				//						.extend(secondsCapture)
				//						.literal(":").onceOrNotAtAll()
				//						.extend(nanosCapture);
				= RegexBuilder.startingAnywhere()
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
						.beginNamedCapture("seconds").numberIncludingScientificNotation().once().endNamedCapture()
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

	}

	private void shouldMatchTests(final Regex regex, String testStr, String days, String hours, String minutes, String seconds, String nanos) {
//		assertThat(regex.matchesWithinString(testStr), is(true));
		if (regex.matchesWithinString(testStr)) {
			Optional<Match> optional = regex.getFirstMatchFrom(testStr);
			if (optional.isPresent()) {
				Match match = optional.get();
				System.out.println("TESTING: " + testStr);
				match.getAllNamedCaptures().forEach((k, v) -> System.out.println("" + k + " => " + v));
				assertThat(match.getNamedCapture("days"), is(days));
				assertThat(match.getNamedCapture("hours"), is(hours));
				assertThat(match.getNamedCapture("minutes"), is(minutes));
				assertThat(match.getNamedCapture("seconds"), is(seconds));
				assertThat(match.getNamedCapture("nanos"), is(nanos));
			} else {
				regex.testAgainst(testStr);
				Assert.fail("Match Failed");
			}
		} else {
			regex.testAgainst(testStr);
			Assert.fail("Match Failed");
		}
	}
}
