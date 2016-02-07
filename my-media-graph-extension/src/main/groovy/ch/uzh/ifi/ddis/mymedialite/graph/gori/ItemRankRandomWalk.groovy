package ch.uzh.ifi.ddis.mymedialite.graph.gori

import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.convergence.TopAbsoluteConstantOrderConvergenceTester
import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter.LazyOrderedRandomWalkCounter
import ch.uzh.ifi.ddis.mymedialite.graph.util.ProbabilityTree

import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Vertex


class ItemRankRandomWalk extends AbstractItemRank{

	static final int RANDOMWALK_ITERATIONS = 1000000

	static final int CONVERGENCE_INTERVAL_TEST = 20000

	static final int numberOfTopItemsToConsiderForConvergenceTest = 5

	static final def itemItemCorrelationEdgeLabel = "coview"
	static final def itemItemCorrelationWeight = "correlationWeight"

	def correlationOutEdgesCash = new ConcurrentHashMap()

	Random rand = new Random()

	@Override
	public void train() {
		// set up the usual user-item-feedback graph
		super.train()

		// introduce item-tem-coview edges (correlation graph)
		List items = graph.V(vertexType, itemVertexType).toList()
		double[][] ncm = getNormalizedCorrelationMatrix(items)
		addCorrelationEdges(graph,items,ncm)
	}

	void addCorrelationEdges(def graph, def items, double[][] normalizedCorrelationMatrix){
		int dimension = items.size()
		// def batchGraph = BatchGraph.wrap(graph)
		int count = 0
		for(int i = 0; i < dimension; i++){
			for(int j = 0; j < dimension; j++){
				double w = normalizedCorrelationMatrix[i][j]
				if (w > 0.0){
					def e = graph.addEdge(null, items[j], items[i], itemItemCorrelationEdgeLabel)
					e.setProperty(itemItemCorrelationWeight, w)
					count++
				}
			}
		}
		println "Number of added correlation edges: $count"
		// batchGraph.stopTransaction(TransactionalGraph.Conclusion.SUCCESS)
		println "Additional training phase completed."
	}

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {

		ProbabilityTree<Vertex> origins = initProbabilityTree(userVertex)
		def ranks = rank(origins)
		return sortAndShortenResultList(ranks, userVertex)
	}

	ProbabilityTree<Vertex> initProbabilityTree(Vertex userVertex){
		def fbs = userVertex.out(userItemFeedbackEdgeLabel).toList()
		def m = [:]
		fbs.each{ fb ->
			m[fb] = 1.0d / fbs.size().doubleValue()
		}
		return new ProbabilityTree<Vertex>(m)
	}

	Map rank(ProbabilityTree<Vertex> origins){
		int restarts = 0
		def randomWalkCounter = new LazyOrderedRandomWalkCounter()
		def convergenceTester = new TopAbsoluteConstantOrderConvergenceTester(
				CONVERGENCE_INTERVAL_TEST,
				numberOfTopItemsToConsiderForConvergenceTest)
		Vertex current = restart(origins)
		randomWalkCounter.updateCount(current)
		while(true){
			if (RANDOMWALK_ITERATIONS < randomWalkCounter.effectiveWalksCount){
				println "Optimization was aborted after ${randomWalkCounter.effectiveWalksCount} steps (restarts $restarts)"
				break
			}
			double p = rand.nextDouble()
			if (p < alpha){
				current = hopToAdjacentVertexBiasedByWeight(current)
			} else {
				current = restart(origins)
				restarts++
			}
			randomWalkCounter.updateCount(current)
			if (convergenceTester.converged(randomWalkCounter)){
				println "Walk converged after after ${randomWalkCounter.effectiveWalksCount} steps (restarts $restarts)"
				break
			}
		}
		return normalize(randomWalkCounter.getCopyOfVertxCountsInOrderedMap())
	}

	Vertex restart(ProbabilityTree<Vertex> origins){
		Vertex restart
		while(restart == null){
			// I guess that rounding errors can lead to ranges
			// in origin that do not spread the entire range
			// from 0..<1, hence we could get null from origins
			restart = origins.get(rand.nextDouble())
		}
		return restart
	}

	Vertex hopToAdjacentVertexBiasedByWeight(Vertex start){
		def allE = correlationOutEdgesCash[start]
		if (allE == null){
			def eL = start.outE(itemItemCorrelationEdgeLabel).toList()
			allE = new ProbabilityTree<Edge>()
			def m = [:]
			for (i in eL){
				allE.add(i, i.getProperty(itemItemCorrelationWeight))
			}
			correlationOutEdgesCash[start] = allE
		}
		Edge selectedE
		while (selectedE == null){
			selectedE = allE.get(rand.nextDouble())
		}
		return selectedE.inV().next()
	}

	Map normalize(Map counts){
		def sum = 0
		counts.each{k,v ->
			sum += v
		}
		counts.each{
			it.value = it.value / sum
		}
		counts = counts.sort { a, b ->
			a.key.id <=> b.key.id
		}
		return counts
	}
}
