package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

class MedianSimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender
		extends SimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender {

	@Override
	public Object sortRankListsMap(def rankListsMap) {
		rankListsMap.each{k,v ->
			v.sort()
		}
		return rankListsMap.sort {a, b -> 
			def aMedian = calcMedian(a.value)
			def bMedian = calcMedian(b.value)
			def res = aMedian <=> bMedian
			if (res == 0) {
				res = a.value.sum()/a.value.size() <=> b.value.sum()/b.value.size()				
			}
			return res
		}
	}
	
	def calcMedian(def array){
		def numberItems = array.size()
		def midNumber = (int)(numberItems/2)
		def median = numberItems %2 != 0 ? array[midNumber] : (array[midNumber] + array[midNumber-1])/2
		return median
	}
}
