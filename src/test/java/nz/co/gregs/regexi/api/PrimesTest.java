/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.co.gregs.regexi.api;

import nz.co.gregs.regexi.Regex;
import nz.co.gregs.regexi.SieveOfEratosthenes;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.core.Is.is;
import org.junit.Test;

/**
 * Implementing Sieve of Eratosthenes in Regex: ".?|(..+?)\\1+"
 *
 * SIEVE: (^.?$|^(..+?)\1+$)
 *
 * @author gregorygraham
 */
public class PrimesTest {

	Regex sieve = 
			Regex.empty()
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

	@Test
	public void testSieve() {
		System.out.println("SIEVE: " + sieve.toString());

		
		System.out.println();
		System.out.println();
		System.out.println();
		// just the test
		System.out.println(sieve);
		for (int j = 0; j < 21; j++) {
			final String string = new String(new char[j]).replaceAll(".", "_");
			if (sieve.matches(string)) {
				// it's not prime
				System.out.println("  " + j + " is not prime");
			} else {
				System.out.println("  " + j + " PRIME");
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();
		// just the test
		System.out.println(SieveOfEratosthenes.REGEX);
		for (int j = 0; j < 21; j++) {
			if (SieveOfEratosthenes.isPrime(j)) {
				// it's not prime
				System.out.println("  " + j + " is not prime");
			} else {
				System.out.println("  " + j + " PRIME");
			}
		}
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(0),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(1),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(2),is(true));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(3),is(true));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(4),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(5),is(true));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(6),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(7),is(true));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(8),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(9),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(10),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(11),is(true));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(12),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(13),is(true));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(14),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(15),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(16),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(17),is(true));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(18),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(19),is(true));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(20),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(21),is(false));
		MatcherAssert.assertThat(SieveOfEratosthenes.isPrime(22),is(false));
	}
}
