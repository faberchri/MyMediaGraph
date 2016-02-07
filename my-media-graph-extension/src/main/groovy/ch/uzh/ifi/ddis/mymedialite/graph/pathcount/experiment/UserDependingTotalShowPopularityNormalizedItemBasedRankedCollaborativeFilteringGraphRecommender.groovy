package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

class UserDependingTotalShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender
		extends UserDependingShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender {

	def getPopularityAffinity(def userVertex) {
		initItemPopularities()
		def affinity = 0
		def count = 0
		userVertex.out(userItemFeedbackEdgeLabel).sideEffect{watchedShow ->
			affinity += itemPopularities.get(watchedShow)
			count += maxItemPopularity
		}.iterate()
		affinity = affinity / count
		return affinity // affinity is == 1 if user watched only the most popular show
	}
}
