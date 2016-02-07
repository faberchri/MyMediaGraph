package es.uam.eps.ir.diversity.probability.relevance;

import es.uam.eps.ir.Dataset;

/**
 *
 * @author saul
 */
public class RatingsRelativeItemExpRelevance implements ItemRelevanceProbability {

    private final Dataset dataset;

    public RatingsRelativeItemExpRelevance(Dataset test) {
        this.dataset = test;
    }

    @Override
    public double probability(Long item, Long user) {
        if (!dataset.getItems(user).contains(item)) {
            return 0.0;
        } else {
            return Math.pow(2, dataset.getRating(user, item)) / Math.pow(2, dataset.getMaxRating());
        }
    }
}
