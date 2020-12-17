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
import java.util.stream.Collectors;
import nz.co.gregs.regexi.Match;
import nz.co.gregs.regexi.Regex;
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
						Regex.startingAnywhere().dot().digits()
				).onceOrNotAtAll();

		final Regex separator
				= Regex.startingAnywhere().beginRange('0', '9').includeMinus().negated().closeRange().atLeastOnce();

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
						.closeRange()
						.atLeastOnce()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce()
						.literal(':').once()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce()
						.literal(':').once()
						.literal('-').onceOrNotAtAll()
						.anyCharacterBetween('0', '9').atLeastOnce().add(Regex.startingAnywhere().dot().digits()
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
		regex = Regex.startingAnywhere().anyOf("Amy","Bob","Charlie");

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

//		System.out.println("REGEX: " + pattern.getRegex());
		assertThat(pattern.matchesWithinString("before -1 after"), is(true));
		assertThat(pattern.matchesWithinString("before 2 after"), is(true));
		assertThat(pattern.matchesWithinString("before -234 after"), is(true));
		assertThat(pattern.matchesWithinString("before +4 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4 after"), is(true));
		assertThat(pattern.matchesWithinString("before 4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.05 after"), is(true));
		assertThat(pattern.matchesWithinString("before 02 after"), is(false));
		assertThat(pattern.matchesWithinString("before -0234 after"), is(false));
		assertThat(pattern.matchesWithinString("before 004 after"), is(false));
		assertThat(pattern.matchesWithinString("before _4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4after"), is(false));
		assertThat(pattern.matchesWithinString("before 2*E10"), is(false));

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

//		System.out.println("REGEX: " + pattern.getRegex());
		assertThat(pattern.matchesWithinString("before -1 after"), is(true));
		assertThat(pattern.matchesWithinString("before 2 after"), is(true));
		assertThat(pattern.matchesWithinString("before -234 after"), is(true));
		assertThat(pattern.matchesWithinString("before +4 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4 after"), is(true));
		assertThat(pattern.matchesWithinString("before 4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.5 after"), is(true));
		assertThat(pattern.matchesWithinString("before -4.05 after"), is(true));
		assertThat(pattern.matchesWithinString("before 02 after"), is(false));
		assertThat(pattern.matchesWithinString("before -0234 after"), is(false));
		assertThat(pattern.matchesWithinString("before 004 after"), is(false));
		assertThat(pattern.matchesWithinString("before _4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4 after"), is(false));
		assertThat(pattern.matchesWithinString("before A4after"), is(false));
		assertThat(pattern.matchesWithinString("before 2*E10"), is(false));
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
				= Regex.startingFromTheBeginning()
						.literalCaseInsensitive("interval").once()
						.space().once()
						.numberIncludingScientificNotation().once()
						.space().once()
						.beginOrGroup().word().endOrGroup()
						.endOfInput();

		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND";
		assertThat(pattern.matchesEntireString(intervalString), is(true));
		final List<String> allMatches = pattern.getAllGroups(intervalString);
		assertThat(allMatches.size(), is(6));
		
		final Double value = Double.valueOf(allMatches.get(2));
		assertThat(value, is(Double.valueOf("-1.999999999946489E-6")));
		assertThat(Math.round(value * 1000000), is(-2L));

	}

	@Test
	public void testNamedCaptures() {
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ^(?<interval>((?i)interval(?-i)){1}) {1}(?<value>([-+]?\b[1-9]+\d*(\.{1}\d+)?(((?i)E(?-i)){1}[-+]?[1-9]+\d*(\.{1}\d+)?)?(?!\S)){1}) {1}(?<unit>\w+)$
		Regex pattern
				= Regex.startingFromTheBeginning()
						.beginNamedCapture("interval").literalCaseInsensitive("interval").once().endCapture()
						.space().once()
						.beginNamedCapture("value").numberIncludingScientificNotation().once().endCapture()
						.space().once()
						.beginNamedCapture("unit").word().endCapture()
						.endOfInput();

		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND";
		assertThat(pattern.matchesEntireString(intervalString), is(true));

		final HashMap<String, String> allGroups = pattern.getAllNamedCapturesOfFirstMatchWithinString(intervalString);
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
				= Regex.startingAnywhere()
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
		Regex regex
				= Regex.startingAnywhere()
						.numberLike().once();

//		System.out.println("REGEX: " + pattern.getRegex());
		List<Match> matches = regex.getAllMatches("-1 2 -234 +4 -4 4.5 FAIL 02 -0234 004 _4 A4");
		assertThat(matches.size(), is(11));
		for (Match match : matches) {
			assertThat(match.getEntireMatch(), isOneOf("-1", "2", "-234", "+4", "-4", "4.5", "02", "-0234", "004", "4", "4"));
		}
		List<String> collected = matches.stream().map(v->v.getEntireMatch()).collect(Collectors.toList());
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

		regex
				= Regex.startingAnywhere()
						.number().once();

//		System.out.println("REGEX: " + pattern.getRegex());
		matches = regex.getAllMatches("-1 2 -234 +4 -4 4.5 FAIL 02 -0234 004 _4 A4");
		assertThat(matches.size(), is(6));
		for (Match match : matches) {
			assertThat(match.getEntireMatch(), isOneOf("-1", "2", "-234", "+4", "-4", "4.5"));
		}
		collected = matches.stream().map(v->v.getEntireMatch()).collect(Collectors.toList());
		assertThat(collected.get(0), is("-1"));
		assertThat(collected.get(1), is("2"));
		assertThat(collected.get(2), is("-234"));
		assertThat(collected.get(3), is("+4"));
		assertThat(collected.get(4), is("-4"));
		assertThat(collected.get(5), is("4.5"));
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
						.caseInsensitiveGroup()
						.literal("day").once()
						.literal("s").onceOrNotAtAll()
						.caseInsensitiveEnd()
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
				= Regex.startingAnywhere()
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
		System.out.println("nz.co.gregs.regexi.RegexTest.testLotsOfMatchesAndNamedGroups()");
		// -2 days 00:00:00
		// 1 days 00:00:5.5
		// 0 days 00:00:-5.5
		//
		// ^(?<interval>((?i)interval(?-i)){1}) {1}(?<value>([-+]?\b[1-9]+\d*(\.{1}\d+)?(((?i)E(?-i)){1}[-+]?[1-9]+\d*(\.{1}\d+)?)?(?!\S)){1}) {1}(?<unit>\w+)$
		Regex regex
				= Regex.startingAnywhere()
						.beginNamedCapture("interval").literalCaseInsensitive("interval").once().endCapture()
						.space().once()
						.beginNamedCapture("value").numberIncludingScientificNotation().once().endCapture()
						.space().once()
						.beginNamedCapture("unit")
						.caseInsensitiveGroup()
						.anyOf("DAY","HOUR","MINUTE","SECOND").once().literal("S").onceOrNotAtAll()
						.caseInsensitiveEnd()
						.endCapture();

		System.out.println("REGEX: " + regex.getRegex());
		String intervalString = "INTERVAL -1.999999999946489E-6 SECOND, INTERVAL 4 YEARS, INTERVAL 2 DAY, interval -34 hour, interval 56 minutes";
		assertThat(regex.matchesWithinString(intervalString), is(true));

		final List<Match> allMatches = regex.getAllMatches(intervalString);
		allMatches.stream().forEach(t -> System.err.println("MATCH: "+t.getEntireMatch()));
		assertThat(allMatches.size(), is(4));
		
		for (var match : allMatches) {
			System.out.println("MATCH: " + match.getEntireMatch());
			final HashMap<String, String> allNamedCaptures = match.getAllNamedCaptures();
			assertThat(allNamedCaptures.size(), is(3));
			final Double value = Double.valueOf(allNamedCaptures.get("value"));
			assertThat(Math.abs(Math.round(value * 1000000)), isOneOf(2L, 2000000L, 34000000L,56000000L));
			assertThat(match.getNamedCapture("unit").toLowerCase(), isOneOf("second", "day", "hour","minutes"));
		}
	}
}
