package ch.uzh.ifi.ddis.mymedialite.graph.zhou

import junit.framework.Assert

/**
 * Created by faber on 26.02.15.
 */
class TestS extends GroovyTestCase {

    void testProbS(){
        calcAndAssert(TestGraph.getWp(), new ProbS(), 0, [])
    }

    void testHeatS(){
        calcAndAssert(TestGraph.getWh(), new HeatS(), 0, [])
    }

    void testHybrid(){
        def hybrid = new Hybrid()
        0.step(1.05, 0.05){
            hybrid.lambda = it
            calcAndAssert(TestGraph.getWhp(it), hybrid, 0.00000001, [])
        }
    }

    void testHybridRw(){
        def hybrid = new ch.uzh.ifi.ddis.mymedialite.graph.zhou.randomwalk.Hybrid()
        def deviations = []
        0.step(1.05, 0.05){
            hybrid.lambda = it
            calcAndAssert(TestGraph.getWhp(it), hybrid, 0.03, deviations, {m -> m.collectEntries{key, value -> [(key): (value / m.values().sum())]}})
        }
        println "Total deviation: ${deviations.sum()}, Avg. deviation: ${deviations.sum() / deviations.size()}, Top 10 deviations: ${deviations.sort{-it}[0..<10]}"
    }

    void testHybrid2HopsRw(){
        def hybrid = new ch.uzh.ifi.ddis.mymedialite.graph.zhou.randomwalk.HybridTwoHops()
        def deviations = []
        0.step(1.05, 0.05){
            hybrid.lambda = it
            calcAndAssert(TestGraph.getWhp(it), hybrid, 0.01, deviations, {m -> m.collectEntries{key, value -> [(key): (value / m.values().sum())]}})
        }
        println "Total deviation: ${deviations.sum()}, Avg. deviation: ${deviations.sum() / deviations.size()}, Top 10 deviations: ${deviations.sort{-it}[0..<10]}"
    }

    void testHybridShortRw(){
        def hybrid = new ch.uzh.ifi.ddis.mymedialite.graph.zhou.randomwalk.HybridShort()
        def deviations = []
        0.step(1.05, 0.05){
            hybrid.lambda = it
            calcAndAssert(TestGraph.getWhp(it), hybrid, 0.01, deviations, {m -> m.collectEntries{key, value -> [(key): (value / m.values().sum())]}})
        }
        println "Total deviation: ${deviations.sum()}, Avg. deviation: ${deviations.sum() / deviations.size()}, Top 10 deviations: ${deviations.sort{-it}[0..<10]}"
    }

    def calcAndAssert(def groundTruthW, def abstractS, double delta, def deviations, def normalizationClosure =  {m -> m}){
        abstractS.setup(TestGraph.g)
        for (i in 1..4){
            def groundTruthM = TestGraph.getFTildeMap(i, groundTruthW)
            groundTruthM = normalizationClosure(groundTruthM)
            def userName = "User"+i
            def calculatedM = abstractS.getFTilde(TestGraph.g.V(TestGraph.nameProperty, userName).next());
            calculatedM.each{k, v ->
                def groundTruthValue = groundTruthM[k.getProperty(TestGraph.nameProperty)]
                def deviation = (groundTruthValue - v).abs()
                deviations.add(deviation)
                println "lambda: ${abstractS.getLambda()} | $userName | ${k.name}: expected $groundTruthValue vs. actual $v (delta: $deviation - allowed delta: $delta)"
                Assert.assertEquals(groundTruthValue.doubleValue(), v.doubleValue(), delta)
            }
        }
    }

}
