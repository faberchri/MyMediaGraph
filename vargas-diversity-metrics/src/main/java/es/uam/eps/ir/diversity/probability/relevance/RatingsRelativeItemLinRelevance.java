package es.uam.eps.ir.diversity.probability.relevance;

import es.uam.eps.ir.Dataset;

/**
 *
 * @author saul
 */
public class RatingsRelativeItemLinRelevance implements ItemRelevanceProbability {

    private final Dataset dataset;

    public RatingsRelativeItemLinRelevance(Dataset test) {
        this.dataset = test;
    }

    @Override
    public double probability(Long item, Long user) {
        if (!dataset.getItems(user).contains(item)) {
            return 0.0;
        } else {
            return dataset.getRating(user, item) / dataset.getMaxRating();
        }
    }
}
