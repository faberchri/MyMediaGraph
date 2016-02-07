package ch.uzh.ifi.ddis.mymedialite.graph.huang

import ch.uzh.ifi.ddis.mymedialite.graph.util.UpdateableTreeSet

class NeighbourhoodSampledBranchAndBound extends BranchAndBound {

	private final def sampleSize = 100

	protected void iterate(UpdateableTreeSet prio, Map vertexToPriorityMap){
		def front = prio.pollFirst() // has highest level of activation
		def aLF = front.activationLevel
		def aL = aLF * EDGE_WEIGHT

		def neighbours = getAllNeighbours(front.vertex)

		//		if (neighbours[0].getProperty(vertexType).equals(userVertexType)){
		def rand = new Random()
		def tmp = [] as Set
		while(tmp.size() < sampleSize && tmp.size() < neighbours.size()){
			tmp.add(neighbours[rand.nextInt(neighbours.size())])
		}
		neighbours = tmp

		//		}
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

}
