package ch.uzh.ifi.ddis.mymedialite.graph.gori

import ch.uzh.ifi.ddis.mymedialite.graph.util.ProbabilityTree


class TestItemRankRandomWalk extends GroovyTestCase {


	public void testRank() {
		println "--- Rank ---"
		TestGraph g = new TestGraph()
		ItemRankRandomWalk recommender = new ItemRankRandomWalk()
		recommender.addCorrelationEdges(g.getG(), g.getItems(), g.getTestRoundedNormalizedCorrelationArray())

		def rankMap = recommender.rank(new ProbabilityTree(g.getTestRoundedPersonalizationMap()))
		g.evalRankMap(rankMap, 0.01)
	}

	//	public void testRank2() {
	//		println "--- Rank ---"
	//		TestGraph g = new TestGraph()
	//		ItemRankRandomWalk recommender = new ItemRankRandomWalkExperimental()
	//		recommender.addCorrelationEdges(g.getG(), g.getItems(), g.getTestRoundedNormalizedCorrelationArray())
	//
	//		def rankMap = recommender.rank(new ProbabilityTree(g.getTestRoundedPersonalizationMap()))
	//		g.evalRankMap(rankMap, 0.001)
	//	}
}
