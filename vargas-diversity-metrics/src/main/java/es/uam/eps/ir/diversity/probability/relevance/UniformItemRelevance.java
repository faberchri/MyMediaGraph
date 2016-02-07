package es.uam.eps.ir.diversity.probability.relevance;

/**
 *
 * @author saul
 */
public class UniformItemRelevance implements ItemRelevanceProbability {

    @Override
    public double probability(Long item, Long user) {
        return 1.0;
    }
}
