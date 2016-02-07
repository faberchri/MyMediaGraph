package ch.uzh.ifi.ddis.mymedialite.graph.gori

import no.uib.cipr.matrix.DenseMatrix
import no.uib.cipr.matrix.Matrix
import no.uib.cipr.matrix.Vector

class TestItemRankMatrixPrecalculated extends GroovyTestCase {

	TestGraph g = new TestGraph()

	ItemRankMatrixPrecalculated recommender =new ItemRankMatrixPrecalculated()

	public void testRank() {
		println "--- Rank ---"
		Vector d = g.getTestRoundedPersonalizationVector()
		Matrix c = g.getTestRoundedNormalizedCorrelationMatrix()
		Matrix precalculatedItemRanks = this.recommender.precalculateFast(c)
		def rankMap = recommender.rank(g.getItems(), d, precalculatedItemRanks)
		g.evalRankMap(rankMap)
	}

	public void testMatrixPower() {
		double[][] oA = new double[2][2]
		oA[0][0] = 2.5d
		oA[0][1] = 6.7d
		oA[1][0] = 3.1d
		oA[1][1] = 4.0d

		Matrix o = new DenseMatrix(oA)
		Matrix reference = new DenseMatrix(o)

		double[][] m0A = new double[2][2]
		m0A[0][0] = 1d
		m0A[0][1] = 0d
		m0A[1][0] = 0d
		m0A[1][1] = 1d
		Matrix m0 = new DenseMatrix(m0A)
		Matrix result = recommender.matrixPower(o, 0)
		assertTrue(matrixEquals(result, m0))
		assertTrue(matrixEquals(o, reference))

		result = recommender.matrixPower(o, 1)
		assertTrue(matrixEquals(result, reference))
		assertTrue(matrixEquals(o, reference))

		double[][] m2A = new double[2][2]
		m2A[0][0] = 27.020d
		m2A[0][1] = 43.550d
		m2A[1][0] = 20.150d
		m2A[1][1] = 36.770d
		Matrix m2 = new DenseMatrix(m2A)
		result = recommender.matrixPower(o, 2)
		assertTrue(matrixEquals(result, m2))
		assertTrue(matrixEquals(o, reference))

		double[][] m3A = new double[2][2]
		m3A[0][0] = 202.555d
		m3A[0][1] = 355.234d
		m3A[1][0] = 164.362d
		m3A[1][1] = 282.085d
		Matrix m3 = new DenseMatrix(m3A)
		result = recommender.matrixPower(o, 3)
		assertTrue(matrixEquals(result, m3))
		assertTrue(matrixEquals(o, reference))

		double[][] m4A = new double[2][2]
		m4A[0][0] = 1607.6129d
		m4A[0][1] = 2778.0545d
		m4A[1][0] = 1285.3685d
		m4A[1][1] = 2229.5654d
		Matrix m4 = new DenseMatrix(m4A)
		result = recommender.matrixPower(o, 4)
		assertTrue(matrixEquals(result, m4))
		assertTrue(matrixEquals(o, reference))

		double[][] m5A = new double[2][2]
		m5A[0][0] = 12631.0012d
		m5A[0][1] = 21883.22443d
		m5A[1][0] = 10125.07399d
		m5A[1][1] = 17530.23055d
		Matrix m5 = new DenseMatrix(m5A)
		result = recommender.matrixPower(o, 5)
		assertTrue(matrixEquals(result, m5))
		assertTrue(matrixEquals(o, reference))

		/////////

		def matrixPowers = recommender.calcAllMatrixPowers(o,6)
		assertTrue(matrixEquals(o, reference))
		assertTrue(matrixEquals(matrixPowers[0], m0))
		assertTrue(matrixEquals(matrixPowers[1], o))
		assertTrue(matrixEquals(matrixPowers[2], m2))
		assertTrue(matrixEquals(matrixPowers[3], m3))
		assertTrue(matrixEquals(matrixPowers[4], m4))
		assertTrue(matrixEquals(matrixPowers[5], m5))
	}

	boolean matrixEquals(Matrix a, Matrix b){
		if (a.numColumns() != b.numColumns()) return false
		if (a.numRows() != b.numRows()) return false
		for (int i = 0; i < a.numRows(); i++){
			for (int j = 0; j < a.numColumns(); j++){
				assertEquals(a.get(i, j), b.get(i, j), 0.000000001d)
			}
		}
		return true
	}
}
