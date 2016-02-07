package es.uam.eps.ir.diversity.probability;

import es.uam.eps.ir.Dataset;

/**
 *
 * @author saul
 */
public class ProfileUniformItemProbability implements ItemProbability {

    private final Dataset train;

    public ProfileUniformItemProbability(Dataset train) {
        this.train = train;
    }

    @Override
    public double probability(Long item, Long user) {
        if (!train.getItems(user).contains(item)) {
            return 0;
        } else {
            return 1 / (double) train.getItems(user).size();
        }
    }
}
