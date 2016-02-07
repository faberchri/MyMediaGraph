package ch.uzh.ifi.ddis.mymedialite.graph.huang

import ch.uzh.ifi.ddis.mymedialite.graph.util.UpdateableTreeSet


class ReactivatingBranchAndBound extends BranchAndBound {

	@Override
	public void iterate(UpdateableTreeSet prio, Map vertexToPriorityMap) {
		def front = prio.pollFirst() // has highest level of activation
		def aLF = front.activationLevel
		def aL = aLF * EDGE_WEIGHT

		def neighbours = getAllNeighbours(front.vertex)
		for (n in neighbours){
			def nP = vertexToPriorityMap[n]
			if (nP == null) {
				nP = new VertexPriority(n, 0.0)
				vertexToPriorityMap[n] = nP
			}
			if (!prio.update(nP, aL)){
				nP.update(aL)
				prio.add(nP)
			}
		}
	}
}
