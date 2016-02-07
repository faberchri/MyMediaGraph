package ch.uzh.ifi.ddis.mymedialite.graph.gori


import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender

import com.google.common.collect.Sets
import com.tinkerpop.blueprints.Vertex

abstract class AbstractItemRank extends ItemRankGraphRecommender {

	static final double alpha = 0.85

	def itemFeedbackCash = new ConcurrentHashMap()

	double[][] getNormalizedCorrelationMatrix(List items) {
		int dimension = items.size()
		double[][] raw = getRawCorrelationMatrix(items) // both quadratic
		double[][] nor = new double[dimension][dimension]
		for(int i = 0; i < dimension; i++){
			// divide all entries of column j of raw by
			// sum of all entries in column j in raw
			double jSum = 0.0
			for(int j = 0; j < dimension; j++){
				jSum = jSum + raw[i][j]
			}
			for(int j = 0; j < dimension; j++){
				nor[j][i] = raw[j][i] / jSum // swap indices to assure correct normalization (see test case)
			}
		}
		return nor
	}

	double[][] getRawCorrelationMatrix(List items){
		int dimension = items.size()
		double[][] m = new double[dimension][dimension]
		for(int i = 0; i < dimension; i++){
			for(int j = i; j < dimension; j++){
				if (i == j) {
					m[i][j] = 0.0
				} else {
					def correlation = getRawCorrelation(items[i], items[j]).doubleValue()
					m[i][j] = correlation
					m[j][i] = correlation
				}
			}
		}
		return m
	}

	int getRawCorrelation(Vertex item1, Vertex item2) {
		def i1Fb = getItemFeedback(item1)
		def i2Fb = getItemFeedback(item2)
		if (i1Fb.size() < i2Fb.size()){
			return Sets.intersection(i1Fb, i2Fb).size()
		}
		return Sets.intersection(i2Fb, i1Fb).size()
	}

	Set getItemFeedback(Vertex item){
		def fb = itemFeedbackCash[item]
		if (fb == null){
			fb = item.in(userItemFeedbackEdgeLabel).toList() as Set
			itemFeedbackCash[item] = fb
		}
		return fb
	}

	List sortAndShortenResultList(Map m, def userVertex){
		m = m.sort { a, b ->
			b.value <=> a.value
		}
		//println "final sorted counts map ${m[0..10]}"

		m.keySet().removeAll(userVertex.out(userItemFeedbackEdgeLabel).toList())
		return m.keySet() as List
	}
}
