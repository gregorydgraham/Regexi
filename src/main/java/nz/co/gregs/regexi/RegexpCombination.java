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
class RegexpCombination extends Regex {
	
	private final HasRegexFunctions<?> first;
	private final HasRegexFunctions<?> second;

	protected RegexpCombination(HasRegexFunctions<?> first, HasRegexFunctions<?> second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String getRegex() {
		return first.getRegex() + second.getRegex();
	}
	
}
//	@Deprecated
//	public static class Group extends Regex {
//
//		private final Regex regexp;
//
//		public Group(Regex regexp) {
//			this.regexp = regexp;
//		}
//
//		@Override
//		public String getRegex() {
//			return "(" + regexp.getRegex() + ")";
//		}
//	}
