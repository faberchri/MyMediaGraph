package es.uam.eps.ir.diversity.novelty;

import es.uam.eps.ir.Dataset;
import es.uam.eps.ir.diversity.distance.DistanceModel;
import es.uam.eps.ir.diversity.probability.ItemProbability;

/**
 *
 * @author saul
 */
public class ExpectedNoveltyModel extends ListIndependentNoveltyModel {

    private final ItemProbability ip;
    private final DistanceModel distm;
    private final Dataset train;

    public ExpectedNoveltyModel(DistanceModel distm, ItemProbability ip, Dataset train) {
        this.ip = ip;
        this.distm = distm;
        this.train = train;
    }

    @Override
    public String toString() {
        return "pn" + distm;
    }

    @Override
    public double novelty(Long item, Long user) {
        double novelty = 0.0;
        double norm = 0.0;
        for (long j : train.getItems(user)) {
            novelty += ip.probability(j, user) * distm.distance(item, j);
            norm += ip.probability(j, user);
        }

        if (norm == 0.0)
            return 1.0;

        return novelty / norm;
    }

}
