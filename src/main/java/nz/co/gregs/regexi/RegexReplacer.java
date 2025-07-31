/*
 * Copyright 2025 Gregory Graham.
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
package nz.co.gregs.regexi;

import java.io.Serializable;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 *
 * @author gregorygraham
 */
public class RegexReplacer implements Serializable {

  private final Regex regex;
  private final String pattern;
  
  public RegexReplacer(Regex regex, String pattern) {
    this.regex = regex;
    this.pattern = pattern;
  }

	public String replaceAll(String s) {
		return getMatcher(s).replaceAll(pattern);
	}

	public String replaceFirst(String s) {
		return getMatcher(s).replaceFirst(pattern);
	}

	public String replaceAll(String s, Function<MatchResult, String> fn) {
		return getMatcher(s).replaceAll(fn);
	}

	public String replaceFirst(String s, Function<MatchResult, String> fn) {
		return getMatcher(s).replaceFirst(fn);
	}

	private Matcher getMatcher(String s) {
		return regex.getMatcher(s);
	}
  
}
