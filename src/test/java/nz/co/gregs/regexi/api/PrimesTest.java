/*
 * @author gregorygraham
 */
package nz.co.gregs.regexi.api;

import nz.co.gregs.regexi.SieveOfEratosthenes;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.core.Is.is;
import org.junit.Test;

public class PrimesTest {

	@Test
	public void testSieve() {
		System.out.println("SIEVE: " + SieveOfEratosthenes.REGEX.toString());

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
