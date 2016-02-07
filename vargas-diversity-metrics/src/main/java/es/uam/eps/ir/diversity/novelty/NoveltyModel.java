package es.uam.eps.ir.diversity.novelty;

import java.util.List;

/**
 *
 * @author saul
 */
public interface NoveltyModel {

    public double novelty(Long item, Long user, List<Long> rankContext);
}
