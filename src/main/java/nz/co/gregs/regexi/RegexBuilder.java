/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

/**
 *
 * @author gregorygraham
 */
public class RegexBuilder {

	/**
	 * Create a new empty regular expression.
	 *
	 * @return a new empty regular expression
	 */
	public static Regex startingAnywhere() {
		return new UnescapedSequence("");
	}

	/**
	 * Create a new regular expression that includes a test for the start of the
	 * string.
	 *
	 * @return a new regular expression
	 */
	public static Regex startingFromTheBeginning() {
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
	 * {@code Regex regex =  Regex.startOrGroup().literal("A").or().literal("B").endGroup();
	 * } produces "(A|B)".
	 *
	 * @return a new regular expression
	 */
	public static OrGroup<Regex> startOrGroup() {
		return startingAnywhere().beginOrGroup();
	}

	private RegexBuilder() {
	}
}
