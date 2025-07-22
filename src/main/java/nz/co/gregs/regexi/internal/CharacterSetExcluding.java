/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the type returned by {@link #endSet() }
 */
public class CharacterSetExcluding<REGEX extends AbstractHasRegexFunctions<REGEX>> extends CharacterSet<REGEX> {

  public CharacterSetExcluding(REGEX regex) {
    super(regex, true);
  }

  public final CharacterSetExcluding<REGEX> excludeRange(Character lowest, Character highest) {
    return (CharacterSetExcluding<REGEX>) extendWithRange(lowest, highest);
  }

  public final CharacterSetExcluding<REGEX> excludeLiterals(String literals) {
    return (CharacterSetExcluding<REGEX>) extendWithLiterals(literals);
  }

  public final CharacterSetExcluding<REGEX> excludeLiteral(Character literal) {
    return (CharacterSetExcluding<REGEX>) extendWithLiteral(literal);
  }

  public CharacterSetExcluding<REGEX> excludeMinus() {
    return (CharacterSetExcluding<REGEX>) extendWithHyphen();
  }

  /** excludes backslash characters.
   *
   * @return the extended regexi 
   * @deprecated use {@link #excludeBackslash() }
   */
  @Deprecated
  public CharacterSetExcluding<REGEX> excludeBackslah() {
    return (CharacterSetExcluding<REGEX>) extendWithBackslash();
  }

  public CharacterSetExcluding<REGEX> excludeBackslash() {
    return (CharacterSetExcluding<REGEX>) extendWithBackslash();
  }

  public CharacterSetExcluding<REGEX> excludeCaret() {
    return (CharacterSetExcluding<REGEX>) extendWithCaret();
  }

  public CharacterSetExcluding<REGEX> excludeCloseBracket() {
    return (CharacterSetExcluding<REGEX>) extendWithCloseBracket();
  }

  public CharacterSetExcluding<REGEX> excludeDigits() {
    return excludeRange('0', '9');
  }

  public CharacterSetExcluding<REGEX> excludeUppercase() {
    return excludeRange('A', 'Z');
  }

  public CharacterSetExcluding<REGEX> excludeWhitespace() {
    return (CharacterSetExcluding<REGEX>) extendWithCharacterClass('s');
  }

  public CharacterSetExcluding<REGEX> excludeWordcharacters() {
    return (CharacterSetExcluding<REGEX>) extendWithCharacterClass('w');
  }

  public CharacterSetExcluding<REGEX> excludeNonwhitespace() {
    return (CharacterSetExcluding<REGEX>) extendWithCharacterClass('S');
  }

  public CharacterSetExcluding<REGEX> excludeNonwordcharacters() {
    return (CharacterSetExcluding<REGEX>) extendWithCharacterClass('W');
  }

  public CharacterSetExcluding<REGEX> excludeLowercase() {
    return excludeRange('a', 'z');
  }

  public CharacterSetExcluding<REGEX> excludeLetters() {
    return excludeUppercase().excludeLowercase();
  }

  public CharacterSetExcluding<REGEX> excludeDot() {
    return excludeLiterals(".");
  }
}
