
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

import nz.co.gregs.regexi.Regex;
import org.junit.Test;

/**
 *
 * @author gregorygraham
 */
public class ISO31Tests {

	@Test
	public void testIntegerISO_31() {

		RegexTests tests = new RegexTests();

		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "something", false);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "1,234,567", false);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "1.234.567", false);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), " +1", false);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "1.0", false);

		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "1", true);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "+1", true);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "-1", true);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "1 234 567", true);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "-1 234 567", true);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "+1 234 567", true);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "+1 012345", true);
		tests.add(Regex.startingFromTheBeginning().integerISO_31().toRegex(), "1 012345", true);

		tests.performTests();
	}

	@Test
	public void testNumberISO_31() {

		RegexTests tests = new RegexTests();

		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "something", false);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "1,234,567", false);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "1.234.567", false);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "1,234,567.0", false);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "1.234.567,1", false);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), " +1", false);

		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "1", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "+1", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "-1", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "1.0", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "+1.0", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "-1.123", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "1 234 567", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "-1 234 567.012", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "+1 234 567.3", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "+1 012345", true);
		tests.add(Regex.startingFromTheBeginning().numberISO_31().toRegex(), "1 012345.4", true);

		tests.performTests();
	}
}
