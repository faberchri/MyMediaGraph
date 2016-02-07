package ch.uzh.ifi.ddis.mymedialite.graph.fouss

import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender

import com.tinkerpop.blueprints.Vertex

class OneWayIntermediateCounts extends ItemRankGraphRecommender {

	def rand = new Random()

	def neighboursCache = new ConcurrentHashMap()

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		def m = getAverageWalkCounts(userVertex, 2)
		m = m.sort { a, b ->
			a.value <=> b.value
		}
		//println "final sorted counts map $m"
		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		return m.keySet() as List
	}

	def getAverageWalkCounts(def start, def iterationsPerTarget){
		def items = graph.V(vertexType, itemVertexType).toList()
		def counts = [:]
		def iterations = [:]
		items.each{ item ->
			counts[item] = 0
			iterations[item] = 0
		}
		def c = 1
		while(c <= iterationsPerTarget){
			walkToAllTargets(start, counts, iterations)
			//println "iterations $c"
			c++
		}
		counts.each { target, count ->
			counts[target] = count / iterations[target]
		}
		return counts
	}

	def walkToAllTargets(def start, def counts, def iterations){
		counts.each{ target, prevCount ->
			walk(start, target, counts, iterations)
		}
	}

	def walk(def start, def target, def counts, def iterations){
		def c = 0
		def currentHits = [] as Set
		while(true){
			if (counts.containsKey(start)){
				if (!currentHits.contains(start)){
					counts[start] = counts[start] + c
					iterations[start] = iterations[start] + 1
					currentHits.add(start)
				}
				if (targetReached(start, target)){
					//	println "target reached after $c hops"
					break
				}
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
