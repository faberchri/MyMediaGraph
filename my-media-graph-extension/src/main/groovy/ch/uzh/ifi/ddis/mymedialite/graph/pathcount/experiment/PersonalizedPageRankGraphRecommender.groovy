package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

import java.util.List

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender;

import com.tinkerpop.blueprints.Vertex;

class PersonalizedPageRankGraphRecommender extends ItemRankGraphRecommender {

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		
		def outWeights = [:]
		// cache for all users the inverse of their outdegree 
		graph.V(vertexType, userVertexType).sideEffect{outWeights[it] = 1 / it.out(userItemFeedbackEdgeLabel).count() }.iterate()
		
		def m = [:].withDefault{0}
		def c = 0
		def rand = new java.util.Random()
		while (c < 2000) {
//			def max = rand.nextInt(4) + 1
			def max = 0.85
			userVertex._().as('x')
				.outE(userItemFeedbackEdgeLabel)
				.gather.transform{it[rand.nextInt(it.size())]}
				.sideEffect{
					// increase item count by the weight of the preceding user
					def outUser = it.outV.next(); def inItem = it.inV.next();
					m[inItem] = m[inItem] + outWeights[outUser]
				}
				.inV
				.in(userItemFeedbackEdgeLabel)
				.gather.transform{it[rand.nextInt(it.size())]}
				.loop('x'){def r = Math.random(); r < max}
				.iterate()
			c++
		}
		m = m.sort {a, b -> b.value <=> a.value}
		println "intermediate result $m"
		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		
		return m.keySet() as List;
		
//		def m = [:]
//		def c = 0
//		def rand = new java.util.Random()
//		while (c < 4000) {
//			def max = rand.nextInt(27) + 3
//			userVertex._().as('x')
//				//.sideEffect{println "startUser: $it"}
//				.out(userItemFeedbackEdgeLabel)
//				.gather.transform{it[rand.nextInt(it.size())]}
//				//.sideEffect{println "likedItem: $it"}
//				.groupCount(m)
//				//.sideEffect{println "countsMap: $m"}
//				.in(userItemFeedbackEdgeLabel)
//				.gather.transform{it[rand.nextInt(it.size())]}
//				//.sideEffect{println "newUser: $it"}
//				//.loop('x'){println "gremlinLoopCount: ${it.loops} / $max";  it.loops < max}
//				.loop('x'){it.loops < max}
//				.iterate()
//			//println " ---- $c done ----"
//			c++
//		}
//		m = m.sort {a, b -> b.value <=> a.value}
//		//println "intermediate result $m"
//		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
//		// def msum = m.values().sum()
//		// m.each{k,v -> m[k] = v / msum}
//		
//		return m.keySet() as List;
		
		

	}
}
