package ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment

import java.util.List

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.Tokens.T


class ShowPopularityNormalizedSampledRankedCollaborativeFilteringGraphRecommender
		extends ItemRankGraphRecommender {

	@Override
	protected List<Vertex> runRankQuery(Object userVertex) {
		// Random sample all paths from current user to new shows in one other user distance, count number of paths to new show, divide each count by number of incoming watched edges of show, order descending by counts 
		def samplingRatio = 5
		def showsWatchedByCurrentUser = [];
		//def duration = System.currentTimeMillis()
		def sampledResult = userVertex.out(userItemFeedbackEdgeLabel).aggregate(showsWatchedByCurrentUser).in(userItemFeedbackEdgeLabel).except([userVertex]).gather{ def endIndex = (it.size() < samplingRatio) ? 0 : it.size().intdiv(samplingRatio); Collections.shuffle(it); return it[0..<endIndex] }.scatter.out(userItemFeedbackEdgeLabel).except(showsWatchedByCurrentUser).groupCount.cap.transform{ def m=[:]; it.each{k,v -> m.put(k, v/k.in.count())}; return m}.orderMap(T.decr).toList();
		//duration = System.currentTimeMillis() - duration;
		//println "Sampled duration: ${duration / 1000.0} seconds."
		
		//duration = System.currentTimeMillis()
		//showsWatchedByCurrentUser = [];
		//def fullResult = 	userVertex.out(userItemFeedbackEdgeLabel).aggregate(showsWatchedByCurrentUser).in(userItemFeedbackEdgeLabel).except([userVertex]).out(userItemFeedbackEdgeLabel).except(showsWatchedByCurrentUser).groupCount.cap.transform{ def m=[:]; it.each{k,v -> m.put(k, v/k.in.count())}; return m}.orderMap(T.decr).toList();
		//duration = System.currentTimeMillis() - duration;
		//println "Full duration: ${duration / 1000.0} seconds."
		
		//println "full    result: $fullResult"
		//println "sampled result: $sampledResult"
		return sampledResult;
		
	}

}
