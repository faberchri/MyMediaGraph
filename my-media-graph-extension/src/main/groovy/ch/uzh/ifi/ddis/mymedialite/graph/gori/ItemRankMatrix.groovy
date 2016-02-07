package ch.uzh.ifi.ddis.mymedialite.graph.gori

import no.uib.cipr.matrix.DenseMatrix
import no.uib.cipr.matrix.DenseVector
import no.uib.cipr.matrix.Matrix
import no.uib.cipr.matrix.Vector

import com.tinkerpop.blueprints.Vertex


class ItemRankMatrix extends AbstractItemRank {


	static final int ONLINE_ITERATIONS = 20

	Matrix correlation

	List items

	@Override
	public void train() {
		// set up the usual user-item-feedback graph
		super.train()

		// get the correlation matrix
		this.items = graph.V(vertexType, itemVertexType).toList()
		this.correlation = new DenseMatrix(getNormalizedCorrelationMatrix(items))
	}

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		Vector personalization = getInitialPersonalizationVector(items, userVertex)

		def ranks = rank(items, personalization, correlation)
		return sortAndShortenResultList(ranks, userVertex)
	}

	def rank(def items, Vector personalization, Matrix correlation){
		// copy and scale (by alpha) correlation matrix
		correlation = new DenseMatrix(correlation)
		correlation.scale(alpha)

		// copy and scale (by 1.0 - alpha) personalization vector
		personalization = new DenseVector(personalization)
		personalization.scale(1.0d - alpha)

		// initialize IR vector
		Vector itemRanks = getInitialItemRankVector(items)

		//		println "C-0:\n$correlation"
		//		println "P-0:\n$personalization"
		//		println "IR-0:\n$itemRanks"
		//		println "---"
		for (i in 0..<ONLINE_ITERATIONS) {

			itemRanks = iterate(itemRanks, personalization, correlation)

			//println "P-${i+1}:\n$personalization"
			//println "C-${i+1}:\n$correlation"
			//println "IR-${i+1}:\n$itemRanks\n---"
		}
		return getRankMap(items, itemRanks)
	}

	Vector iterate(Vector itemRank, Vector personalization, Matrix correlation){
		Vector t1 = new DenseVector(correlation.numRows())

		correlation.mult(itemRank, t1)
		t1.add(personalization)

		return t1
	}

	Vector getInitialItemRankVector(List items){
		int d = items.size()
		double[] pr = new double[d]
		for (int i = 0; i < d; i++) {
			pr[i] = 1.0 / (double) d
		}
		return new DenseVector( pr)
	}

	Vector getInitialPersonalizationVector(List items, Vertex userVertex){
		def fb = userVertex.out(userItemFeedbackEdgeLabel).toList() as Set
		double norm = fb.size().doubleValue()
		int d = items.size()
		double[] p = new double[d]
		for (int i = 0; i < d; i++) {
			if(fb.contains(items[i])){
				p[i] = 1.0d / norm
			} else {
				p[i] = 0.0
			}
		}
		return new DenseVector(p)
	}

	Map getRankMap(List items, Vector itemRank){
		def m = [:]
		items.eachWithIndex{ item, index ->
			m[item] = itemRank.get(index)
		}
		return m
	}

}
