package es.uam.eps.ir.diversity.novelty;

import es.uam.eps.ir.diversity.probability.ItemProbability;

/**
 *
 * @author saul
 */
public class LogNoveltyModel extends ListIndependentNoveltyModel {

    private final ItemProbability ip;

    public LogNoveltyModel(ItemProbability ip) {
        this.ip = ip;
    }

    @Override
    public double novelty(Long item, Long user) {
        double p = ip.probability(item, user);
        if (p == 0.0)
            return Double.NaN;
        return Math.log(1 / ip.probability(item, user)) / Math.log(2);
    }
}
