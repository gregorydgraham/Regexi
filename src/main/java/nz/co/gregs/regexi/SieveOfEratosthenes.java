/*
 * @author gregorygraham
 */
package nz.co.gregs.regexi;

public class SieveOfEratosthenes {

	/**
	 * Implementing Sieve of Eratosthenes in Regex: ".?|(..+?)\\1+"
	 *
	 * SIEVE: ^.?$|^(..+?)\1+$
	 *
	 * @author gregorygraham
	 */
	public static final Regex REGEX
			= Regex.empty()
					.startOfInput()
					.anyCharacter().onceOrNotAtAllGreedy()
					.endOfInput()
					.or()
					.startOfInput()
					.beginGroup()
					.anyCharacter().anyCharacter().oneOrMoreReluctant()
					.endGroup()
					.backReference(1).oneOrMoreGreedy()
					.endOfInput()
					.toRegex();

	private SieveOfEratosthenes() {
	}

	public static boolean isPrime(int number) {
		return !REGEX.matches(new String(new char[number]));
	}

	public static boolean isNotPrime(int number) {
		return REGEX.matches(new String(new char[number]));
	}

}
