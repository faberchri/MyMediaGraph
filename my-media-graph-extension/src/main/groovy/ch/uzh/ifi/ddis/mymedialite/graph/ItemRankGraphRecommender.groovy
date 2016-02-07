package ch.uzh.ifi.ddis.mymedialite.graph

import com.tinkerpop.blueprints.Vertex
import java.util.concurrent.ConcurrentHashMap

abstract class ItemRankGraphRecommender extends GraphRecommender {

	def userRecommendationsMap = new ConcurrentHashMap();

	def numberOfReturnedPredictions = 0
	
	@Override
	public double predict(int userId, int itemId) {
		def duration = System.currentTimeMillis()
		// println "Calculate prediction for user $userId and item $itemId (number of returned Predictions: $numberOfReturnedPredictions)"
		def itemVertex = getVertexByItemId(itemId)
		if (itemVertex == null) {
			throw new IllegalArgumentException("Attempt to generate recommendations for unknown item. Item id: $itemId")
		}
		def rankList = getRankList(userId)
		def res = evaluateRankList(rankList, itemVertex)
		duration = (System.currentTimeMillis()  - duration) / 1000.0;
		numberOfReturnedPredictions++
		// println "Prediction calculation for user $userId and item $itemId completed in $duration seconds. Prediction: $res (number of returned Predictions: $numberOfReturnedPredictions)"
		return res
	}

	List getRankList(int userId){
		def rankList = userRecommendationsMap.get(userId)
		if (rankList == null) {
			def userVertex = getVertexByUserId(userId)
			if (userVertex == null) {
				throw new IllegalArgumentException("Attempt to generate recommendations for unknown user. User id: $userId")
			}
			rankList = runRankQuery(userVertex)
			userRecommendationsMap.put(userId, rankList)
			// println "Current ranked list map size: ${userRecommendationsMap.size()} (Thread: ${Thread.currentThread()})"
		}
		return rankList
	}

	double evaluateRankList(def rankList, def itemId){
		// rankList may contain only a subset of all items
		def itemIndex = rankList.indexOf(itemId)
		if (itemIndex != -1){
			// rankList is sorted by best recommendation first
			// but we want to return the highest score for the item at index 0
			return rankList.size() - itemIndex
		}
		// if item not in rankList we give lowest score
		return 0.0
	}


	protected abstract List<Vertex> runRankQuery(def userVertex);
}
