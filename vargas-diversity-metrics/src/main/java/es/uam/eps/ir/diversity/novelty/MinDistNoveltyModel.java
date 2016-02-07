package es.uam.eps.ir.diversity.novelty;

import es.uam.eps.ir.Dataset;
import es.uam.eps.ir.diversity.distance.DistanceModel;
import java.util.ArrayList;

/**
 *
 * @author saul
 */
public class MinDistNoveltyModel extends ListIndependentNoveltyModel {

    private final DistanceModel distm;
    private final Dataset train;

    public MinDistNoveltyModel(DistanceModel distm, Dataset train) {
        this.distm = distm;
        this.train = train;
    }

    @Override
    public double novelty(Long item, Long user) {
        return distm.minDistance(item, new ArrayList<Long>(train.getItems(user)));
    }
}
