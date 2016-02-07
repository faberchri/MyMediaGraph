package es.uam.eps.ir.diversity.novelty;

import java.util.List;

/**
 *
 * @author saul
 */
public abstract class ListIndependentNoveltyModel implements NoveltyModel {

    @Override
    public double novelty(Long item, Long user, List<Long> rankContext) {
        return novelty(item, user);
    }
    
    public abstract double novelty(Long item, Long user);
    
}
