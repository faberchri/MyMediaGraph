package ch.uzh.ifi.ddis.mymedialite.graph.zhou.randomwalk

/**
 * Created by faber on 23.03.15.
 */
class HybridShort extends HybridTwoHops{

    def walk(def userVertex, def m, def getDegree, def getRandomNeighbor){
        def v = getRandomNeighbor(userVertex) // get a liked item
        def betaDegree = getDegree(v)
        v = getRandomNeighbor(v) // get a user that liked an item that the target user liked (neighbor)
        v = getRandomNeighbor(v) // get an item liked by a neighbor
        def alphaDegree = getDegree(v)
        m[v] = m[v] + betaDegree**(1-lambda) / alphaDegree**(1-lambda)
    }

}
