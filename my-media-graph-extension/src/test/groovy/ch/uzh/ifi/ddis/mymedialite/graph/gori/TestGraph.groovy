package ch.uzh.ifi.ddis.mymedialite.graph.gori

import junit.framework.Assert
import no.uib.cipr.matrix.DenseMatrix
import no.uib.cipr.matrix.DenseVector
import no.uib.cipr.matrix.Matrix
import no.uib.cipr.matrix.Vector
import ch.uzh.ifi.ddis.mymedialite.graph.GraphRecommender

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.tg.TinkerGraph
import com.tinkerpop.gremlin.groovy.Gremlin

class TestGraph {

	static {
		Gremlin.load()
	}

	def g = new TinkerGraph()

	def users = []

	def items = []

	public TestGraph() {
		for(i in 1..12){
			Vertex u = g.addVertex()
			users.add(u)
			u.setProperty(GraphRecommender.vertexType, GraphRecommender.userVertexType)
			u.setProperty("name", "user_$i")
		}


		for(i in 1..5){
			Vertex item = g.addVertex()
			items.add(item)
			item.setProperty(GraphRecommender.vertexType, GraphRecommender.itemVertexType)
			item.setProperty("name", "item_$i")
		}

		def eN = GraphRecommender.userItemFeedbackEdgeLabel

		g.addEdge(null, users[0], items[0], eN)
		g.addEdge(null, users[0], items[1], eN)

		g.addEdge(null, users[1], items[1], eN)
		g.addEdge(null, users[1], items[2], eN)
		g.addEdge(null, users[1], items[3], eN)

		g.addEdge(null, users[2], items[0], eN)
		g.addEdge(null, users[2], items[2], eN)

		g.addEdge(null, users[3], items[0], eN)
		g.addEdge(null, users[3], items[3], eN)
		g.addEdge(null, users[3], items[4], eN)

		g.addEdge(null, users[4], items[0], eN)
		g.addEdge(null, users[4], items[1], eN)
		g.addEdge(null, users[4], items[2], eN)
		g.addEdge(null, users[4], items[3], eN)

		g.addEdge(null, users[5], items[0], eN)
		g.addEdge(null, users[5], items[2], eN)

		g.addEdge(null, users[6], items[0], eN)
		g.addEdge(null, users[6], items[2], eN)
		g.addEdge(null, users[6], items[3], eN)

		g.addEdge(null, users[7], items[0], eN)
		g.addEdge(null, users[7], items[1], eN)
		g.addEdge(null, users[7], items[3], eN)

		g.addEdge(null, users[8], items[0], eN)
		g.addEdge(null, users[8], items[4], eN)

		g.addEdge(null, users[9], items[0], eN)
		g.addEdge(null, users[9], items[4], eN)

		g.addEdge(null, users[10], items[1], eN)
		g.addEdge(null, users[10], items[3], eN)

		g.addEdge(null, users[11], items[2], eN)
		g.addEdge(null, users[11], items[3], eN)
	}

	double[][] getTestRawCorrelationArray(){
		double[][] m = new double[5][5]
		m[0][0] = 0.0; m[0][1] = 3.0; m[0][2] = 4.0; m[0][3] = 4.0; m[0][4] = 3.0
		m[1][0] = 3.0; m[1][1] = 0.0; m[1][2] = 2.0; m[1][3] = 4.0; m[1][4] = 0.0
		m[2][0] = 4.0; m[2][1] = 2.0; m[2][2] = 0.0; m[2][3] = 4.0; m[2][4] = 0.0
		m[3][0] = 4.0; m[3][1] = 4.0; m[3][2] = 4.0; m[3][3] = 0.0; m[3][4] = 1.0
		m[4][0] = 3.0; m[4][1] = 0.0; m[4][2] = 0.0; m[4][3] = 1.0; m[4][4] = 0.0

		return m
	}

	double[][] getTestNormalizedCorrelationArray(){
		double[][] m = new double[5][5]
		m[0][0] = 0.0 / 14.0d; m[0][1] = 3.0 / 9.0d; m[0][2] = 4.0 / 10.0d; m[0][3] = 4.0 / 13.0d; m[0][4] = 3.0 / 4.0d
		m[1][0] = 3.0 / 14.0d; m[1][1] = 0.0 / 9.0d; m[1][2] = 2.0 / 10.0d; m[1][3] = 4.0 / 13.0d; m[1][4] = 0.0 / 4.0d
		m[2][0] = 4.0 / 14.0d; m[2][1] = 2.0 / 9.0d; m[2][2] = 0.0 / 10.0d; m[2][3] = 4.0 / 13.0d; m[2][4] = 0.0 / 4.0d
		m[3][0] = 4.0 / 14.0d; m[3][1] = 4.0 / 9.0d; m[3][2] = 4.0 / 10.0d; m[3][3] = 0.0 / 13.0d; m[3][4] = 1.0 / 4.0d
		m[4][0] = 3.0 / 14.0d; m[4][1] = 0.0 / 9.0d; m[4][2] = 0.0 / 10.0d; m[4][3] = 1.0 / 13.0d; m[4][4] = 0.0 / 4.0d

		return m
	}

	Matrix getTestNormalizedCorrelationMatrix(){
		return new DenseMatrix(getTestNormalizedCorrelationArray())
	}

	double[][] getTestRoundedNormalizedCorrelationArray(){
		double[][] m = new double[5][5]
		m[0][0] = 0.000d; m[0][1] = 0.333d; m[0][2] = 0.400d; m[0][3] = 0.307d; m[0][4] = 0.750d
		m[1][0] = 0.214d; m[1][1] = 0.000d; m[1][2] = 0.200d; m[1][3] = 0.307d; m[1][4] = 0.000d
		m[2][0] = 0.285d; m[2][1] = 0.222d; m[2][2] = 0.000d; m[2][3] = 0.307d; m[2][4] = 0.000d
		m[3][0] = 0.285d; m[3][1] = 0.444d; m[3][2] = 0.400d; m[3][3] = 0.000d; m[3][4] = 0.250d
		m[4][0] = 0.214d; m[4][1] = 0.000d; m[4][2] = 0.000d; m[4][3] = 0.076d; m[4][4] = 0.000d

		return m
	}

	Matrix getTestRoundedNormalizedCorrelationMatrix(){
		return new DenseMatrix(getTestRoundedNormalizedCorrelationArray())
	}

	Vector getTestRoundedPersonalizationVector(){
		double[] personalization = new double[5]
		personalization[0] = 0.66d
		personalization[1] = 0.33d
		personalization[2] = 0.0d
		personalization[3] = 0.0d
		personalization[4] = 0.0d
		return new DenseVector(personalization)
	}

	Map getTestRoundedPersonalizationMap(){
		def personalization = [:]
		personalization[items[0]] = 0.66d
		personalization[items[1]] = 0.33d
		return personalization
	}

	def evalRankMap(def rankMap, allowedDeviation = 0.0002){
		println "rankMap: $rankMap"
		Assert.assertEquals(0.3175, rankMap[items[0]], allowedDeviation)
		Assert.assertEquals(0.1952, rankMap[items[1]], allowedDeviation)
		Assert.assertEquals(0.1723, rankMap[items[2]], allowedDeviation)
		Assert.assertEquals(0.2245, rankMap[items[3]], allowedDeviation)
		Assert.assertEquals(0.0723, rankMap[items[4]], allowedDeviation)
	}
}
