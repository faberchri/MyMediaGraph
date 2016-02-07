package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

import java.util.List;

import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.ItemBasedPureCF3Path;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.Tokens.T

abstract class UserDependingShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender extends ItemBasedPureCF3Path{

	def itemPopularities = [:]
	def itemPopularityRanks = null
	def maxItemPopularity = null
		
	@Override
	protected List<Vertex> runRankQuery(def userVertex) {
	
		def popularityAffinity = getPopularityAffinity(userVertex)
		
		// multiple views of same show cause duplication of counts of similar shows (no dedup)
		def res = [:].withDefault{0}
		userVertex.out(userItemFeedbackEdgeLabel).sideEffect{watchedShow ->
			def similarShows = getCachedSimilarShows(watchedShow);
			similarShows.each{k, v ->				
				def popularity = itemPopularities.get(k)
				def tmp = ( v / maxItemPopularity * popularityAffinity ) + ( v / popularity * (1 - popularityAffinity) )
				res.put(k, res.get(k) + tmp)
			}
		}.iterate()
		res = res.sort {a, b -> b.value <=> a.value}
		res = res.keySet() as List;
		return res
	}
	
	abstract def getPopularityAffinity(def userVertex);
			
	synchronized def initItemPopularities() {
		if (itemPopularityRanks == null) {
			def tmp = graph.V(vertexType, userVertexType).out(userItemFeedbackEdgeLabel).groupCount(itemPopularities).cap.orderMap(T.incr).toList()
			maxItemPopularity = tmp.last().in(userItemFeedbackEdgeLabel).count()
			itemPopularityRanks = [:]
			tmp.eachWithIndex{k, i ->
				itemPopularityRanks.put(k, (i + 1)) // least popular item has rank 1 not 0
			}
		}
	}

}
