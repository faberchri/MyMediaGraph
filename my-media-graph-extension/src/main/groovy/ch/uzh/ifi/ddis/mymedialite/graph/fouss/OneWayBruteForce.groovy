package ch.uzh.ifi.ddis.mymedialite.graph.fouss

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender

import com.tinkerpop.blueprints.Vertex

class OneWayBruteForce extends ItemRankGraphRecommender {

	def rand = new Random()

	def neighboursCache = [:]

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		def m = getAverageWalkCounts(userVertex, 5)
		m = m.sort { a, b ->
			a.value <=> b.value
		}
		println "final sorted counts map $m"
		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		return m.keySet() as List
	}

	def getAverageWalkCounts(def start, def iterationsPerTarget){
		def items = graph.V(vertexType, itemVertexType).toList()
		def counts = [:]
		items.each{item ->
			counts[item] = 0
		}
		def c = 1
		while(c <= iterationsPerTarget){
			walkToAllTargets(start, counts)
			println "iterations $c"
			c++
		}
		return counts
	}

	def walkToAllTargets(def start, def counts){
		counts.each{target, prevCount ->
			counts[target] = prevCount + walk(start, target)
		}
		// println "current counts map $counts"
		return counts
	}

	def walk(def start, def target){
		def c = 0
		while(true){
			if (targetReached(start, target)){
				//	println "target reached after $c hops"
				return c
			}
			start = hop(start)
			c++
		}
	}

	def targetReached(def current, def target){
		return current.equals(target)
	}

	def hop(def start){
		def all = neighboursCache.get(start)
		if (all == null){
			all = start.both(userItemFeedbackEdgeLabel).toList()
			neighboursCache[start] = all
		}
		return all[rand.nextInt(all.size())]
	}
}
