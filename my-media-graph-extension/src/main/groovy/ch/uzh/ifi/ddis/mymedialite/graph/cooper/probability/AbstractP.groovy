package ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability

import groovy.transform.CompileStatic
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2DoubleMap

import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper

import com.tinkerpop.blueprints.Vertex

class AbstractP extends BaseCooper {

	private final Map<Vertex,List<Vertex>> cache = new ConcurrentHashMap()

	// we need primitive parameters here for static compilation
	private int localPower
	private double localAlpha

	public AbstractP(def power, def alpha) {
		super(power, alpha)
	}

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {

		def m = calculateRanksMap(userVertex, null, null)
		m = m.sort { a, b ->
			b.value <=> a.value
		}
		// println "Top 10 of final sorted ranks map for $userVertex: ${m[0..<10]}"
		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		return m.keySet() as List
	}

	def calculateRanksMap(def userVertex, def convergenceTester, def maxWalks) {
		localPower = getPower()
		localAlpha = getAlpha()

		def m = new Object2DoubleLinkedOpenHashMap<Vertex>()
		m.defaultReturnValue(0)
		recursion(userVertex, m, 0, 1.0)
		return normalize(m)
	}

	def normalize(def m){
		// normalize, actually only needed to pass test case
		def valueSum = m.values().sum()
		def nM = [:]
		m.each{k, v ->
			nM[k] = v / valueSum
		}
		return nM
	}

	@CompileStatic
	private List<Vertex> getAdjacentVertices(Vertex vertex){
		List<Vertex> li = cache.get(vertex)
		if (li == null){
			li = creatAdjacentVerticesList(vertex)
			cache.put(vertex,li)
		}
		return li
	}

	List<Vertex> creatAdjacentVerticesList(def vertex){
		return vertex.both(userItemFeedbackEdgeLabel).toList()
	}

	@CompileStatic
	private void recursion(Vertex current, Object2DoubleMap<Vertex> probabilities, int depth, double probability){
		if (depth < localPower){
			List<Vertex> adjacents = getAdjacentVertices(current)
			probability = probability / (double) adjacents.size()
			for(Vertex a : adjacents){
				recursion(a, probabilities, depth + 1, probability)
			}
		} else {
			probability = Math.pow(probability, localAlpha)
			probabilities.put(current, probabilities.getDouble(current) + probability)
		}
	}
}
