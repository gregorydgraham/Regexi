/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.api;

import nz.co.gregs.regexi.Regex;
import nz.co.gregs.regexi.RegexBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

/**
 *
 * @author gregorygraham
 */
public class RegexBuilderTest {
	

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
	
}
