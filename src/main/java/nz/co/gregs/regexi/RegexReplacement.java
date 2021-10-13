/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.regexi;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 *
 * @author gregorygraham
 */
public class RegexReplacement {

	String pattern = "";

	private final Regex regex;

	RegexReplacement(Regex regex) {
		this.regex = regex;
	}

	private String escape(String unescaped) {
		String escaped = "";
		if (unescaped == null) {
			escaped = "";
		} else {
			escaped = unescaped
					.replaceAll("\\$", "\\$")
					.replaceAll("\\\\", "\\\\\\\\");
		}
		return escaped;
	}

	public RegexReplacement numberedReference(int literal) {
		pattern += "$" + literal;
		return this;
	}

	public RegexReplacement namedReference(String name) {
		pattern += "${" + name + "}";
		return this;
	}

	public RegexReplacement literal(String literal) {
		pattern += escape(literal);
		return this;
	}

	String getReplacementPattern() {
		System.out.println("REPLACEMENT: " + pattern);
		return pattern;
	}

	public Matcher getMatcher(String s) {
		return regex.getMatcher(s);
	}

	public String replaceAll(String s) {
		System.out.println("STRING: " + s);
		System.out.println("REGEX: " + regex.getRegex());
		System.out.println("RELACEMENT: " + getReplacementPattern());
		return getMatcher(s).replaceAll(getReplacementPattern());
	}

	public String replaceFirst(String s) {
		return getMatcher(s).replaceFirst(getReplacementPattern());
	}

	public String replaceAll(String s, Function<MatchResult, String> fn) {
		return getMatcher(s).replaceAll(fn);
	}

	public String replaceFirst(String s, Function<MatchResult, String> fn) {
		return getMatcher(s).replaceFirst(getReplacementPattern());
	}

}
