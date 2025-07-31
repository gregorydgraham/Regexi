/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi.internal;

import java.io.Serializable;
import java.util.regex.Matcher;
import nz.co.gregs.regexi.Regex;
import nz.co.gregs.regexi.RegexReplacer;

/**
 * A replacement version of a Regex.
 * 
 * Use {@link #getReplacer() } to get the RegexReplacer that makes the replacement.
 *
 * @author gregorygraham
 */
public class PartialRegexReplacement implements Serializable {
  
  String pattern = "";
  Regex regex;

	public PartialRegexReplacement(Regex regex) {
		this.regex = regex;
	}

	private String escape(String unescaped) {
		String escaped;
		if (unescaped == null) {
			escaped = "";
		} else {
			escaped = unescaped
					.replaceAll("\\$", "\\$")
					.replaceAll("\\\\", "\\\\\\\\");
		}
		return escaped;
	}

	public PartialRegexReplacement numberedReference(int literal) {
		pattern += "$" + literal;
		return this;
	}

	public PartialRegexReplacement namedReference(String name) {
		pattern += "${" + name + "}";
		return this;
	}

	public PartialRegexReplacement literal(String literal) {
		pattern += escape(literal);
		return this;
	}

	public String getReplacementPattern() {
		return pattern;
	}

	public Matcher getMatcher(String s) {
		return regex.getMatcher(s);
	}

	public RegexReplacer nothing() {
		return new RegexReplacer(regex, "");
	}

	public PartialRegexReplacement backslash() {
		return this.literal("\\");
	}
  
  public RegexReplacer getReplacer(){
    return new RegexReplacer(regex, pattern);
  }
}
