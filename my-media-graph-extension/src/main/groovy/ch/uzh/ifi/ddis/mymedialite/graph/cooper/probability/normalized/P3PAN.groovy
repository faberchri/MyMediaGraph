package ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

class P3PAN extends PPopularityAbsoluteNormalized {

	def static final defaultLeverage = 0.7

	public P3PAN() {
		super(BaseCooper.P3_DEFAULT_POWER, defaultLeverage)
	}
}
