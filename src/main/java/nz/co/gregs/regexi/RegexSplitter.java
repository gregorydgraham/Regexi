package nz.co.gregs.regexi;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gregorygraham
 */
public class RegexSplitter {

	private final Regex regex;

	RegexSplitter(Regex baseRegex) {
		this.regex = baseRegex;
	}

	public String[] split(String sourceText) {
		return sourceText.split(regex.getRegex());
	}

	public List<String> splitToList(String sourceText) {
		String[] split = sourceText.split(regex.getRegex());
		List<String> asList = Arrays.asList(split);
		return asList;
	}

}
