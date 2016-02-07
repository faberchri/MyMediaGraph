package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.PGlobalNormalized

class P3PopularityAbsoluteInvertedExponentialNormalized extends
PGlobalNormalized {

	def initPopRanks(def leverage){
		def m = [:]
		graph.V(vertexType, itemVertexType).sideEffect{
			m[it] = it.in(userItemFeedbackEdgeLabel).count()
		}.iterate()

		//		println "a:\t$m"
		def pR = [:]
		for (i in m){
			pR[i.key] = 1.0d / Math.pow(leverage,i.value)
		}
		//		println "r:\t$popularityRank"
		println pR
		this.popularityRank = pR
	}

}
