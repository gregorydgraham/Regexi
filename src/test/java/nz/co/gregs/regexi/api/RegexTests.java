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
package nz.co.gregs.regexi.api;

import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.regexi.Regex;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

/**
 *
 * @author gregorygraham
 */
public class RegexTests {
	
	List<String> tests = new ArrayList<>();
	List<Regex> regexes = new ArrayList<>();
	List<Boolean> expected = new ArrayList<>();

	public RegexTests() {
	}

	public RegexTests add(Regex regex, String target, boolean expectedResult) {
		tests.add(target);
		regexes.add(regex);
		expected.add(expectedResult);
		return this;
	}

	public void performTests() {
		String[] testArray = tests.toArray(new String[]{});
		Regex[] regexArray = regexes.toArray(new Regex[]{});
		Boolean[] expectedArray = expected.toArray(new Boolean[]{});
		for (int i = 0; i < regexArray.length; i++) {
			boolean test = regexArray[i].matches(testArray[i]);
			if (test != expectedArray[i]) {
				System.out.println("FAILED: [" + i + "] expected=" + expectedArray[i] + " actually=" + test + " USING: ~" + testArray[i] + "~ AGAINST: " + regexArray[i].getRegex());
				List<String> testAgainst = regexArray[i].testAgainst(testArray[i]);
				for (String string : testAgainst) {
					System.out.println(string);
				}
			}
			MatcherAssert.assertThat(test, Matchers.is(expectedArray[i]));
		}
	}
	
}
