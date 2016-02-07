package ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

class P5PAN extends PPopularityAbsoluteNormalized {

	def static final defaultLeverage = 0.8

	public P5PAN() {
		super(BaseCooper.P5_DEFAULT_POWER, defaultLeverage)
	}
}
