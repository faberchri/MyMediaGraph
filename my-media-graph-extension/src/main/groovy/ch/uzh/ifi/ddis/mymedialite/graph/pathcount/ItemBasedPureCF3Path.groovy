package ch.uzh.ifi.ddis.mymedialite.graph.pathcount

import java.util.List

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender;

import com.tinkerpop.blueprints.Vertex;

class ItemBasedPureCF3Path extends
		ItemRankGraphRecommender {

	def similarShowsCache = [:]
	
	// returned list contains already watched shows!
	@Override
	protected List<Vertex> runRankQuery(def userVertex) {
	
		// multiple views of same show cause duplication of counts of similar shows (no dedup)
		def res = [:].withDefault{0}
		userVertex.out(userItemFeedbackEdgeLabel).sideEffect{watchedShow ->
			def similarShows = getCachedSimilarShows(watchedShow);
			similarShows.each{k, v ->
				res.put(k, res.get(k) + v)
			}		
		}.iterate()
		res = res.sort {a, b -> b.value <=> a.value}
		res = res.keySet() as List;
		return res
	}
		
	def getCachedSimilarShows(def show){
		def similarShows = similarShowsCache.get(show)
		if (similarShows == null) {
			similarShows = getSimilarShows(show)
			similarShowsCache.put(show, similarShows)
		}
		return similarShows
	}
	
	def getSimilarShows(def show){
		def similarShows = [:]
		show.in(userItemFeedbackEdgeLabel).out(userItemFeedbackEdgeLabel).except([show]).groupCount(similarShows).iterate()
		similarShows = similarShows.sort {a, b -> b.value <=> a.value}
		return similarShows
	}
}
