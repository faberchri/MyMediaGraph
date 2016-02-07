package ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

class P3PRN extends PPopularityRankNormalized{

	def static final defaultLeverage = 0.8

	public P3PRN() {
		super(BaseCooper.P3_DEFAULT_POWER, defaultLeverage)
	}
}
