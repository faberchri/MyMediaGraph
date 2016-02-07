package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.PAlphaHelper
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.CachedP

class P3Alpha extends CachedP {

	def alphaHelper = new PAlphaHelper(this)

	public P3Alpha() {
		super(BaseCooper.P3_DEFAULT_POWER, BaseCooper.EFFECTIVE_DEFAULT_ALPHA)
	}

	def abort(def dimensions, def rand){
		def abortionProb = alphaHelper.getAbortionProbability(dimensions)
		return rand.nextFloat() < abortionProb
	}

	@Override
	public Object hop(Object vertex, Object rand) {
		def list = getAdjacentVertices(vertex)
		def dimension = list.size()
		if (abort(dimension, rand)){
			return null
		}
		return list[rand.nextInt(dimension)]
	}
}
