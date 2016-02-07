package es.uam.eps.ir.diversity.metrics;

import es.uam.eps.ir.discount.DiscountModel;
import es.uam.eps.ir.diversity.novelty.NoveltyModel;
import es.uam.eps.ir.diversity.probability.relevance.ItemRelevanceProbability;
import java.util.List;

/**
 *
 * @author
 * saul
 */
public class NoveltyMetric {

    private final NoveltyModel nm;
    private final DiscountModel discm;
    private final ItemRelevanceProbability rel;
    private final int cut;

    public NoveltyMetric(NoveltyModel nm, DiscountModel dm, ItemRelevanceProbability rel, int cut) {
        this.nm = nm;
        this.discm = dm;
        this.rel = rel;
        this.cut = cut;
    }

    public double compute(Long u, List<Long> list) {
        double novelty = 0;
        int R = Math.min(cut, list.size());
        for (int k = 0; k < R; k++) {
            Long i = list.get(k);
            novelty += discm.discount(k) * rel.probability(i, u) * nm.novelty(i, u, list);
        }

        double norm = normalization(list);
        if (norm == 0) {
            return 0;
        }
        return novelty / norm;
    }

    private double normalization(List<Long> list) {
        double norm = 0;
        int R = Math.min(cut, list.size());
        for (int l = 0; l < (R - 1); l++) {
            norm += (l + 1) * (discm.discount(l) - discm.discount(l + 1));
        }
        norm += R * discm.discount(R - 1);
        return norm;
    }
}
