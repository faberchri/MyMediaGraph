package ch.uzh.ifi.ddis.mymedialite.graph.rwbenchmark

/**
 * Created by faber on 31.03.15.
 */
class ANP3 extends BenchmarkBase{

    def beta = 0.5

    def walk(def userVertex, def m, def getDegree, def getRandomNeighbor){
        def v = getRandomNeighbor(userVertex)
        v = getRandomNeighbor(v)
        v = getRandomNeighbor(v)
        m[v] = m[v] + 1
    }

    def rerank(m){
        def r = [:]
        m.each{k, v ->
            r[k] = v / ((k.in(userItemFeedbackEdgeLabel).count())**beta)
        }
        return r
    }

    public String toString() {
        return super.toString() + " beta=$beta"
    }
}
