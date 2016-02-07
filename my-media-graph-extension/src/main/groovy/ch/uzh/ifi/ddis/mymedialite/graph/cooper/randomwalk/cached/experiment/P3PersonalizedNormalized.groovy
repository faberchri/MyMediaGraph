package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P3;

abstract class P3PersonalizedNormalized extends P3 {

	def popularityRank = [:]

	@Override
	public void train() {
		super.train()
		initPopRanks()
	}

	@Override
	public Object performRandomWalk(Object randomWalkCounter, Object start, Object rand) {
		def target = walk(start, rand, 3)
		def v = popularityRank[start][target]

		randomWalkCounter.updateCount(target, v)
	}

	abstract protected def initPopRanks()
}
