package ch.uzh.ifi.ddis.mymedialite.graph.cooper

import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.convergence.NullConvergenceTester

import com.tinkerpop.gremlin.groovy.Gremlin


class TestP extends GroovyTestCase {

	static {
		Gremlin.load()
	}

	def g = new TestGraph()

	void testP3Tree(){
        def deltas = []
        testCalculateRanksMap(
				BaseCooper.NULL_DEFAULT_ALPHA,
				BaseCooper.P3_DEFAULT_POWER,
				new ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree.P3(),
				0.005,
                deltas)
	}

	void testP3Cached(){
        def deltas = []
        testCalculateRanksMap(
				BaseCooper.NULL_DEFAULT_ALPHA,
				BaseCooper.P3_DEFAULT_POWER,
				new ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P3(),
				0.005,
                deltas)
	}

	void testP3Probability(){
        def deltas = []
        testCalculateRanksMap(
				BaseCooper.NULL_DEFAULT_ALPHA,
				BaseCooper.P3_DEFAULT_POWER,
				new ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.P3(),
				0.0000001,
                deltas)
	}

	void testP5Tree(){
        def deltas = []
        testCalculateRanksMap(
				BaseCooper.NULL_DEFAULT_ALPHA,
				BaseCooper.P5_DEFAULT_POWER,
				new ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree.P5(),
				0.005,
                deltas)
	}

	void testP5Cached(){
        def deltas = []
        testCalculateRanksMap(
				BaseCooper.NULL_DEFAULT_ALPHA,
				BaseCooper.P5_DEFAULT_POWER,
				new ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P5(),
				0.007,
                deltas)
	}

	void testP5Probability(){
        def deltas = []
        testCalculateRanksMap(
				BaseCooper.NULL_DEFAULT_ALPHA,
				BaseCooper.P5_DEFAULT_POWER,
				new ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.P5(),
				0.0000001,
                deltas)
	}

    void testP3AlphaCached(){
        def deltas = []
        (-2).step(4, 0.1){
            def rec = new ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P3Alpha()
            rec.alpha = it
            println "-- test alpha = $it --"
            testCalculateRanksMap(
                    it,
                    BaseCooper.P3_DEFAULT_POWER,
                    rec, 0.01, deltas)
            println "Total deltas: ${deltas.sum()}, Avg. deviation: ${deltas.sum() / deltas.size()}, Top 10 deviations: ${deltas.sort{-it}[0..<10]}"
        }
    }


    //	void testP3AlphaTree(){
	//		testCalculateRanksMap(
	//				BaseCooper.EFFECTIVE_DEFAULT_ALPHA,
	//				BaseCooper.P3_DEFAULT_POWER,
	//				new ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree.P3Alpha())
	//	}
	//
	//	void testP3AlphaCached(){
	//		testCalculateRanksMap(
	//				BaseCooper.EFFECTIVE_DEFAULT_ALPHA,
	//				BaseCooper.P3_DEFAULT_POWER,
	//				new ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3Alpha())
	//	}

	void testP3AlphaProbability(){
        def deltas = []
        (-2).step(4, 0.1){
			def rec = new ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.P3Alpha()
			rec.alpha = it
			println "-- test alpha = $it --"
			testCalculateRanksMap(
					it,
					BaseCooper.P3_DEFAULT_POWER,
					rec, 0.0000001,
                    deltas)
		}
	}

	void testP5AlphaProbability(){
        def deltas = []
		(-2).step(4, 0.1){
			def rec = new ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.P5Alpha()
			rec.alpha = it
			println "-- test alpha = $it --"
			testCalculateRanksMap(
					it,
					BaseCooper.P5_DEFAULT_POWER,
					rec, 0.0000001, deltas)
		}
        println "Total deltas: ${deltas.sum()}, Avg. deviation: ${deltas.sum() / deltas.size()}, Top 10 deviations: ${deltas.sort{-it}[0..<10]}"
    }

	def testCalculateRanksMap(def alpha, def walkLength, def recommender, def precision, def deltas) {
		def vertices = g.getAllVerticesSortedByName()
		for (i in vertices) {
			for (j in vertices) {
				compare(i, j, alpha, walkLength, recommender, precision, deltas)
			}
		}
	}

	def compare(def startVertex, def targetVertex, def alpha, def walkLength, def recommender, def precision, deltas) {
		def groundTruth = g.getNormalizedProbability(startVertex.name, targetVertex.name, alpha, walkLength)
		def randowWalkCounts = recommender.calculateRanksMap(startVertex, new NullConvergenceTester(), 100000)
		//def simulationResult = getNormalizedCountsMap(randowWalkCounts)
		def simulationResult = simulationResultsByName(randowWalkCounts)
		if (simulationResult.containsKey(targetVertex.name)){
			simulationResult = simulationResult[targetVertex.name]
		}else{
			simulationResult = 0.0
		}
        def delta = (groundTruth - simulationResult).abs()
        deltas.add(delta)
		println "$startVertex.name -> $targetVertex.name -- exp: $groundTruth vs. actual: $simulationResult (delta: $delta)"
		assertEquals(groundTruth, simulationResult, precision)
	}

	def simulationResultsByName(def randowWalkCounts){
		def m = [:]
		randowWalkCounts.each {k, v ->
			m[k.name] = v
		}
		m = m.sort()
		//println m
		return m
	}
}
