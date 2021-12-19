/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.co.gregs.regexi.internal;

/**
 *
 * @author gregorygraham
 * @param <REGEX> the PartialRegex to return
 */
public abstract class AbstractHasRegexFunctions<REGEX extends AbstractHasRegexFunctions<REGEX>> implements HasRegexFunctions<REGEX> {

	protected abstract void registerNamedGroup(String name);
}
