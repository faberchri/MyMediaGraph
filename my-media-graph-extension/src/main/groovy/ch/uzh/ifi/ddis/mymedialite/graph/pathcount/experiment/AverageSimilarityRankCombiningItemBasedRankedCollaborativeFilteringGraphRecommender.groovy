package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

class AverageSimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender
		extends SimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender {

	@Override
	public Object sortRankListsMap(def rankListsMap) {
		return rankListsMap.sort {a, b -> a.value.sum()/a.value.size() <=> b.value.sum()/b.value.size()};
	}
}
