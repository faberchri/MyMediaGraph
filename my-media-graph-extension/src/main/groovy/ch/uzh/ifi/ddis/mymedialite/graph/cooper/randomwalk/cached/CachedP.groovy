package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached

import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.AbstractP

abstract class CachedP extends AbstractP {

	//def cache = [:]

	def cache = new ConcurrentHashMap()

	//	def cache = CacheBuilder.newBuilder()
	//	.build(
	//	new CacheLoader(){
	//		@Override
	//		public Object load(Object vertex) {
	//			return vertex.both(userItemFeedbackEdgeLabel).toList()
	//		}
	//	})

	public CachedP(def power, def alpha) {
		super(power, alpha)
	}

	@Override
	public Object buildRandomWalkDatastructure(Object userVertex) {
		return userVertex
	}

	def walk(def vertex, def rand, def length){
		def c = 0
		while (c < length){
			vertex = hop(vertex, rand)
			if (vertex == null){
				return null
			}
			c++
		}
		return vertex
	}

	def hop(def vertex, def rand){
		def list = getAdjacentVertices(vertex)
		return list[rand.nextInt(list.size())]
	}

	def getAdjacentVertices(def vertex){
		def li = cache.get(vertex)
		if (li == null){
			li = creatAdjacentVerticesList(vertex)
			cache[vertex] = li
		}
		return li
	}

	def getDegree(def vertex){
		return getAdjacentVertices(vertex).size()
	}

	def creatAdjacentVerticesList(def vertex){
		return vertex.both(userItemFeedbackEdgeLabel).toList()
	}

	@Override
	public Object performRandomWalk(Object vertex, Object rand) {
		return walk(vertex, rand, getPower())
	}

}
