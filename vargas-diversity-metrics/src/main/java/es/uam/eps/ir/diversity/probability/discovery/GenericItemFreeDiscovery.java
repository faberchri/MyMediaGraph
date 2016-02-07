package es.uam.eps.ir.diversity.probability.discovery;

import es.uam.eps.ir.Dataset;
import es.uam.eps.ir.diversity.probability.GenericItemProbability;

/**
 *
 * @author saul
 */
public class GenericItemFreeDiscovery extends GenericItemProbability {
    private GenericItemDiscovery gip;
    private double norm;

    public GenericItemFreeDiscovery(Dataset train) {
        this(train, new GenericItemDiscovery(train));
    }

    public GenericItemFreeDiscovery(Dataset train, GenericItemDiscovery gip) {
        this.gip = gip;

        this.norm = 0.0;
        for (Long j : train.getItems()) {
            norm += gip.probability(j);
        }
    }

    @Override
    public double probability(Long item) {
        if (norm == 0.0) {
            return 0;
        } else {
            return gip.probability(item) / norm;
        }
    }
}
