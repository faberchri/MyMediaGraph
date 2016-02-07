package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.PPopularityAbsoluteNormalized

class P3AutoPopularityAbsoluteNormalized extends
PPopularityAbsoluteNormalized {

	def headQuota = 0.6

	@Override
	public void train() {
		super.train()
		this.leverage = findLeverage()
		println "leverage: $leverage"
		initPopRanks(leverage)
	}

	def findLeverage(){
		def m = [:] // item counts map
		graph.V(vertexType, itemVertexType).sideEffect{
			m[it] = it.in(userItemFeedbackEdgeLabel).count()
		}.iterate()

		def totRatings = m.values().sum()
		def top = totRatings  * headQuota // where to abort
		m = m.sort { a, b ->
			b.value <=> a.value
		}

		def iC = 0
		def cR = 0
		for (i in m){
			cR += i.value
			iC++
			if (cR > top){
				def r = (m.size() - iC) / m.size()
				return r
			}
		}
	}
}
