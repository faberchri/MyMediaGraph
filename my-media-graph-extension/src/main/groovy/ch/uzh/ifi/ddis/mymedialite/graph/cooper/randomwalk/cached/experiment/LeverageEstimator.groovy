package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import groovy.json.JsonOutput.*

import java.util.concurrent.ConcurrentHashMap

import org.mymedialite.data.IPosOnlyFeedback
import org.mymedialite.data.PosOnlyFeedback
import org.mymedialite.datatype.SparseBooleanMatrix
import org.mymedialite.eval.CandidateItems
import org.mymedialite.eval.ItemsParallel
import org.mymedialite.eval.measures.IMeasure
import org.mymedialite.eval.measures.IMeasure.AucAdapter

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.PGlobalNormalized

class LeverageEstimator {

	public static class EstimatorAdapter {
		def lowerBound = -1.5

		def upperBound = 3.5

		def initialStepSize = 0.5

		def numberOfTestUsers = 200

		def optimizationMeasure = new AucAdapter()

		def plateauSize = 3

		def numCycles = 80

		def epsilon = 0.003

		def shifter = new LinearSearchLeverageShifter(lowerBound, upperBound, initialStepSize)

		def test = new CyclesOptTest(numCycles, plateauSize)

		// def test = new EpsilonOptTest(epsilon, plateauSize)

		def train(def graph, PGlobalNormalized rec) {
			println "--- start estimating leverage ... ---"
			LeverageEstimator est = new LeverageEstimator(graph, rec, numberOfTestUsers)
			// this.leverage = est.estimateEps(0.003, 3, new AucAdapter())
			rec.leverage = est.estimate(test, shifter,optimizationMeasure)
			println "final leverage: ${rec.leverage}"
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder()
			builder.append("lowerBound=")
			builder.append(lowerBound)
			builder.append(" upperBound=")
			builder.append(upperBound)
			builder.append(" initialStepSize=")
			builder.append(initialStepSize)
			builder.append(" numberOfTestUsers=")
			builder.append(numberOfTestUsers)
			builder.append(" optimizationMeasure=")
			builder.append(optimizationMeasure)
			builder.append(" plateauSize=")
			builder.append(plateauSize)
			builder.append(" numCycles=")
			builder.append(numCycles)
			builder.append(" epsilon=")
			builder.append(epsilon)
			builder.append(" shifter=")
			builder.append(shifter)
			builder.append(" test=")
			builder.append(test)
			return builder.toString()
		}
	}

	public static class BaseOptTest{

		LinkedHashMap measures = new LinkedHashMap()

		def plateauSize

		def BaseOptTest(def plateauSize){
			this.plateauSize = plateauSize
		}

		def done(def currentLeverage, def currentMeasureV){
			measures.put(currentLeverage, currentMeasureV)
			measures = measures.sort {a,b ->
				b.value <=> a.value
			}
			println "all tested leverages (current: $currentLeverage): $measures"
		}

		def getBestLeverage(){
			measures = measures.sort {a,b ->
				b.value <=> a.value
			}

			def vLi = new ArrayList(measures.keySet())[0..<plateauSize]
			return vLi.sum() / vLi.size()
		}
	}

	public static class CyclesOptTest extends BaseOptTest{
		def numCycles

		def cCycles = 0

		public CyclesOptTest(def numCycles, def plateauSize){
			super(plateauSize)
			this.numCycles = numCycles
		}

		def done(def currentLeverage, def currentMeasureV){
			super.done(currentLeverage, currentMeasureV)
			cCycles++
			println "--- Cycles: $cCycles / $numCycles ---"
			return cCycles >= numCycles
		}
	}

	public static class EpsilonOptTest extends BaseOptTest{
		def epsilon

		public EpsilonOptTest(def epsilon, def plateauSize){
			super(plateauSize)
			this.epsilon = epsilon
		}

