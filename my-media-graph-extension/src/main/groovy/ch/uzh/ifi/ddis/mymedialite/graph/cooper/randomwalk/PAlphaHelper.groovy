package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk

import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

class PAlphaHelper {

	def abortionProbCash = new ConcurrentHashMap()

	private final BaseCooper baseCooper

	public PAlphaHelper(BaseCooper baseCooper) {
		this.baseCooper = baseCooper
	}

	double calcAbortionProbability(int dimensions){
		return 1 - dimensions * Math.pow(1 / dimensions, baseCooper.getAlpha())
	}

	double getAbortionProbability(int dimensions){

		def res = abortionProbCash.get(dimensions)
		if (res == null){
			res = calcAbortionProbability(dimensions)
			abortionProbCash[dimensions] = res
		}
		return res
	}
}
