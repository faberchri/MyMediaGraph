package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import java.util.concurrent.ConcurrentHashMap

import ch.uzh.ifi.ddis.mymedialite.graph.util.BinaryShifter


class BinarySearchDynamicLowDegreeP3 extends DynamicLowDegreeP3 {

	def threshold = 0.001

	def minBoundDistance = 0.5

	BinaryShifter currentBinaryShifter

	def currentBetaIndex = 0

	def findBeta(){


		// remove edges for optimization
		def testEdges = removeEdgesRandomly(optimizationSetQuota)

		this.upperB = getOverallMaxDegree()
		this.lowerB = upperB * -1
		this.currentBinaryShifter= new BinaryShifter(lowerB, upperB, threshold, minBoundDistance)

		def testData = getTestData(testEdges)
		def trainData = getTrainingData()
		def testUsers = testData.allUsers()
		Collections.shuffle(testUsers)
		testUsers = testUsers[0..<((numberOfUsersToTest < testUsers.size())? numberOfUsersToTest : testUsers.size())]
		def betas = [:]
		while(! optimizationDone()){
			shiftBeta()

			def m = optimizationCycle(testData, trainData, testUsers, this.beta)
			currentBinaryShifter.update(m)

			betas.put(new ArrayList(this.beta), m)
			setUserRecommendationsMap(new ConcurrentHashMap())

			println "All tested betas so far: $betas"
			println "--- cycle done ---"
		}

		// select beta with highest value in metric
		return getBestBeta(betas)
	}

	def getOverallMaxDegree(){
		def m=[:]
		graph.V.both(userItemFeedbackEdgeLabel).groupCount(m).iterate()
		def maxE = m.max { it.value }
		println "Vertex with highest degree in graph: $maxE"
		return maxE.value
	}

	void shiftBeta(){
		this.beta[currentBetaIndex] = currentBinaryShifter.next()
		println "Shifted beta: $beta"
	}

	public boolean optimizationDone() {
		if (!currentBinaryShifter.hasNext()){
			println "currentBinaryShifter.hasNext(): false"
			this.beta[currentBetaIndex] = currentBinaryShifter.lowerLim
			this.currentBinaryShifter = new BinaryShifter(lowerB, upperB, threshold, minBoundDistance)
			currentBetaIndex++
		}
		return currentBetaIndex > 2
	}

}
