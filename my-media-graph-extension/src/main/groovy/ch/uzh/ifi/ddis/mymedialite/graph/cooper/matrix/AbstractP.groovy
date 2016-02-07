package ch.uzh.ifi.ddis.mymedialite.graph.cooper.matrix

import no.uib.cipr.matrix.DenseMatrix
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

import com.tinkerpop.blueprints.Vertex

abstract class AbstractP extends BaseCooper {

	def vertexList

	def queryMatrix

	public AbstractP(def power, def alpha) {
		super(power, alpha)
	}

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		def row = vertexList.indexOf(userVertex)
		def queryMatrix = getQueryMatrix()
		def m = [:]
		for (int column = 0; column < queryMatrix.numColumns(); column++){
			m[vertexList[column]] = queryMatrix.get(row, column)
		}

		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		m = m.sort { a, b ->
			b.value <=> a.value
		}
		return m.keySet() as List
	}

	@Override
	public void train() {
		super.train()
		vertexList = getSortedListOfVertices(graph)
		queryMatrix = getPowerOfTransitionMatrix(
				getAlpha(),
				getPower(),
				getInverseDegreeMatrix(
				createDegreeMatrix(vertexList, userItemFeedbackEdgeLabel)
				),
				createAdjacencyMatrix(vertexList, userItemFeedbackEdgeLabel)
				)
	}

	def getSortedListOfVertices(def g){
		def vLi = graph.V(vertexType, userVertexType).toList()
		vLi.addAll(graph.V(vertexType, itemVertexType).toList())
		vLi = vLi.sort{ a,b ->
			def typeC = a.getProperty(vertexType)
					.compareTo(b.getProperty(vertexType))
			if (typeC != 0) {
				return typeC
			} else {
				if (a.getProperty(vertexType) == userVertexType) {
					return a.getProperty(myMediaLiteUserIdVertexProperty)
					.compareTo(b.getProperty(myMediaLiteUserIdVertexProperty))
				} else {
					return a.getProperty(myMediaLiteItemIdVertexProperty)
					.compareTo(b.getProperty(myMediaLiteItemIdVertexProperty))
				}
			}
		}
		return vLi
	}

	static def createAdjacencyMatrix(def vertices, def edgeLabel){

		def res = new DenseMatrix(vertices.size(), vertices.size())
		vertices.eachWithIndex{ v, i ->
			v.both(edgeLabel).sideEffect{ res.set(i, vertices.indexOf(it), 1.0) }.iterate()
		}
		return res
	}

	static def createDegreeMatrix(def vertices, def edgeLabel){
		def res = new DenseMatrix(vertices.size(), vertices.size())
		vertices.eachWithIndex{ v, i ->
			res.set(i, i, v.both(edgeLabel).count())
		}
		return res
	}

	static def getInverseDegreeMatrix(def degreeMatrix) {
		return invertDegreeMatrixFast(degreeMatrix)
		//return invertDegreeMatrixWithLia4j(degreeMatrix)
	}

	static def invertDegreeMatrixFast(def degreeMatrix){
		// degree matrix is square matrix with non-zero value on diagonal only
		def dimension = degreeMatrix.numRows()
		def inverted = new DenseMatrix(dimension, dimension)
		for (int i = 0; i < dimension; i++){
			inverted.set(i, i, 1.0 / degreeMatrix.get(i,i))
		}
		return inverted
	}

	//	def invertDegreeMatrixWithLia4j(def degreeMatrix){
	//		MatrixInverter inverter = degreeMatrix.withInverter(LinearAlgebra.GAUSS_JORDAN)
	//		return inverter.inverse(LinearAlgebra.DENSE_FACTORY)
	//	}

	static def getPowerOfTransitionMatrix(def alpha, def power, def invDeg, def adjacency){

		// TODO use here the property of centrosymmetry of P^* matrices for space efficient storage.
		// use an implementation of MTJ that uses JNI (BLAS, LAPACK).

		int dimension = adjacency.numRows()
		def transition = invDeg.mult(adjacency, new DenseMatrix(dimension, dimension))

		// raise each entry of transition to the power of alpha
		for (int i = 0; i < dimension; i++){
			for (int j = 0; j < dimension; j++){
				def tV = transition.get(i,j)
				// tV is always >= 0
				// we raise only positive values to alpha
				// otherwise entire transition matrix contains only 1
				if (tV > 0){
					double v = Math.pow(tV, alpha)
					transition.set(i, j, v)
				}
			}
		}

		def tmp = transition

		for(int i = 1; i < power; i++){
			tmp = tmp.mult(transition, new DenseMatrix(dimension, dimension))
		}
		return tmp
	}

}
