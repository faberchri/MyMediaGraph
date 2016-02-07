package es.uam.eps.ir.diversity.novelty;

import es.uam.eps.ir.discount.DiscountModel;
import es.uam.eps.ir.diversity.distance.DistanceModel;
import es.uam.eps.ir.diversity.probability.relevance.ItemRelevanceProbability;
import java.util.List;

/**
 *
 * @author saul
 */
public class ListDistanceNovelty implements NoveltyModel {

    private final DistanceModel distm;
    private final DiscountModel discm;
    private final ItemRelevanceProbability rel;
    private final int cut;

    public ListDistanceNovelty(DistanceModel distm, DiscountModel discm, ItemRelevanceProbability rel, int cut) {
        this.distm = distm;
        this.discm = discm;
        this.rel = rel;
        this.cut = cut;
    }

    @Override
    public double novelty(Long item, Long user, List<Long> rankContext) {
        double setDist = 0;
        List<Long> list = rankContext;

        int k = rankContext.indexOf(item);

        int R = Math.min(cut, list.size());
        for (int l = 0; l < R; l++) {
            Long il = list.get(l);
            setDist += discm.discount(Math.max(0, l - k)) * rel.probability(item, user) * distm.distance(item, il);
        }

        double norm = setDistNormalization(user, k, list);
        if (norm != 0) {
            setDist /= norm;
        }

        return setDist;
    }

    private double setDistNormalization(Long user, int k, List<Long> list) {
        double norm = 0;
        int R = Math.min(cut, list.size());
        for (int l = 0; l < R; l++) {
            Long il = list.get(l);
            norm += discm.discount(Math.max(0, l - k)) * rel.probability(il, user);
        }
        return norm;
    }
}
