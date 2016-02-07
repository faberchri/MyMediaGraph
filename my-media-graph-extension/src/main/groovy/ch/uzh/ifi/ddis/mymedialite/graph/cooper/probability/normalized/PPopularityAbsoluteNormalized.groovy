package ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized

import com.tinkerpop.blueprints.Vertex

class PPopularityAbsoluteNormalized extends PGlobalNormalized {

	public PPopularityAbsoluteNormalized(def power, def leverage) {
		super(power, leverage)
	}

	def initPopRanks(def leverage){
		def m = [:]
		graph.V(vertexType, itemVertexType).sideEffect{
			m[it] = it.in(userItemFeedbackEdgeLabel).count()
		}.iterate()

		//		println "a:\t$m"
		def pR = [:]
		for (i in m){
			pR[i.key] = 1.0d / Math.pow(i.value,leverage)
		}
		//		println "r:\t$popularityRank"
		this.popularityRank = pR
	}
}
