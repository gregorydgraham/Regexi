/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.co.gregs.regexi;

public class SieveOfEratosthenes {
	
	public static final Regex REGEX =
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

	private SieveOfEratosthenes() {
	}
	
	public static boolean isPrime(int number){
		return !REGEX.matches(new String(new char[number]));
	}
	
	public static boolean isNotPrime(int number){
		return REGEX.matches(new String(new char[number]));
	}
	
}
