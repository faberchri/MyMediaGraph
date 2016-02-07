package ch.uzh.ifi.ddis.mymedialite.graph.rwbenchmark

/**
 * Created by faber on 31.03.15.
 */
class P3Alpha extends BenchmarkBase{

    def alpha = 0.8

    def walk(def userVertex, def m, def getDegree, def getRandomNeighbor){
        def v = getRandomNeighbor(userVertex)

        def q = getDegree(v)
        v = getRandomNeighbor(v)

        q *= getDegree(v)
        v = getRandomNeighbor(v)

        m[v] = m[v] + q**(1 - getAlpha())
    }

    public String toString() {
        return super.toString() + " alpha=$alpha"
    }
}
