package nz.co.gregs.regexi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	/**
	 * Looks within the source text using the regex it was created from and
	 * returns the value of the named capture specified.
	 *
	 * <p>
	 * This method only returns the first value from the first match within the
	 * source text. Use {@link #getAllValuesFrom(java.lang.String) } to get all
	 * the appropriate values from the string.</p>
	 *
	 * @param sourceText the source text to search within.
	 * @return the value of the named capture within the source text or an empty
	 * optional.
	 */
	public Optional<String> getValueFrom(String sourceText) {

		final Optional<Match> firstMatchFrom = regex.getFirstMatchFrom(sourceText);
		if (firstMatchFrom.isPresent()) {
			final String yearSection = firstMatchFrom.get().getNamedCapture(namedCapture);
			return Optional.of(yearSection);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Looks within the source text using the regex it was created from and
	 * returns the value(s) of the named capture specified.
	 *
	 * @param sourceText the source text to search within.
	 * @return the values of the named capture within the source text.
	 */
	public List<String> getAllValuesFrom(String sourceText) {
		List<String> results = new ArrayList<>(0);
		final List<Match> matches = regex.getAllMatches(sourceText);
		if (matches.size() > 0) {
			List<String> collect = matches.stream()
					.map(match -> match.getNamedCapture(namedCapture))
					.filter(v -> v != null)
					.collect(Collectors.toList());
			results.addAll(collect);
		}
		return results;
	}

}
