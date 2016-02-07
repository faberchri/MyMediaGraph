package ch.uzh.ifi.ddis.mymedialite.graph.zhou

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender
import com.tinkerpop.blueprints.Vertex
import no.uib.cipr.matrix.DenseMatrix
import no.uib.cipr.matrix.DenseVector
import no.uib.cipr.matrix.Matrix

import java.util.concurrent.Callable
import java.util.concurrent.Executors

abstract class AbstractS extends ItemRankGraphRecommender{

    def userVertices
    def itemVertices

    Matrix wMatrix;

    @Override
    public void train() {
        super.train()
        setup(graph)
        println "-- Training of ${getClass().getSimpleName()} done --"
    }

    @Override
    protected List<Vertex> runRankQuery(Object userVertex) {
        def fT = getFTilde(userVertex)
        fT.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
        fT = fT.sort { a, b ->
            b.value <=> a.value
        }
        return fT.keySet() as List
    }

    def abstract getLambda();

    def setup(def g){
        userVertices = g.V(vertexType, userVertexType).toList()
        itemVertices = g.V(vertexType, itemVertexType).toList()
        wMatrix = calcW()
    }

    def protected calcW(){
        def lambda = getLambda()
        double[][] w = new double[itemVertices.size()][itemVertices.size()]

        // cache inverted user degrees in a map (optimization)
        def invertedUserDegrees = [:]
        userVertices.each {user ->
            invertedUserDegrees[user] = 1 / user.out(userItemFeedbackEdgeLabel).count()
        }

        // cache item neighbors in map of sets (optimization)
        def itemNeighbors =[:]
        itemVertices.each{item ->
            itemNeighbors[item] = item.in(userItemFeedbackEdgeLabel).toList() as Set
        }

        def task = {alpha, alphaIndex, alphaIn ->
            //def start = System.currentTimeMillis()
            itemVertices.eachWithIndex { beta, betaIndex ->
                def betaIn = itemNeighbors[beta]
                def sum = 0
                userVertices.each { user ->
                    if (alphaIn.contains(user) && betaIn.contains(user)) {
                        sum += invertedUserDegrees[user]
                    }
                }
                def kAlpha = alphaIn.size()
                def kBeta = betaIn.size()
                w[alphaIndex][betaIndex] = sum / (kAlpha**(1 - lambda) * kBeta**lambda)
            }
            //def now = System.currentTimeMillis()
            //def t = now - start
            //return t / itemVertices.size()

        }
        def pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        def futures = []
        def defer = { c ->
            pool.submit(c as Callable)
        }
        itemVertices.eachWithIndex {alpha, alphaIndex ->
            def alphaIn = itemNeighbors[alpha]
            futures.add(defer{task(alpha, alphaIndex, alphaIn)})
        }
        pool.shutdown()
        println "W-matrix calculation progress ** target: ${itemVertices.size()} **"
        def pC = 1
        //def rT = 0
        futures.eachWithIndex{f, i ->
            //rT += f.get()
            f.get()
            if (pC % 25 == 0){
                println ""
            }
            print "-${i + 1}"
            // print "-${i}(${rT / (i + 1)})"
            pC++

        }
        println ""
        return new DenseMatrix(w)
    }

    def protected getF(def userVertex){
        def likedItems = userVertex.out(userItemFeedbackEdgeLabel).toList() as Set
        double[] f = new double[itemVertices.size()]
        itemVertices.eachWithIndex {item, index->
            if (likedItems.contains(item)){
                f[index] = 1
            }
        }
        return new DenseVector(f)
    }

    protected Map getFTilde(def userVertex){
        def fTilde = wMatrix.mult(getF(userVertex), new DenseVector(itemVertices.size()))
        def fTMap = [:]
        itemVertices.eachWithIndex {item, index ->
            fTMap[item] = fTilde.get(index)
        }
        return fTMap
    }

    @Override
    public String toString() {
        return super.toString() + " lambda=${getLambda()}"
    }
}
