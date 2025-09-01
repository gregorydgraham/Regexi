/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

/**
 * Negative lookahead looks past the current match to check that it is NOT followed by the lookahead expression.
 *
 * <p>
 * This is similar to adding an excluding expression, @{code "[^~].} for instance, but doesn't include the excluded expression in the match. Used with named
 * captures, this is very useful to strip delimiters. 
 * </p>
 *
 * @author gregorygraham
 * @param <REGEX> the type returned by {@link #endLookahead() }
 */
public class NegativeLookahead<REGEX extends AbstractHasRegexFunctions<REGEX>> extends RegexGroup<NegativeLookahead<REGEX>, REGEX> {

  protected NegativeLookahead(REGEX original) {
    super(original);
  }

  @Override
  public String toRegexString() {
    final String regexp = getCurrent().toRegexString();
    return "(?!" + regexp + ")";
  }

  public REGEX endLookahead() {
    return super.endGroup();
  }

}
