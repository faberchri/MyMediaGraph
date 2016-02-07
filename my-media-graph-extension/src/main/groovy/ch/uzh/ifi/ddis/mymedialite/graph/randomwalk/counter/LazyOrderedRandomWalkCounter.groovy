package ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter

import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.RandomWalkCount


class LazyOrderedRandomWalkCounter extends RandomWalkCounter {

	def vertexToWalkCountMap = [:]

	def currentOrderedCollection

	def updateCount(def vertex){
		return updateCount(vertex, 1)
	}

	def updateCount(def vertex, double increase){
		if (vertex == null){
			nullWalksCount++
			return false
		}
		effectiveWalksCount++

		// invalidate the pointer to a copy of an old ordering list
		currentOrderedCollection = null
		def rw = vertexToWalkCountMap.get(vertex)
		if (rw == null){
			rw = new RandomWalkCount(vertex)
			vertexToWalkCountMap.put(vertex, rw)
		}
		rw.update(increase)
		return true
	}

	def getRankedRandomWalks(){
		return copyRankedRandomWalks()
	}

	def copyRankedRandomWalks(){
		if (currentOrderedCollection != null){
			return currentOrderedCollection
		}
		def li = new ArrayList(vertexToWalkCountMap.values())
		currentOrderedCollection = li.sort()
		return currentOrderedCollection
	}

	LinkedHashMap getCopyOfVertxCountsInOrderedMap(){
		def res = new LinkedHashMap()
		for (rw in getRankedRandomWalks()){
			res.put(rw.getVertex(), rw.getCount())
		}
		return res
	}

	def size(){
		return vertexToWalkCountMap.size()
	}

	@Override
	public String toString() {
		return "${this.getRankedRandomWalks()}"
		//		return "${this.getClass().getSimpleName()}: ${this.getRankedRandomWalks()}"
	}
}
