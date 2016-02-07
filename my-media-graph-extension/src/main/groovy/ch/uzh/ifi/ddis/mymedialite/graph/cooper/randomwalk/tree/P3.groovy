package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

import com.tinkerpop.pipes.util.structures.Tree


/**
 * 
 * Creates the data structure used for and performs 
 * the random walk of the P^3 algorithm proposed in
 * Cooper, C., Lee, S. H., Radzik, T., & Siantos, Y. (2014). 
 * Random Walks in Recommender Systems: Exact Computation 
 * and Simulations. In Proceedings of the Companion 
 * Publication of the 23rd International Conference 
 * on World Wide Web Companion (pp. 811–816). Seoul, Korea.
 *
 */
class P3 extends TreeBasedP {

	public P3() {
		super(BaseCooper.P3_DEFAULT_POWER, BaseCooper.NULL_DEFAULT_ALPHA)
	}

	public P3(def alpha) {
		super(BaseCooper.P3_DEFAULT_POWER, alpha)
	}

	def createSearchTree(def userVertex){
		def tree = new Tree()
		userVertex
				.both(userItemFeedbackEdgeLabel)
				.both(userItemFeedbackEdgeLabel)
				.both(userItemFeedbackEdgeLabel)
				.tree(tree).cap().iterate()
		return tree
	}

	//	@Override
	//	protected List<Vertex> runRankQuery(Object userVertex) {
	//		def randomWalkCount = 0;
	//		def m = [:].withDefault {0}
	//		def randomWalkMatrix = buildRandomWalkDatastructure(userVertex)
	//		def rand = new java.util.Random()
	//		while(true){
	//			if (abortRandomWalk(randomWalkCount)){
	//				break
	//			}
	//
	//			userVertex
	//				.out(userItemFeedbackEdgeLabel) 		// we fetch all liked items
	//				.gather()								// breath first
	//				.transform{it[rand.nextInt(it.size())]}	// randomly select one liked item of given user
	//				.in(userItemFeedbackEdgeLabel)			// fetch all users who liked same item
	//				.gather()								// breath first
	//				.transform{it[rand.nextInt(it.size())]}	// randomly select one user
	//				.out(userItemFeedbackEdgeLabel)			// fetch all items
	//				.gather()								// breath first
	//				.sideEffect{
	//					def target = it[rand.nextInt(it.size())]	// randomly select one liked item
	//					m[target] = m[target] + 1			// increase count
	//				}.iterate()
	//
	//			if (balancedStateReached()){
	//				break
	//			}
	//
	//			randomWalkCount++;
	//		}
	//		m = m.sort {a, b -> b.value <=> a.value}
	//		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
	//		return m.keySet() as List;
	//	}

}
