package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

class UserDependingMedianRankShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender
		extends UserDependingShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender {


	@Override
	public Object getPopularityAffinity(Object userVertex) {
		initItemPopularities()
		def ranks = []
		userVertex.out(userItemFeedbackEdgeLabel).sideEffect{watchedShow ->
			ranks.add(itemPopularityRanks.get(watchedShow))
		}.iterate()
		ranks.sort()
		def numberItems = ranks.size()
		def midNumber = (int)(numberItems/2)
		def median = numberItems %2 != 0 ? ranks[midNumber] : (ranks[midNumber] + ranks[midNumber-1])/2
		def affinity = median / itemPopularityRanks.size()
		return affinity // affinity is == 1 if user watched only the most popular show
	}
}
