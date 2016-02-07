package es.uam.eps.ir.diversity.probability.choice;

import es.uam.eps.ir.Dataset;
import es.uam.eps.ir.diversity.probability.GenericItemProbability;

/**
 *
 * @author saul
 */
public class RatingsGenericItemChoice extends GenericItemProbability {

    private final Dataset test;

    public RatingsGenericItemChoice(Dataset test) {
        this.test = test;
    }

    @Override
    public double probability(Long item) {
        if (!test.getItems().contains(item))
            return 0.0;
        
        return test.getUsers(item).size() / (double) test.getNumRatings();
    }
}
