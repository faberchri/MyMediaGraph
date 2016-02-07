package ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter

import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.RandomWalkCount
import ch.uzh.ifi.ddis.mymedialite.graph.util.UpdateableTreeSet

class ConstantlyOrderedRandomWalkCounter extends RandomWalkCounter{

	def vertexToWalkCountMap = [:]

	// fully qualified class name prevents java.lang.VerifyError: Illegal use of nonvirtual function call
	def orderedWalks = new ch.uzh.ifi.ddis.mymedialite.graph.util.UpdateableTreeSet<RandomWalkCount>()

	def updateCount(def vertex){
		if (vertex == null){
			nullWalksCount++
			return false
		}
		effectiveWalksCount++
		def rw = vertexToWalkCountMap.get(vertex)
		if (rw == null){
			rw = new RandomWalkCount(vertex)
			orderedWalks.add(rw)
			vertexToWalkCountMap.put(vertex, rw)
		}
		orderedWalks.update(rw)
		return true
	}

	def getRankedRandomWalks(){
		return orderedWalks
	}

	def copyRankedRandomWalks(){
		return new ArrayList(orderedWalks)
	}

	LinkedHashMap getCopyOfVertxCountsInOrderedMap(){
		def res = new LinkedHashMap()
		for (rw in orderedWalks){
			res.put(rw.vertex, rw.count)
		}
		return res
	}

	def size(){
		return orderedWalks.size()
	}
}
