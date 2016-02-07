package ch.uzh.ifi.ddis.mymedialite.graph.huang

import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender
import ch.uzh.ifi.ddis.mymedialite.graph.util.UpdateableTreeSet

import com.tinkerpop.blueprints.Vertex

class BranchAndBound extends ItemRankGraphRecommender {

	def EDGE_WEIGHT = 0.5

	def ITERATIONS = 35

	def neighboursCache = new ConcurrentHashMap()

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		def prio = new UpdateableTreeSet<VertexPriority>()
		def vertexToPriorityMap = [:]

		def root = new VertexPriority(userVertex, 1.0)
		prio.add(root)
		vertexToPriorityMap[userVertex] = root

		for (i in 1..ITERATIONS){
			iterate(prio, vertexToPriorityMap)
		}

		return sortAndShortenResultList(vertexToPriorityMap, userVertex)
	}

	protected void iterate(UpdateableTreeSet prio, Map vertexToPriorityMap){
		def front = prio.pollFirst() // has highest level of activation
		def aLF = front.activationLevel
		def aL = aLF * EDGE_WEIGHT

		def neighbours = getAllNeighbours(front.vertex)
		for (n in neighbours){
			def nP = vertexToPriorityMap[n]
			if (nP == null) {
				nP = new VertexPriority(n, aL)
				vertexToPriorityMap[n] = nP
				prio.add(nP)
			} else {
				if (prio.contains(nP)){
					prio.update(nP, aL)
				} else {
					nP.update(aL)
				}
			}
		}
	}


	List getAllNeighbours(def vertex){
		def all = neighboursCache.get(vertex)
		if (all == null){
			all = vertex.both(userItemFeedbackEdgeLabel).toList()
			neighboursCache[vertex] = all
		}
		return all
	}

	List sortAndShortenResultList(Map m, def userVertex){
		m.keySet().removeAll(graph.V(vertexType, userVertexType).toList())
		m = m.sort { a, b ->
			a.value <=> b.value
		}
		println "final sorted counts map ${m[0..10]}"

		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		return m.keySet() as List
	}

    @Override
    public String toString() {
        return super.toString() + " iterations=$ITERATIONS edgeWeight=$EDGE_WEIGHT"
    }
}
