package ch.uzh.ifi.ddis.mymedialite.graph.gori

import ch.uzh.ifi.ddis.mymedialite.graph.util.ProbabilityTree

import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Vertex

class ItemRankRandomWalkExperimental extends ItemRankRandomWalk {

	Map rank(ProbabilityTree<Vertex> origins){
		int iterations = 0
		def counts = [:].withDefault {0.0d}
		def norm = [:].withDefault {0}
		Vertex current = restart(origins)
		counts[current] = counts[current] + 1
		norm[current] = norm[current] + 1
		while(iterations < RANDOMWALK_ITERATIONS){
			double p = rand.nextDouble()
			if (p < alpha){
				current = walk(current, counts, norm)
			} else {
				current = restart(origins)
				counts[current] = counts[current] + 1
				norm[current] = norm[current] + 1
			}
			iterations++
		}
		return super.normalize(counts)
	}

	Vertex walk(Vertex start, def counts, def norm){
		def allE = correlationOutEdgesCash[start]
		if (allE == null){
			allE = start.outE(itemItemCorrelationEdgeLabel).toList()
			correlationOutEdgesCash[start] = allE
		}
		Edge selectedE = allE[rand.nextInt(allE.size())]
		Vertex selectedV = selectedE.inV().next()
		double weight = selectedE.getProperty(itemItemCorrelationWeight)
		counts[selectedV] = counts[selectedV] + weight
		norm[selectedV] = norm[selectedV] + 1
		return selectedV
	}

	Map normalize(Map counts, Map norms){
		counts.each{
			it.value = it.value / norms[it.key]
		}
		counts = counts.sort { a, b ->
			a.key.id <=> b.key.id
		}
		return counts
	}
}
