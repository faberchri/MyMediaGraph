package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

import java.util.List;

import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.ItemBasedPureCF3Path;

import com.tinkerpop.blueprints.Vertex;

abstract class SimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender
		extends ItemBasedPureCF3Path {

	
	@Override
	protected List<Vertex> runRankQuery(def userVertex) {
	
		// multiple views of same show cause duplication of counts of similar shows (no dedup)
		def res = [:].withDefault{[]} // map of lists; lists contain ranks of similarity of watched shows to the key show
		def watchedShows = [] as Set
		userVertex.out(userItemFeedbackEdgeLabel).sideEffect{watchedShow ->
			watchedShows.add(watchedShow)
			def similarShows = getCachedSimilarShows(watchedShow);
			
			// Attention: Only efficient if we receive a sorted map here (best first)
			// to assure correctness we sort again (should run in O(n) assuming Timsort)
			similarShows = similarShows.sort {a, b -> b.value <=> a.value}
			
			def rank = 1
			similarShows.each{similarShow, pathCount ->
				res.get(similarShow).add(rank)
				rank++
			}
		}.iterate()
		res.keySet().removeAll(watchedShows)
		res = sortRankListsMap(res)
		res = res.keySet() as List;
		return res
	}
	
	def abstract sortRankListsMap(def rankListsMap)

}
