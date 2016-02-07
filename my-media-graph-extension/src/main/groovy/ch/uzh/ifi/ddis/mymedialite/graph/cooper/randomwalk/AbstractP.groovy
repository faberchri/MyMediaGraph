package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper
import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.convergence.TopAbsoluteConstantOrderConvergenceTester
import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter.LazyOrderedRandomWalkCounter
import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter.RandomWalkCounter

import com.tinkerpop.blueprints.Vertex


/**
 * 
 * Base class for the random walk based recommendation
 * algorithms proposed in
 * Cooper, C., Lee, S. H., Radzik, T., & Siantos, Y. (2014). 
 * Random Walks in Recommender Systems: Exact Computation 
 * and Simulations. In Proceedings of the Companion 
 * Publication of the 23rd International Conference 
 * on World Wide Web Companion (pp. 811–816). Seoul, Korea.
 *
 */
abstract class AbstractP extends BaseCooper {

	def MAX_WALKS = 20000000

	def CONVERGENCE_INTERVAL_TEST = 20000

	def numberOfTopItemsToConsiderForConvergenceTest = 5

	def quotaOfTopItemsToConsiderForConvergenceTest = 0.1

	public AbstractP(def power, def alpha) {
		super(power, alpha)
	}

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		def m = calculateRanksMap(userVertex,
				new TopAbsoluteConstantOrderConvergenceTester(
				CONVERGENCE_INTERVAL_TEST,
				numberOfTopItemsToConsiderForConvergenceTest),
				MAX_WALKS)


		//		def m = calculateRanksMap(userVertex,
		//				new TopQuotaConstantOrderConvergenceTester(CONVERGENCE_INTERVAL_TEST, graph.V(vertexType, itemVertexType).count().intValue(), 0.01),
		//				MAX_WALKS)


		// def m = calculateRanksMap(userVertex, new NullConvergenceTester(), 1000)

		m = m.sort { a, b ->
			b.value <=> a.value
		}
		// println "Top 10 of final sorted ranks map for $userVertex: ${m[0..<10]}"
		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		return m.keySet() as List
	}

	def calculateRanksMap(def userVertex, def convergenceTester, def maxWalks) {
		def randomWalkCounter = new LazyOrderedRandomWalkCounter()
		def randomWalkDataStructure = buildRandomWalkDatastructure(userVertex)
		def rand = new java.util.Random()
		while(true){
			if (abortRandomWalk(randomWalkCounter, maxWalks)){
				println "Walk was aborted after ${randomWalkCounter.effectiveWalksCount +  randomWalkCounter.nullWalksCount} restarts (effective walks: $randomWalkCounter.effectiveWalksCount, aborted walks: $randomWalkCounter.nullWalksCount)"
				break
			}

			performRandomWalk(randomWalkCounter, randomWalkDataStructure, rand)

			if (convergenceTester.converged(randomWalkCounter)){
				// println "Walk converged after ${randomWalkCounter.effectiveWalksCount +  randomWalkCounter.nullWalksCount} restarts (effective walks: $randomWalkCounter.effectiveWalksCount, aborted walks: $randomWalkCounter.nullWalksCount)"
				break
			}
		}
		def m = normalizeRandomWalkCounts(randomWalkCounter)
		return m
	}

	def normalizeRandomWalkCounts(RandomWalkCounter randomWalkCounter){
		def norm = randomWalkCounter.effectiveWalksCount + randomWalkCounter.nullWalksCount
		def countsMap = randomWalkCounter.getCopyOfVertxCountsInOrderedMap()
		def r = [:]
		countsMap.each {k, v ->
			r[k] = v / norm
		}
		return r
	}

	def abortRandomWalk(RandomWalkCounter randomWalkCounter, def maxWalks){
		if (randomWalkCounter.effectiveWalksCount < maxWalks) {
			return false
		}
		return true
	}

	def abstract buildRandomWalkDatastructure(def userVertex)

	def performRandomWalk(def randomWalkCounter, def randomWalkDataStructure, def rand){
		def target = performRandomWalk(randomWalkDataStructure, rand)
		randomWalkCounter.updateCount(target)
	}

	def abstract performRandomWalk(def randomWalkDataStructure, def rand)

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
		builder.append("MAX_WALKS=")
		builder.append(MAX_WALKS)
		builder.append(" CONVERGENCE_INTERVAL_TEST=")
		builder.append(CONVERGENCE_INTERVAL_TEST)
		builder.append(" numberOfTopItemsToConsiderForConvergenceTest=")
		builder.append(numberOfTopItemsToConsiderForConvergenceTest)
		builder.append(" quotaOfTopItemsToConsiderForConvergenceTest=")
		builder.append(quotaOfTopItemsToConsiderForConvergenceTest)
		return super.toString() + " " + builder.toString()
	}
}
