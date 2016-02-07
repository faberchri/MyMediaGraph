package ch.uzh.ifi.ddis.mymedialite.graph.rwbenchmark

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender
import com.tinkerpop.blueprints.Vertex

abstract class BenchmarkBase extends ItemRankGraphRecommender {

    def numWalks = 1000

    @Override
    protected List<Vertex> runRankQuery(Object userVertex) {
        def m = getRankMap(userVertex)
        m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
        m = m.sort { a, b ->
            b.value <=> a.value
        }
        return m.keySet() as List
    }

    def getRankMap(def userVertex){
        def m = [:].withDefault {0}
        def rand = new java.util.Random()

        def getDegree = {Vertex v ->
            return v.both(userItemFeedbackEdgeLabel).count()
        }.memoize()

        def getNeighbors = {Vertex v ->
            return v.both(userItemFeedbackEdgeLabel).toList()
        }.memoize()

        def getRandomNeighbor = {Vertex v ->
            def l = getNeighbors(v)
            return l[rand.nextInt(l.size())]
        }

        (0..<numWalks).each{
            walk(userVertex, m, getDegree, getRandomNeighbor)
        }

        m = rerank(m)

        return m
    }

    def rerank(def m){
        // nothing to do
        return m
    }

    abstract def walk(def userVertx, def m, def getDegree, def getRandomNeighbor)

    public String toString() {
        return super.toString() + " numWalks=$numWalks"
    }
}
