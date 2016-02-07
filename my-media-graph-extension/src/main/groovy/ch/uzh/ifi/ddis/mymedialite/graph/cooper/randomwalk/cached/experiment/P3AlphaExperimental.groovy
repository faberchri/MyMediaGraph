package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter.RandomWalkCounter

class P3AlphaExperimental extends P3Alpha {

	def nulls = [:].withDefault {0}

	def currentC = 0

	@Override
	public Object performRandomWalk(Object vertex, Object rand) {
		def r = walk(vertex, rand, 3)
		nulls[r] = nulls[r] + currentC
		currentC = 0
		return r
	}

	@Override
	public Object hop(Object vertex, Object rand) {
		def list = getAdjacentVertices(vertex)
		def dimension = list.size()
		while (abort(dimension, rand)){
			currentC++
		}
		return list[rand.nextInt(dimension)]
	}

	@Override
	public Object normalizeRandomWalkCounts(RandomWalkCounter randomWalkCounter) {
		def m = randomWalkCounter.getCopyOfVertxCountsInOrderedMap()
		//println m
		//println nulls
		def r = [:]
		m.each{ k,v ->
			def nullCount = nulls[k]
			if (nullCount != 0){
				r[k] = v / nullCount
				//println "${r[k]} = $v / $nullCount"
			}
		}
		//println r
		//println "---"
		return r
	}
}
