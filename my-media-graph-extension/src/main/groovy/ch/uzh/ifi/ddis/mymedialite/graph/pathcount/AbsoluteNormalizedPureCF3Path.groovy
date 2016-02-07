package ch.uzh.ifi.ddis.mymedialite.graph.pathcount

import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.gremlin.Tokens.T

class AbsoluteNormalizedPureCF3Path
extends ItemRankGraphRecommender {

	Map itemWeightCount = new ConcurrentHashMap()

	def leverage = 0.5

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		// find all path from current user to new shows in one other user distance,
		// count number of paths to new show,
		// multiply each count by item popularity dependent weight
		// , order descending by counts
		def showsWatchedByCurrentUser = []
		return userVertex.out(userItemFeedbackEdgeLabel)
		.aggregate(showsWatchedByCurrentUser)
		.in(userItemFeedbackEdgeLabel)
		.out(userItemFeedbackEdgeLabel)
		.except(showsWatchedByCurrentUser)
		.groupCount.cap.transform{
			def m=[:]
			it.each{k,v ->
				m.put(k, v * getItemWeight(k))
			}
			return m
		}
		.orderMap(T.decr).toList()
	}

	def getItemWeight(def item){
		def w = itemWeightCount.get(item)
		if (w == null){
			w = calcItemWeight(item)
			itemWeightCount.put(item, w)
		}
		return w
	}

	def calcItemWeight(def item){
		def pop = item.in(userItemFeedbackEdgeLabel).count()
		return 1.0d / Math.pow(pop, leverage)
	}

	@Override
	public String toString() {
		return super.toString() + " leverage=" + leverage
	}

}
