package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P3;


class LowDegreeP3 extends P3 {

	@Override
	public Object performRandomWalk(Object randomWalkCounter, Object start, Object rand) {

		def currentWeight = 1.0
		//		def p = [] as Set
		for (i in 1..3){
			start = hop(start, rand)
			//			if (p.contains(start)){
			//				currentWeight = currentWeight + 1.0
			//			}
			//			p.add(start)

			def v = 0.0
			if (i == 1 || i == 3){
				v = 1000.0 / (double)getDegree(start)
			}
			//println v
			currentWeight = currentWeight + v
			//println currentWeight
		}
		randomWalkCounter.updateCount(start, currentWeight)
	}

	//	@Override
	//	public Object normalizeRandomWalkCounts(RandomWalkCounter randomWalkCounter) {
	//		// don't normalize
	//		return randomWalkCounter.getCopyOfVertxCountsInOrderedMap()
	//	}
}
