package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

class UserDependingRankedShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender
		extends UserDependingShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender {

	def getPopularityAffinity(def userVertex) {
		initItemPopularities()
		def affinity = 0
		def count = 0
		userVertex.out(userItemFeedbackEdgeLabel).sideEffect{watchedShow ->
			affinity += itemPopularityRanks.get(watchedShow)
			count++
		}.iterate()
		affinity = affinity / count
		affinity = affinity / itemPopularityRanks.size()
		return affinity // affinity is == 1 if user watched only the most popular show
	}
}
