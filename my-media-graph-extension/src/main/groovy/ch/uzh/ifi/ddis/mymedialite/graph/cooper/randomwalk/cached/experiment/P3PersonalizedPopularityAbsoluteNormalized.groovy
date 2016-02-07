package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

class P3PersonalizedPopularityAbsoluteNormalized extends P3PersonalizedNormalized {

	def initPopRanks(){
		def m = [:]
		graph.V(vertexType, itemVertexType).sideEffect{
			m[it] = it.in(userItemFeedbackEdgeLabel).count()
		}.iterate()

		// average item pop
		def avgItemPopularity = m.values().sum() / m.size()
		println "Avg. item pop: $avgItemPopularity"

		graph.V(vertexType, userVertexType).sideEffect{
			def li = it.out(userItemFeedbackEdgeLabel).toList()
			def s = 0
			for (i in li){
				s += i.in(userItemFeedbackEdgeLabel).count()
			}
			def c = li.size()
			def n = s / c
			def l
			if (n < avgItemPopularity){
				l = avgItemPopularity / n
			} else {
				l = (n / avgItemPopularity) * -1
			}
			println "$it\ts:$s,\tc:$c,\tn:$n\tl:$l"
			def lM = [:]
			for (i in m){
				lM[i.key] = 1.0d / Math.pow(i.value, l)
			}
			// println "$it\t$lM"
			popularityRank[it] = lM
		}.iterate()

		// println "r:\t$popularityRank"

	}

}
