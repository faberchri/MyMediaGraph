package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.PAlphaHelper


/**
 *
 * Implements the random walk for the P^3_alpha algorithm
 * proposed in
 * Cooper, C., Lee, S. H., Radzik, T., & Siantos, Y. (2014).
 * Random Walks in Recommender Systems: Exact Computation
 * and Simulations. In Proceedings of the Companion
 * Publication of the 23rd International Conference
 * on World Wide Web Companion (pp. 811–816). Seoul, Korea.
 * 
 * According to the paper alpha = 1.9 shows best performance
 * improvements on the MovieLens dataset. 
 *
 */
class P3Alpha extends P3 {

	def alphaHelper = new PAlphaHelper(this)

	public P3Alpha() {
		super(BaseCooper.EFFECTIVE_DEFAULT_ALPHA)
	}

	def performRandomWalk(def randomWalkMatrix, def rand){

		def dimensions = randomWalkMatrix.size()
		def abortionProb = alphaHelper.getAbortionProbability(dimensions)
		if (rand.nextFloat() < abortionProb) {
			return null
		}
		def i1 = rand.nextInt(dimensions)

		dimensions = randomWalkMatrix[i1].size()
		abortionProb = alphaHelper.getAbortionProbability(dimensions)
		if (rand.nextFloat() < abortionProb) {
			return null
		}
		def i2 = rand.nextInt(dimensions)

		dimensions = randomWalkMatrix[i1][i2].size()
		abortionProb = alphaHelper.getAbortionProbability(dimensions)
		if (rand.nextFloat() < abortionProb) {
			return null
		}
		def i3 = rand.nextInt(dimensions)
		return randomWalkMatrix[i1][i2][i3]
	}
}
