package es.uam.eps.ir.diversity.probability;

/**
 *
 * @author saul
 */
public abstract class GenericItemProbability implements ItemProbability {

    public abstract double probability(Long item);

    @Override
    public double probability(Long item, Long user) {
        return probability(item);
    }
}
