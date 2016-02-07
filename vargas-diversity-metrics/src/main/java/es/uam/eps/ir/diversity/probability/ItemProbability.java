/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.diversity.probability;

/**
 *
 * @author saul
 */
public interface ItemProbability {
    public double probability(Long item, Long user);
}
