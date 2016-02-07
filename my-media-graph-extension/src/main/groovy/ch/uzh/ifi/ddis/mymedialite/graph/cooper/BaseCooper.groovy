package ch.uzh.ifi.ddis.mymedialite.graph.cooper

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender

import com.tinkerpop.blueprints.Vertex

abstract class BaseCooper extends ItemRankGraphRecommender {

	def static final P3_DEFAULT_POWER = 3

	def static final P5_DEFAULT_POWER = 5

	def static final NULL_DEFAULT_ALPHA  = 1

	def static final EFFECTIVE_DEFAULT_ALPHA  = 1.9

	def power

	def alpha

	public BaseCooper(def power, def alpha) {
		this.power = power
		this.alpha = alpha
	}

	def  protected getPower(){
		return power
	}

	def protected getAlpha(){
		return alpha
	}

	@Override
	public String toString() {
		return super.toString() + " power=$power alpha=$alpha"
	}
}
