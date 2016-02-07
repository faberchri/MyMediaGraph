package ch.uzh.ifi.ddis.mymedialite.graph.fouss.limited

import ch.uzh.ifi.ddis.mymedialite.graph.fouss.Commute

import com.tinkerpop.blueprints.Vertex

class OneWay extends Commute {

	int approxGraphDiameter

	@Override
	public void train() {
		super.train()
		this.approxGraphDiameter = estimateGraphDiameter(graph)
	}

	int estimateGraphDiameter(def graph){

		int diameter = 1
		def allUsers = graph.V(vertexType, userVertexType).toList()
		if (allUsers.isEmpty()){
			return diameter
		}
		int maxDiameter = 50
		for (i in 0..<100) {
			// select randomly two user nodes
			def root = allUsers[rand.nextInt(allUsers.size())]
			def target = allUsers[rand.nextInt(allUsers.size())]

			// calculate shortest path between the two vertex
			def s = [root] as Set
			def shortestP = root.both().except(s).store(s).loop(3){it.object != target && it.loops <= maxDiameter}.path().toList()
			println "shortestP: $shortestP"
			if (shortestP.isEmpty()){
				continue
			}
			def shortestPSize = shortestP[0].size()
			println "shortestPSize: $shortestPSize"
			if (shortestPSize > diameter) {
				diameter = shortestPSize
			}
			if (diameter >= maxDiameter){
				break
			}
		}
		println "diameter: $diameter"
		return diameter
	}

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		def pathLenths = [:].withDefault { 0 }
		def targetCounts = [:].withDefault { 0 }

		for(i in 0..<ITERATIONS){
			walk(userVertex, pathLenths, targetCounts)
		}

		pathLenths = normalizePathCounts(pathLenths, targetCounts)
		return sortAndShortenResultList(pathLenths, userVertex)
	}

	void walk(def start, def counts, def norm){
		def met = [] as Set
		def numHops = approxGraphDiameter + 2
		for(i in 1..numHops){
			start = hop(start)
			if (i % 2 == 1 && !met.contains(start)){
				counts[start] = counts[start] + i
				norm[start] = norm[start] + 1
			}
			met.add(start)
		}
	}

	void walkNoCycles(def start, def counts, def norm){
		def met = [] as Set
		def numHops = approxGraphDiameter + 2
		for(i in 1..numHops){
			start = hop(start)
			if (met.contains(start)){
				//println i
				break
			}
			met.add(start)
			if (i % 2 == 1){
				counts[start] = counts[start] + i
				norm[start] = norm[start] + 1
			}
		}
	}

	void walkNoCycles2(def start, def counts, def norm){
		def met = [] as Set
		def numHops = approxGraphDiameter + 2
		for(i in 1..numHops){
			def noInfLoop = 0
			while (true){
				def tmp = hop(start)
				if (!met.contains(tmp)){
					start = tmp
					met.add(start)
					break
				}
				noInfLoop++
				if (noInfLoop >= 50){
					break
				}
			}
			if (i % 2 == 1){
				counts[start] = counts[start] + i
				norm[start] = norm[start] + 1
			}
		}
	}

}
