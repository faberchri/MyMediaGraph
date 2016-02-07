package ch.uzh.ifi.ddis.mymedialite.graph.gori

import no.uib.cipr.matrix.Matrix
import no.uib.cipr.matrix.Vector


class TestItemRankMatrix extends GroovyTestCase {

	TestGraph g = new TestGraph()

	ItemRankMatrix recommender = new ItemRankMatrix()

	public void testRank() {
		println "--- Rank ---"
		Vector d = g.getTestRoundedPersonalizationVector()
		Matrix c = g.getTestRoundedNormalizedCorrelationMatrix()
		def rankMap = recommender.rank(g.getItems(), d, c)
		g.evalRankMap(rankMap)
	}

	public void testGetNormalizedCorrelationMatrix() {
		println "--- Normalized ---"
		test(recommender.getNormalizedCorrelationMatrix(g.getItems()), g.getTestNormalizedCorrelationArray())
	}

	public void testGetRawCorrelationMatrix() {
		println "--- Raw ---"
		test(recommender.getRawCorrelationMatrix(g.getItems()), g.getTestRawCorrelationArray())
	}

	def test(def actual, def expected){
		println "Actual:\n${Arrays.deepToString(actual)}\nExpected:\n${Arrays.deepToString(expected)}"
		assertTrue(Arrays.deepEquals(actual, expected))
	}
}
