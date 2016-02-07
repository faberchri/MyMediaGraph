package ch.uzh.ifi.ddis.mymedialite.graph.zhou

import ch.uzh.ifi.ddis.mymedialite.graph.GraphRecommender
import com.tinkerpop.blueprints.impls.tg.TinkerGraph
import com.tinkerpop.gremlin.groovy.Gremlin
import no.uib.cipr.matrix.DenseMatrix
import no.uib.cipr.matrix.DenseVector
import no.uib.cipr.matrix.Matrix


class TestGraph {

    def static final feedbackEdge = GraphRecommender.userItemFeedbackEdgeLabel
    def static final type = GraphRecommender.vertexType
    def static final userType = GraphRecommender.userVertexType
    def static final itemType = GraphRecommender.itemVertexType
    def static final nameProperty ='name'

    def static g = new TinkerGraph()
    static {
        Gremlin.load()
        initGraph()
    }

    def static initGraph(){
        def user1 = g.addVertex()
        user1.setProperty(nameProperty,'User1')
        user1.setProperty(type,userType)
        def user2 = g.addVertex()
        user2.setProperty(nameProperty,'User2')
        user2.setProperty(type,userType)
        def user3 = g.addVertex()
        user3.setProperty(nameProperty,'User3')
        user3.setProperty(type,userType)
        def user4 = g.addVertex()
        user4.setProperty(nameProperty,'User4')
        user4.setProperty(type,userType)


        def itemA = g.addVertex()
        itemA.setProperty(nameProperty,'ItemA')
        itemA.setProperty(type,itemType)
        def itemB = g.addVertex()
        itemB.setProperty(nameProperty,'ItemB')
        itemB.setProperty(type,itemType)
        def itemC = g.addVertex()
        itemC.setProperty(nameProperty,'ItemC')
        itemC.setProperty(type,itemType)
        def itemD = g.addVertex()
        itemD.setProperty(nameProperty,'ItemD')
        itemD.setProperty(type,itemType)
        def itemE = g.addVertex()
        itemE.setProperty(nameProperty,'ItemE')
        itemE.setProperty(type,itemType)


        g.addEdge(null,user1, itemA, feedbackEdge)
        g.addEdge(null,user1, itemD, feedbackEdge)

        g.addEdge(null,user2, itemA, feedbackEdge)
        g.addEdge(null,user2, itemB, feedbackEdge)
        g.addEdge(null,user2, itemC, feedbackEdge)
        g.addEdge(null,user2, itemD, feedbackEdge)

        g.addEdge(null,user3, itemA, feedbackEdge)
        g.addEdge(null,user3, itemC, feedbackEdge)

        g.addEdge(null,user4, itemC, feedbackEdge)
        g.addEdge(null,user4, itemE, feedbackEdge)
    }

    static DenseMatrix getWh(){
        // row normalized
        return new DenseMatrix(
                [[  5/4/3,  1/4/3,  3/4/3,  3/4/3,    0/3],
                [   1/4/1,  1/4/1,  1/4/1,  1/4/1,    0/1],
                [   3/4/3,  1/4/3,  5/4/3,  1/4/3,  1/2/3],
                [   3/4/2,  1/4/2,  1/4/2,  3/4/2,    0/2],
                [     0/1,    0/1,  1/2/1,    0/1,  1/2/1]] as double[][])
    }

    static DenseMatrix getWp(){
        // column normalized
        return new DenseMatrix(
                [[  5/4/3,  1/4/1,  3/4/3,  3/4/2,    0/1],
                [   1/4/3,  1/4/1,  1/4/3,  1/4/2,    0/1],
                [   3/4/3,  1/4/1,  5/4/3,  1/4/2,  1/2/1],
                [   3/4/3,  1/4/1,  1/4/3,  3/4/2,    0/1],
                [     0/3,    0/1,  1/2/3,    0/2,  1/2/1]] as double[][])
    }

    static DenseMatrix getWhp(def l){
        // column normalized
        return new DenseMatrix(
                 [[  (1/(3**(1-l) * 3**l)) * 5/4,  (1/(3**(1-l) * 1**l)) * 1/4,  (1/(3**(1-l) * 3**l)) * 3/4,  (1/(3**(1-l) * 2**l)) * 3/4,  (1/(3**(1-l) * 1**l)) *   0],
                 [   (1/(1**(1-l) * 3**l)) * 1/4,  (1/(1**(1-l) * 1**l)) * 1/4,  (1/(1**(1-l) * 3**l)) * 1/4,  (1/(1**(1-l) * 2**l)) * 1/4,  (1/(1**(1-l) * 1**l)) *   0],
                 [   (1/(3**(1-l) * 3**l)) * 3/4,  (1/(3**(1-l) * 1**l)) * 1/4,  (1/(3**(1-l) * 3**l)) * 5/4,  (1/(3**(1-l) * 2**l)) * 1/4,  (1/(3**(1-l) * 1**l)) * 1/2],
                 [   (1/(2**(1-l) * 3**l)) * 3/4,  (1/(2**(1-l) * 1**l)) * 1/4,  (1/(2**(1-l) * 3**l)) * 1/4,  (1/(2**(1-l) * 2**l)) * 3/4,  (1/(2**(1-l) * 1**l)) *   0],
                 [   (1/(1**(1-l) * 3**l)) *   0,  (1/(1**(1-l) * 1**l)) *   0,  (1/(1**(1-l) * 3**l)) * 1/2,  (1/(1**(1-l) * 2**l)) *   0,  (1/(1**(1-l) * 1**l)) * 1/2]] as double[][])
    }

    private static DenseMatrix getWraw(){
        //            A     B     C     D     E
        return new DenseMatrix(
                [[  5/4,  1/4,  3/4,  3/4,    0],
                [   1/4,  1/4,  1/4,  1/4,    0],
                [   3/4,  1/4,  5/4,  1/4,  1/2],
                [   3/4,  1/4,  1/4,  3/4,    0],
                [     0,    0,  1/2,    0,  1/2]] as double[][])

    }

    static DenseVector getF(def user){
        switch (user){
            case 1:
                return new DenseVector([1,0,0,1,0] as double[])
            case 2:
                return new DenseVector([1,1,1,1,0] as double[])
            case 3:
                return new DenseVector([1,0,1,0,0] as double[])
            case 4:
                return new DenseVector([0,0,1,0,1] as double[])
        }
    }

    def static getFTilde(def user, Matrix w){
        //println "Ground Truth W:\n$w"
        def f = getF(user)
        //println "f-User$user: $f"
        return w.mult(f, new DenseVector(f.size()))
    }

    def static getFTildeMap(def user, Matrix w){
        def weights = getFTilde(user, w)
        //println "fTilde-User$user: $weights"
        return  ['ItemA' : weights.get(0),
                 'ItemB' : weights.get(1),
                 'ItemC' : weights.get(2),
                 'ItemD' : weights.get(3),
                 'ItemE' : weights.get(4)]
    }
}
