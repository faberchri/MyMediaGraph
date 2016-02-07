package ch.uzh.ifi.ddis.mymedialite.graph.fouss

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender

import com.google.common.collect.Lists
import com.tinkerpop.blueprints.Direction
import com.tinkerpop.blueprints.Vertex

class Commute extends ItemRankGraphRecommender {

	private final Map<Vertex,List<Vertex>> neighboursCache = new ConcurrentHashMap<Vertex, List<Vertex>>()

	static final ITERATIONS = 2

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		Random rand = ThreadLocalRandom.current()
		List paths = []
		for (i in 0..<ITERATIONS){
			def tmp = getAllTargetsAsPath(userVertex)
			walkPaths(userVertex, tmp, rand)
			paths.addAll(tmp)
		}
		def pathLenths = [:].withDefault { 0 }
		def targetCounts = [:].withDefault { 0 }
		collectPathCounts(paths, pathLenths , targetCounts)
		def r = normalizePathCounts(pathLenths, targetCounts)
		return sortAndShortenResultList(r, userVertex)
	}

	List getAllTargetsAsPath(Vertex start){
		def li = []
		def items = graph.V(vertexType, itemVertexType).toList()
		items.each { target ->
			li.add(getPath(target, start))
		}
		return li
	}

	def getPath(Vertex target, Vertex start){
		return new ReturnPath(target, start)
	}

	@CompileStatic
	@TypeChecked
	void walkPaths(Vertex start, List<OneWayPath> targets, Random rand){
		for(OneWayPath target in targets){
			walk(start, target, rand)
		}
	}

	@CompileStatic
	@TypeChecked
	void walk(Vertex start, OneWayPath target, Random rand){
		int c = 0
		while(true){
			c++
			start = hop(start, rand)
			if (target.targetReached(start)){
				//println "break: $c"
				break
			}
		}
	}

	@CompileStatic
	@TypeChecked
	Vertex hop(Vertex start, Random rand){
		List<Vertex> all = neighboursCache.get(start)
		if (all == (Object) null){
			all = Lists.newArrayList(start.getVertices(Direction.BOTH, userItemFeedbackEdgeLabel))
			neighboursCache.put(start, all)
		}
		int randI = rand.nextInt(all.size().intValue())
		return all.get(randI)
	}

	void collectPathCounts(List targets, Map pathLenths, Map targetCounts){
		targets.each{ target ->
			target.collectPaths(pathLenths, targetCounts)
		}
	}

	Map normalizePathCounts(Map pathLengths, Map targetCounts){
		def r = [:]
		pathLengths.each { vertex, lengthSum ->
			r[vertex] = lengthSum / targetCounts[vertex]
		}
		return r
	}

	List sortAndShortenResultList(Map m, def userVertex){
		m = m.sort { a, b ->
			a.value <=> b.value
		}
		println "final sorted counts map ${m[0..3]}"
		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		return m.keySet() as List
	}
}
