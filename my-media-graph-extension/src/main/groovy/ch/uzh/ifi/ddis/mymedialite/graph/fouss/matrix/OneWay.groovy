package ch.uzh.ifi.ddis.mymedialite.graph.fouss.matrix

import ch.uzh.ifi.ddis.mymedialite.graph.fouss.LaplacianPseudoinverse
import com.tinkerpop.blueprints.Vertex
import no.uib.cipr.matrix.Matrix


/**
 * Created by faber on 24.11.14.
 */
class OneWay extends LaplacianPseudoinverse{
    @Override
    protected List<Vertex> runRankQuery(Object userVertex) {
        def m = [:]
        for (int column = 0; column < queryMatrix.numColumns(); column++){
            def itemVertex = vertexList[column]
            m[itemVertex] = getMetric(getQueryMatrix(), userVertex, itemVertex)
        }

        m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
        m = m.sort { a, b ->
            a.value <=> b.value
        }
        return m.keySet() as List
    }

    def getMetric(Matrix pseudoInvLap, def userVertex, def itemVertex){
        // pseudoinverse of laplacian is symmetric
        def userIndex = vertexList.indexOf(userVertex) // i
        def itemIndex = vertexList.indexOf(itemVertex) // k

        def l_ik = pseudoInvLap.get(userIndex, itemIndex)
        def l_kk = pseudoInvLap.get(itemIndex, itemIndex)

        def sum = 0.0
        for (int j = 0; j < pseudoInvLap.numRows(); j++){
            def d_jj = vertexList[j].both(userItemFeedbackEdgeLabel).count()
            def l_ij = pseudoInvLap.get(userIndex, j)
            def l_kj = pseudoInvLap.get(itemIndex, j)
            def tmp = (l_ij - l_ik - l_kj + l_kk) * d_jj
            sum += tmp
        }
        return sum
    }
}
