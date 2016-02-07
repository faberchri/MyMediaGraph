package ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.AbstractP

abstract class PGlobalNormalized extends AbstractP {

	def popularityRank

	def leverage =  0.8 //0.86528

	public PGlobalNormalized(def power, def leverage) {
		super(power, BaseCooper.NULL_DEFAULT_ALPHA)
		this.leverage = leverage
	}

	@Override
	public void train() {
		super.train()
		initPopRanks(leverage)
	}

	@Override
	public String toString() {
		return super.toString() + " leverage=$leverage"
	}

	@Override
	public Object normalize(Object m) {
		def nM = [:]
		m.each{k, v ->
			nM[k] = v * popularityRank[k]
		}
		return nM
	}

	abstract protected def initPopRanks(def leverage)
}
