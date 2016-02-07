package ch.uzh.ifi.ddis.mymedialite.graph.zhou.randomwalk

import ch.uzh.ifi.ddis.mymedialite.graph.GraphRecommender
import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender

import com.tinkerpop.blueprints.Vertex


class Hybrid extends ItemRankGraphRecommender{

    def lambda = 0.0

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
        def m = [:].withDefault {[0,0]}
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

        (0..50000).each{
            walk(userVertex, m, getDegree, getRandomNeighbor)
        }

        def heatSum = m.values().inject(0){sum, val -> sum + val[0]}
        def probSum = m.values().inject(0){sum, val -> sum + val[1]}

        m = m.collectEntries{key, value ->
            [(key): ((1 - lambda) * (value[0] / heatSum) + lambda * (value[1] / probSum))]
        }

        return m
    }

    def walk(def userVertex, def m, def getDegree, def getRandomNeighbor){

        def p = getDegree(userVertex)
        def v = getRandomNeighbor(userVertex)
        p *= getDegree(v)
        v = getRandomNeighbor(v)
        def q = getDegree(v)
        p *= getDegree(v)
        v = getRandomNeighbor(v)
        q *= getDegree(v)
        def r = p / q
        //println "$userVertex.name | $v.name | $r ($p, $q)"
        m[v][0] = m[v][0] + r
        m[v][1] = m[v][1] + 1
    }

    def setup(def g){
        // nothing to do
    }

}
