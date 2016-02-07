package ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized


class PPopularityRankNormalized extends PGlobalNormalized{

	public PPopularityRankNormalized(def power, def leverage) {
		super(power, leverage)
	}

	def initPopRanks(def leverage){
		def m = [:]
		graph.V(vertexType, itemVertexType).sideEffect{
			m[it] = it.in(userItemFeedbackEdgeLabel).count()
		}.iterate()
		m = m.sort { a, b ->
			a.value <=> b.value
		}
		//		println "a:\t$m"
		def cCount = 0
		int r = 0
		def pR = [:]
		for (i in m){
			if (i.value > cCount){
				r++
				cCount = i.value
			}
			pR[i.key] = 1.0d / Math.pow(r,leverage)
		}
		//		println "r:\t$popularityRank"
		this.popularityRank = pR
	}
}
