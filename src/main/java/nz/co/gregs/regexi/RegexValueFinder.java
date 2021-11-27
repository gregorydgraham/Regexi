package nz.co.gregs.regexi;

import java.util.Optional;

/**
 *
 * @author gregorygraham
 */
public class RegexValueFinder {

	private final Regex regex;
	private final String namedCapture;

	RegexValueFinder(Regex baseRegex, String previouslyDefinedNamedCapture) {
		this.regex = baseRegex;
		this.namedCapture = previouslyDefinedNamedCapture;
	}

	public Optional<String> getValueFrom(String sourceText) {
		
		final Optional<Match> firstMatchFrom = regex.getFirstMatchFrom(sourceText);
		if (firstMatchFrom.isPresent()) {
			final String yearSection = firstMatchFrom.get().getNamedCapture(namedCapture);
			return Optional.of(yearSection);
		} else {
			return Optional.empty();
		}
	}

}
