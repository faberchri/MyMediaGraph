package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper
import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter.RandomWalkCounter

class P3Alpha extends CachedP{

    public P3Alpha() {
        super(BaseCooper.P3_DEFAULT_POWER, BaseCooper.EFFECTIVE_DEFAULT_ALPHA)
    }

    def performRandomWalk(def randomWalkCounter, def userVertex, def rand){

        def v = hop(userVertex, rand)
        def q = 1
        (1..<getPower()).each{
            q *= getDegree(v)
            v = hop(v, rand)
        }
        q = q**(1 - getAlpha())

        randomWalkCounter.updateCount(v, q)
    }

    def normalizeRandomWalkCounts(RandomWalkCounter randomWalkCounter){
        def norm = randomWalkCounter.effectiveWalksCount + randomWalkCounter.nullWalksCount
        def m = randomWalkCounter.getCopyOfVertxCountsInOrderedMap()
        def s = m.values().sum()
        m.collectEntries(m){ k, v ->
            [(k): (v / s)]
        }
        return m
    }
}
