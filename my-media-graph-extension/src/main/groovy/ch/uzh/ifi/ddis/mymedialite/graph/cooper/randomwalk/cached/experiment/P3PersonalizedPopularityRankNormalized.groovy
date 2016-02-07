package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment


class P3PersonalizedPopularityRankNormalized extends P3PersonalizedNormalized {

	def initPopRanks() {
		// map with item -> counts (unorderd)
		def countsMap = getItemCountsMap()

		// map with item -> rank (ordered, highest first)
		def ranksMap = getItemRanksMap(countsMap)
		println ranksMap

		// item at half of all ratings
		def medianItem = getHalfRatingItem(countsMap)

		// rank of item at half of all ratings
		def medianRank = ranksMap[medianItem]

		def users = graph.V(vertexType, userVertexType).toList()
		for (u in users){
			def userItems = u.out(userItemFeedbackEdgeLabel).toList()
			def userItemCounts = [:]
			for (uI in userItems){
				userItemCounts[uI] = countsMap[uI]
			}
			def userMedianItem = getHalfRatingItem(userItemCounts)
			def userMedianRank = ranksMap[userMedianItem]

			def l = medianRank / userMedianRank
			l = Math.log(l)

			println "$medianItem, $medianRank, $userMedianItem, $userMedianRank, $l"

			def leveragedRanks = [:]
			for (i in ranksMap){
				leveragedRanks[i.key] = 1.0d / Math.pow(i.value, l)
			}
			popularityRank[u] = leveragedRanks
		}
	}

	def getItemCountsMap(){
		def m = [:]
		graph.V(vertexType, itemVertexType).sideEffect{
			m[it] = it.in(userItemFeedbackEdgeLabel).count()
		}.iterate()
		return m
	}

	def getItemRanksMap(def m){
		m = m.sort { a, b ->
			b.value <=> a.value
		}
		def itemRanks = [:]
		def cCount = Integer.MAX_VALUE
		int r = 0
		for (i in m){
			if (i.value < cCount){
				r++
				cCount = i.value
			}
			itemRanks[i.key] = r
		}
		return itemRanks
	}

	def getHalfRatingItem(def m){
		m = m.sort { a, b ->
			b.value <=> a.value
		}
		// item at 50% of ratings
		def cRatings = 0
		def halfRatings = m.values().sum() / 2.0
		for (i in m){
			cRatings += i.value
			if (cRatings > halfRatings){
				return i.key
			}
		}
	}

}
