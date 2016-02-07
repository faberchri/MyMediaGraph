package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

import com.tinkerpop.pipes.util.structures.Tree

/**
 *
 * Creates the data structure used for and performs
 * the random walk of the P^5 algorithm proposed in
 * Cooper, C., Lee, S. H., Radzik, T., & Siantos, Y. (2014).
 * Random Walks in Recommender Systems: Exact Computation
 * and Simulations. In Proceedings of the Companion
 * Publication of the 23rd International Conference
 * on World Wide Web Companion (pp. 811–816). Seoul, Korea.
 *
 */
class P5 extends TreeBasedP {

	public P5() {
		super(BaseCooper.P5_DEFAULT_POWER, BaseCooper.NULL_DEFAULT_ALPHA)
	}

	def createSearchTree(def userVertex){
		def tree = new Tree()
		userVertex
				.both(userItemFeedbackEdgeLabel)
				.both(userItemFeedbackEdgeLabel)
				.both(userItemFeedbackEdgeLabel)
				.both(userItemFeedbackEdgeLabel)
				.both(userItemFeedbackEdgeLabel)
				.tree(tree).cap().iterate()
		return tree
	}
}
