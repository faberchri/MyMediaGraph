package es.uam.eps.ir.diversity.novelty;

import es.uam.eps.ir.diversity.probability.ItemProbability;

/**
 *
 * @author saul
 */
public class ComplementNoveltyModel extends ListIndependentNoveltyModel {

    private final ItemProbability ip;

    public ComplementNoveltyModel(ItemProbability ip) {
        this.ip = ip;
    }

    @Override
    public double novelty(Long item, Long user) {
        return 1 - ip.probability(item, user);
    }
}
