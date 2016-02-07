package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.PPopularityRankNormalized
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.LeverageEstimator.EstimatorAdapter


class P3EstPopularityRankNormalized extends PPopularityRankNormalized {

	def estA = new EstimatorAdapter()

	public P3EstPopularityRankNormalized() {
		super(BaseCooper.P3_DEFAULT_POWER, 0.8)
	}

	@Override
	public void train() {
		super.train()
		estA.train(graph, this)
		super.train()
	}

	@Override
	public String toString() {
		return super.toString() + " " + estA.toString()
	}
}