		def done(def currentLeverage, def currentMeasureV){
			super.done(currentLeverage, currentMeasureV)
			if (measures.size() < plateauSize) {
				return false
			}

			measures = measures.sort {a,b ->
				b.value <=> a.value
			}

			// get best and last of plateau size
			def vLi = new ArrayList(measures.values())
			def f = vLi[0]
			def s = vLi[plateauSize-1]
			println "--- f: $f, s: $s, vLi: $vLi ---"

			return Math.abs(f - s) <= epsilon
		}
	}

	public static class BaseLeverageShifter{
		def rangeStart

		def rangeEnd

		def start

		def BaseLeverageShifter(def start, def rangeStart, def rangeEnd){
			this.start = start
			this.rangeStart = rangeStart
			this.rangeEnd = rangeEnd
		}

		def getStart(){
			return start
		}
	}

	public static class RandomLeverageShifter extends BaseLeverageShifter{

		def rand = new Random()

		def RandomLeverageShifter(def rangeStart, def rangeEnd){
			super(null, rangeStart, rangeEnd)
		}

		def next(def testLeverage, def measure){
			return rangeStart + (rangeEnd - rangeStart) * rand.nextDouble()
		}

		def getStart() {
			return next(null,null)
		}
	}

	public static class BinarySearchLeverageShifter extends BaseLeverageShifter{

		def allTests = [:]

		def pivot

		def BinarySearchLeverageShifter(def rangeStart, def rangeEnd){
			super(rangeStart, rangeStart, rangeEnd)
			this.pivot = rangeStart
		}

		def next(def testLeverage, def measure){
			allTests[testLeverage] = measure
			shift()
			return pivot
		}

		private void shift(){
			if (allTests[rangeStart] == null){
				pivot = rangeStart
			} else if (allTests[rangeEnd] == null){
				pivot = rangeEnd
			} else {
				def halfDistance = Math.abs(rangeStart - rangeEnd) / 2.0
				if (allTests[rangeStart] < allTests[rangeEnd]){
					rangeStart = rangeStart + halfDistance
					pivot = rangeStart
				} else {
					rangeEnd = rangeEnd - halfDistance
					pivot = rangeEnd
				}
			}
		}
	}

	public static class LinearSearchLeverageShifter extends BaseLeverageShifter{

		def numSteps

		def allTests = [:]

		def stepSize

		def LinearSearchLeverageShifter(def rangeStart, def rangeEnd, def initialStepSize){
			super(rangeStart, rangeStart, rangeEnd)
			this.stepSize = initialStepSize
			this.numSteps = Math.abs(rangeStart - rangeEnd) / stepSize
		}

		def next(def testLeverage, def measure){
			allTests[testLeverage] = measure
			def newL = testLeverage + stepSize
			if (newL > rangeEnd){
				restart()
				newL = rangeStart
			}
			println "next | prev: $testLeverage ($measure) | new: $newL | stepsize: $stepSize | numSteps: $numSteps"
			return newL
		}

		def restart(){
			// sort tests map
			//			allTests = allTests.sort {a,b ->
			//				b.value <=> a.value
			//			}

			println "restart_1: $rangeStart | $rangeEnd | $stepSize"

			//			// set new limits
			//			def it = allTests.iterator()
			//			def f = it.next().key
			//			def s = it.next().key
			//			if (f < s){
			//				rangeStart = f
			//				rangeEnd = s
			//			} else {
			//				rangeStart = s
			//				rangeEnd = f
			//			}
			//
			//			// set step size
			//			stepSize = Math.abs(rangeStart - rangeEnd) / numSteps

			def max = allTests.max { it.value }.key

			rangeStart = max -stepSize
			rangeEnd = max + stepSize

			stepSize = Math.abs(rangeStart - rangeEnd) / numSteps

			println "restart_2: $rangeStart | $rangeEnd | $stepSize"


		}
	}

	def graph

	PGlobalNormalized rec

	def trainData

	def testData

	def testUsers

	public LeverageEstimator(def graph, PGlobalNormalized rec, def numberOfUsersToTest) {
		this.graph = graph
		this.rec = rec
		def testEdges = removeEdgesRandomly(numberOfUsersToTest)
		this.testData = getTestData(testEdges)
		this.trainData = getTrainingData()

		//		this.shifter = new LinearSearchLeverageShifter(rangeStart, rangeEnd)
	}

