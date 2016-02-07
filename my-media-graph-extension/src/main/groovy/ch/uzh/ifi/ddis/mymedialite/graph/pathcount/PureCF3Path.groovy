package ch.uzh.ifi.ddis.mymedialite.graph.pathcount

import ch.uzh.ifi.ddis.mymedialite.graph.ItemRankGraphRecommender;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.Tokens.T

class PureCF3Path extends
ItemRankGraphRecommender {

	@Override
	protected List<Vertex> runRankQuery(def user) {
		// find all path from current user to new shows in one other user distance, count number of paths to new show, order descending by counts
		def showsWatchedByCurrentUser = [];
		return user.out(userItemFeedbackEdgeLabel).aggregate(showsWatchedByCurrentUser).in(userItemFeedbackEdgeLabel).out(userItemFeedbackEdgeLabel).except(showsWatchedByCurrentUser).groupCount.cap.orderMap(T.decr).toList();
	}
}