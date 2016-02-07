package ch.uzh.ifi.ddis.mymedialite.graph.pathcount

import java.util.List

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender;

import com.tinkerpop.blueprints.Vertex;

class ItemAttribute3Path extends ItemRankGraphRecommender {

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		// get attributes of all users shows, get unwatched shows with same attributes, rank shows by number of paths to the show and append to end of result list, loop starting from previously found shows
		def l=[]; def a=[]; def s=[];
		userVertex.out(userItemFeedbackEdgeLabel).as('x').except(s).aggregate(s).out(itemAttributeEdgeLabel).except(a).aggregate(a).in(itemAttributeEdgeLabel).except(s).gather{def counts=it.countBy{it}; counts=counts.sort{i, j -> j.value <=> i.value}; l.addAll(counts.keySet()); return it}.scatter.loop('x'){it.loops < 20}.iterate();		
		return l
	}

}
