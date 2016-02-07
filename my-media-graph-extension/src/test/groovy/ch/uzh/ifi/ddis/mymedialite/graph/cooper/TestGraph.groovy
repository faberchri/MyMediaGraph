package ch.uzh.ifi.ddis.mymedialite.graph.cooper

import no.uib.cipr.matrix.DenseMatrix
import no.uib.cipr.matrix.Matrix
import ch.uzh.ifi.ddis.mymedialite.graph.GraphRecommender
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.matrix.AbstractP

import com.tinkerpop.blueprints.impls.tg.TinkerGraph



class TestGraph {

	def final feedbackEdge = GraphRecommender.userItemFeedbackEdgeLabel
	def final nameProperty ='name'
	def final g

	public TestGraph() {
		g = new TinkerGraph()
		initGraph()
	}

	def getVertexByName(def name) {
		return g.V(nameProperty, name).next()
	}

	def getAllVerticesSortedByName(){
		def vertices = g.V.toList()
		vertices.sort{a,b -> a.name.compareTo(b.name)}
		return vertices
	}

	def initGraph(){

		def user1 = g.addVertex()
		user1.setProperty(nameProperty,'User1')
		def user2 = g.addVertex()
		user2.setProperty(nameProperty,'User2')
		def user3 = g.addVertex()
		user3.setProperty(nameProperty,'User3')
		def user4 = g.addVertex()
		user4.setProperty(nameProperty,'User4')
		def user5 = g.addVertex()
		user5.setProperty(nameProperty,'User5')
		def user6 = g.addVertex()
		user6.setProperty(nameProperty,'User6')

		def itemA = g.addVertex()
		itemA.setProperty(nameProperty,'ItemA')
		def itemB = g.addVertex()
		itemB.setProperty(nameProperty,'ItemB')
		def itemC = g.addVertex()
		itemC.setProperty(nameProperty,'ItemC')
		def itemD = g.addVertex()
		itemD.setProperty(nameProperty,'ItemD')

		g.addEdge(null,user1, itemA, feedbackEdge)

		g.addEdge(null,user2, itemA, feedbackEdge)
		g.addEdge(null,user2, itemB, feedbackEdge)

		g.addEdge(null,user3, itemA, feedbackEdge)
		g.addEdge(null,user3, itemB, feedbackEdge)
		g.addEdge(null,user3, itemC, feedbackEdge)

		g.addEdge(null,user4, itemB, feedbackEdge)
		g.addEdge(null,user4, itemC, feedbackEdge)

		g.addEdge(null,user5, itemC, feedbackEdge)
		g.addEdge(null,user5, itemD, feedbackEdge)

		g.addEdge(null,user6, itemD, feedbackEdge)
	}

	def getTransitionMatrix(def alpha, def walkLength){
		def vertexList = getAllVerticesSortedByName()

		def transitionMatrix = AbstractP.getPowerOfTransitionMatrix(
				alpha,
				walkLength,
				AbstractP.getInverseDegreeMatrix(
				AbstractP.createDegreeMatrix(vertexList, feedbackEdge)
				),
				AbstractP.createAdjacencyMatrix(vertexList, feedbackEdge)
				)
		return transitionMatrix
	}

	def getProbability(def startVertexName, def targetVertexName, def alpha, def walkLength){

		def transitionMatrix = getTransitionMatrix(alpha, walkLength)
		return getProbability(startVertexName, targetVertexName, transitionMatrix)
	}

	def getProbability(def startVertexName, def targetVertexName, def transitionMatrix){
		//println transitionMatrix

		def vertices = g.V.name.toList()
		vertices.sort()

		def startIndex = vertices.indexOf(startVertexName)
		def targetIndex = vertices.indexOf(targetVertexName)

		return transitionMatrix.get(startIndex, targetIndex)
	}

	def getNormalizedProbability(def startVertexName, def targetVertexName, def alpha, def walkLength){
		Matrix transitionMatrix = getTransitionMatrix(alpha, walkLength)
		//println transitionMatrix
		def rowNorms = []
		for (int r = 0; r < transitionMatrix.numRows(); r++) {
			def s = 0.0
			for (int c = 0; c < transitionMatrix.numColumns(); c++) {
				s += transitionMatrix.get(r, c)
			}
			rowNorms.add(s)
		}
		//println rowNorms
		Matrix normalizedM = new DenseMatrix(transitionMatrix.numRows(), transitionMatrix.numColumns())
		for (int r = 0; r < transitionMatrix.numRows(); r++) {
			def norm = rowNorms[r]
			for (int c = 0; c < transitionMatrix.numColumns(); c++) {
				normalizedM.set(r, c, transitionMatrix.get(r, c) / norm)
			}
		}

		return getProbability(startVertexName, targetVertexName, normalizedM)
	}
}

