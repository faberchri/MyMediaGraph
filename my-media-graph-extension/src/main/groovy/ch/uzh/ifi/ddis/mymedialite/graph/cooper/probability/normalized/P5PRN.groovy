package ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

class P5PRN extends PPopularityRankNormalized {

	def static final defaultLeverage = 0.8

	public P5PRN() {
		super(BaseCooper.P5_DEFAULT_POWER, defaultLeverage)
	}
}
