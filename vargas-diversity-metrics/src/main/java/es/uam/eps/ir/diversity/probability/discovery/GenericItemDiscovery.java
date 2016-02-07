package es.uam.eps.ir.diversity.probability.discovery;

import es.uam.eps.ir.Dataset;
import es.uam.eps.ir.diversity.probability.GenericItemProbability;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author saul
 */
public class GenericItemDiscovery extends GenericItemProbability {

    private final Map<Long, Double> map;

    public GenericItemDiscovery(Dataset train) {
        map = new HashMap<Long, Double>();
        double nusers = (double) train.getUsers().size();
        for (Long i : train.getItems()) {
            Set<Long> s = train.getUsers(i);
            if (s != null) {
                map.put(i, s.size() / nusers);
            } else {
                map.put(i, 0.0);
            }
        }
    }

    @Override
    public double probability(Long item) {
        Double p = map.get(item);
        
        if (p == null) {
            return 0.0;
        } else {
            return map.get(item);
        }
    }
}
