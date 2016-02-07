package ch.uzh.ifi.ddis.mymedialite.graph.gori

import no.uib.cipr.matrix.DenseMatrix
import no.uib.cipr.matrix.DenseVector
import no.uib.cipr.matrix.Matrix
import no.uib.cipr.matrix.Vector

class ItemRankMatrixPrecalculated extends ItemRankMatrix {

	static final int PRECALCULATION_ITERATIONS = 1000

	@Override
	public void train() {
		// set up the usual user-item-feedback graph
		super.train()

		// get the correlation matrix
		this.items = graph.V(vertexType, itemVertexType).toList()
		Matrix c = new DenseMatrix(getNormalizedCorrelationMatrix(items))
		this.correlation = precalculateFast(c)
	}

	Matrix precalculateFast(Matrix c){
		Matrix r = new DenseMatrix(c.numColumns(), c.numColumns())
		Matrix cT
		for (i in 0..<PRECALCULATION_ITERATIONS) {
			if (i < 2) {
				cT = matrixPower(c, i)
			} else {
				cT = cT.mult(c,  new DenseMatrix(c.numColumns(), c.numRows()))
			}

			double alphaT = Math.pow(alpha, (double)i)
			Matrix cTScaled = new DenseMatrix(cT)
			cTScaled.scale(alphaT)
			r.add(cTScaled)
			println "Precalc iteration (fast): $i"
		}
		return r
	}

	Matrix precalculate(Matrix c){
		Matrix r = new DenseMatrix(c.numColumns(), c.numColumns())
		// def matrixPowers = calcAllMatrixPowers(c, PRECALCULATION_ITERATIONS)
		for (i in 0..<PRECALCULATION_ITERATIONS) {
			double alphaT = Math.pow(alpha, (double)i)
			//			Matrix cT = matrixPowers[i]
			Matrix cT = matrixPower(c,i)
			cT.scale(alphaT)
			r.add(cT)
		}
		return r
	}

	List calcAllMatrixPowers(Matrix m, int up){
		def l = []
		l[0] = matrixPower(m, 0)
		Matrix copy = new DenseMatrix(m)
		l[1] = copy
		Matrix prev = new DenseMatrix(m)
		for (i in 2..<up) {
			prev = prev.mult(copy,  new DenseMatrix(copy.numColumns(), copy.numRows()))
			l[i] = prev
			println "CalcAllMatrixPowers: $i"
		}
		return l
	}

	Matrix matrixPower(Matrix m, int power){
		if (power < 0 || !m.isSquare()){
			throw new IllegalArgumentException()
		}
		Matrix copy = new DenseMatrix(m)
		if (power == 0){
			copy.zero()
			for (int i = 0; i < copy.numRows(); i++){
				copy.set(i, i, 1.0d)
			}
			return copy
		}
		if (power == 1){
			return copy
		}
		int i = 1
		Matrix work = new DenseMatrix(copy)
		while (i < power) {
			work = work.mult(copy, new DenseMatrix(work.numColumns(), work.numRows()))
			//println("MatrixPower: $i")
			i++
		}
		return work
	}

	@Override
	public Object rank(Object items, Vector personalization, Matrix precalculatedItemRank) {
		personalization.scale(1.0d - alpha)
		Vector itemRanks = precalculatedItemRank.mult(personalization, new DenseVector(personalization.size()))
		return getRankMap(items, itemRanks)
	}
}
