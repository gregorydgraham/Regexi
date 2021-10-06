/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

/**
 *
 * @author gregorygraham
 * @deprecated this class adds little in the way of functionality over Regex
 */
@Deprecated
public class RegexBuilder {

	/**
	 * Create a new empty regular expression.
	 *
	 * @return a new empty regular expression
	 * @deprecated use {@link Regex#startingAnywhere() } instead
	 */
	@Deprecated
	public static PartialRegex startingAnywhere() {
		return new UnescapedSequence("");
	}

	/**
	 * Create a new regular expression that includes a test for the start of the
	 * string.
	 *
	 * @return a new regular expression
	 * @deprecated use {@link Regex#startingFromTheBeginning() } instead
	 */
	@Deprecated
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
	 * {@code PartialRegex regex =  PartialRegex.startOrGroup().literal("A").or().literal("B").endGroup();
 } produces "(A|B)".
	 *
	 * @return a new regular expression
	 * @deprecated use {@link Regex#startOrGroup() } instead
	 */
	@Deprecated

	public static OrGroup<PartialRegex> startOrGroup() {
		return startingAnywhere().beginOrGroup();
	}

	private RegexBuilder() {
	}
}
