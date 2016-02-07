package ch.uzh.ifi.ddis.mymedialite.graph.rwbenchmark

/**
 * Created by faber on 31.03.15.
 */
class HybridS extends BenchmarkBase{

    def lambda = 0.5

    def walk(def userVertex, def m, def getDegree, def getRandomNeighbor){
        def v = getRandomNeighbor(userVertex) // get a liked item
        def betaDegree = getDegree(v)
        v = getRandomNeighbor(v) // get a user that liked an item that the target user liked (neighbor)
        v = getRandomNeighbor(v) // get an item liked by a neighbor
        def alphaDegree = getDegree(v)
        m[v] = m[v] + betaDegree**(1-lambda) / alphaDegree**(1-lambda)
    }

    public String toString() {
        return super.toString() + " lambda=$lambda"
    }

}
