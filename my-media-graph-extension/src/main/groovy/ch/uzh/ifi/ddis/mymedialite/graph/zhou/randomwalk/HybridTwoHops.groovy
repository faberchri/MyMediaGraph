package ch.uzh.ifi.ddis.mymedialite.graph.zhou.randomwalk

import ch.uzh.ifi.ddis.mymedialite.graph.GraphRecommender
import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender

import com.tinkerpop.blueprints.Vertex


class HybridTwoHops extends ItemRankGraphRecommender{

    def lambda = 0.0

    def MAX_WALKS = 200000

    @Override
    protected List<Vertex> runRankQuery(Object userVertex) {
        def fTilde = getFTilde(userVertex)
        fTilde.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
        fTilde = fTilde.sort { a, b ->
            b.value <=> a.value
        }
        return fTilde.keySet() as List
    }

    def getFTilde(def userVertex){
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

        (0..<MAX_WALKS).each{
            walk(userVertex, m, getDegree, getRandomNeighbor)
        }

        def s = m.values().sum()
        m.collectEntries(m){ k, v ->
            [(k): (v / s)]
        }

        return m
    }

    def walk(def userVertex, def m, def getDegree, def getRandomNeighbor){
        def likedItem = getRandomNeighbor(userVertex)
        def d = getDegree(likedItem)
        def q = d**(lambda)
        def p = d
        def v = getRandomNeighbor(likedItem) // get a user vertex
        d = getDegree(v)
        p *= d
        q = q * d
        v = getRandomNeighbor(v)
        d = getDegree(v)
        q = q * d**(1-lambda)
        m[v] = m[v] + p / q
    }

    def setup(def g){
        // nothing to do
    }

}