	//	def estimateEps(def epsilon, def plateauSize, IMeasure... f){
	//		return runEst(f, new EpsilonOptTest(epsilon, plateauSize), shifter)
	//	}
	//
	//	def estimateCyc(def cycles, def plateauSize, IMeasure... f){
	//		return runEst(f, new CyclesOptTest(cycles, plateauSize), shifter)
	//	}

	def estimate(def test, def shift, IMeasure... f){
		return runEst(f, test, shift)
	}

	def runEst(IMeasure[] f, def test, def shifter){
		def testLeverage = shifter.getStart()
		while(true){
			def m = cycle(testLeverage, f)
			println "leverage: $testLeverage - $m"
			if (test.done(testLeverage, m)){
				println "--- Estimation done ---"
				break
			}
			testLeverage = shifter.next(testLeverage, m)
			println "--- cycle done ---"
		}
		return test.getBestLeverage()
	}

	def cycle(def testLeverage, IMeasure[] measure){
		rec.setUserRecommendationsMap(new ConcurrentHashMap())
		rec.initPopRanks(testLeverage)
		//Collection<Integer> relevant_users = testData.allUsers()//[0..numberOfUsersToTest]  // Users that will be taken into account in the evaluation.
		Collection<Integer> relevant_items = trainData.allItems()  // Items that will be taken into account in the evaluation.

		Map allM = ItemsParallel.evaluate(rec, testData, trainData, testUsers, relevant_items, null, CandidateItems.OVERLAP, false, Arrays.asList(measure))
		return allM.get(measure[0].getName())
	}



	IPosOnlyFeedback getTestData(def testEdges){
		return getData(testEdges)
	}

	IPosOnlyFeedback getTrainingData(){
		def remainingE = graph.E.filter{it.label== rec.userItemFeedbackEdgeLabel}.toList()
		return getData(remainingE)
	}

	IPosOnlyFeedback getData(def edges){
		IPosOnlyFeedback data = new PosOnlyFeedback(SparseBooleanMatrix.class)
		for (e in edges){
			def uId = e.outV.next().getProperty(rec.myMediaLiteUserIdVertexProperty)
			def iId = e.inV.next().getProperty(rec.myMediaLiteItemIdVertexProperty)
			data.add(uId, iId)
		}
		return data
	}

	List removeEdgesRandomly(int numTestUsers){
		def allEdges = graph.E.filter{it.label== rec.userItemFeedbackEdgeLabel}.toList()
		Collections.shuffle(allEdges)
		this.testUsers = [] as Set
		def result = []
		while(testUsers.size() < numTestUsers){
			def e = allEdges.pop()
			// we need to make sure that a vertex becomes not entirely unconnected from the graph
			if (e.outV.next().out(rec.userItemFeedbackEdgeLabel).count() > 1 && e.inV.next().in(rec.userItemFeedbackEdgeLabel).count() > 1){
				def uId = e.outV.next().getProperty(rec.myMediaLiteUserIdVertexProperty)
				this.testUsers.add(uId)
				result.add(e)
				e.remove()
			}
		}
		//		println "testUsers: $testUsers"
		//		def b = 5
		//		println "First $b entries of removed feedback (size: ${result.size()}): ${result[0..<((b < result.size())? b: result.size())]}"
		return result
	}

	//	List removeEdgesRandomly(double quota){
	//		def allEdges = graph.E.filter{it.label== rec.userItemFeedbackEdgeLabel}.toList()
	//		Collections.shuffle(allEdges)
	//		int resultTargetSize = Math.floor(quota * allEdges.size()).intValue()
	//		def result = []
	//		while(result.size() < resultTargetSize){
	//			def e = allEdges.pop()
	//			result.add(e)
	//			e.remove()
	//		}
	//		def b = 5
	//		println "First $b entries of removed feedback (size: ${result.size()}): ${result[0..<((b < result.size())? b: result.size())]}"
	//		return result
	//	}
}
