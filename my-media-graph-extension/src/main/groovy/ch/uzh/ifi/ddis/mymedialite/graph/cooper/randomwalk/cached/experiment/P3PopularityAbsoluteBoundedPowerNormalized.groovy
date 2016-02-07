package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.PGlobalNormalized

class P3PopularityAbsoluteBoundedPowerNormalized extends PGlobalNormalized {

	def initPopRanks(def leverage){
		def m = [:]
		graph.V(vertexType, itemVertexType).sideEffect{
			m[it] = it.in(userItemFeedbackEdgeLabel).count()
		}.iterate()
		m= m.sort{a,b ->
			a.value <=> b.value
		}

		def bound = m.values().max() // get max ratings count
		bound = bound + 1 // we have to add 1 otherwise the most pop item always receives 0 counts
		//		println bound
		def norm = -1 * Math.pow((1/bound), leverage)
		//		println "a:\t$m"
		def pR = [:]
		//		def t = [:]
		for (i in m){
			def v = norm * Math.pow(i.value,leverage) + 1
			pR[i.key] = v
			//			t[i.value] = v
		}
		//println "pR:\t$pR"
		//		println "pR.v:\t${pR.values()}"
		//		t = t.sort{a,b->
		//			a.key <=> b.key
		//		}
		//		println "pR.v:\t${t}"
		this.popularityRank = pR
	}
}
