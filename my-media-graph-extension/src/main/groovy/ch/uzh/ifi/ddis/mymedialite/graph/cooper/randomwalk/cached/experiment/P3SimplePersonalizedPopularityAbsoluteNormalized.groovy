package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment


class P3SimplePersonalizedPopularityAbsoluteNormalized extends P3PersonalizedNormalized {

	def lowerLeverageLimit = 0.55
	def upperLeverageLimit = 0.8

	def initPopRanks(){
		def leverageRangeSize = upperLeverageLimit - lowerLeverageLimit

		def m = [:]
		graph.V(vertexType, itemVertexType).sideEffect{
			m[it] = it.in(userItemFeedbackEdgeLabel).count()
		}.iterate()

		def maxItemPopularity = m.values().max()

		def leverageLog = []

		graph.V(vertexType, userVertexType).sideEffect{
			def watched = it.out(userItemFeedbackEdgeLabel).toList()
			def watchedTotalPop = 0
			for (i in watched){
				watchedTotalPop += i.in(userItemFeedbackEdgeLabel).count()
			}
			def numWatched = watched.size()
			def avgPopWatched = watchedTotalPop / numWatched

			def lowerLeverageLimitDistance = leverageRangeSize * (maxItemPopularity - avgPopWatched) / maxItemPopularity

			def l = lowerLeverageLimit + lowerLeverageLimitDistance
			l = Math.round(l*10.0) / 10.0
			//println "avgPopWatched: $avgPopWatched, lowerLeverageLimitDistance: $lowerLeverageLimitDistance, l: $l"

			def lM = [:]
			for (i in m){
				lM[i.key] = 1.0d / Math.pow(i.value, l)
			}
			// println "$it\t$lM"
			popularityRank[it] = lM
			leverageLog.add(l)
		}.iterate()

		// println "r:\t$popularityRank"
		leverageLog.sort()
		println "leverages: $leverageLog"
		println "Max leverage: ${leverageLog.max()}"
		println "Min leverage: ${leverageLog.min()}"
		println "Avg leverage: ${leverageLog.sum() / leverageLog.size()}"

	}
	@Override
	public String toString() {
		return super.toString() + " lowerLeverageLimit=$lowerLeverageLimit upperLeverageLimit=$upperLeverageLimit"
	}
}
