package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import java.util.concurrent.ConcurrentHashMap

import org.mymedialite.data.IPosOnlyFeedback
import org.mymedialite.data.PosOnlyFeedback
import org.mymedialite.datatype.SparseBooleanMatrix
import org.mymedialite.eval.CandidateItems
import org.mymedialite.eval.Items

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P3;

class DynamicLowDegreeP3 extends P3 {

	def numberOfUsersToTest = 50

	def optimizationSetQuota = 0.1

	def lowerB = -100

	def upperB = 100.0

	def stepSize = 10

	def beta = [lowerB, 0.0, 0.0]

	def betaDone = [false, false, false]

	@Override
	public void train() {
		super.train()

		// optimize betas
		beta = findBeta()

		// reconstruct graph after edge removal
		super.train()
	}

	@Override
	public Object performRandomWalk(Object randomWalkCounter, Object start, Object rand) {

		def currentWeight = 1.0

		for (i in 0..2){
			start = hop(start, rand)
			def v = beta[i] / (double) getDegree(start)

			//			def v = Math.pow(getDegree(start), beta[i])

			currentWeight = currentWeight + v
		}
		randomWalkCounter.updateCount(start, currentWeight)
	}

	def findBeta(){
		// remove edges for optimization
		def testEdges = removeEdgesRandomly(optimizationSetQuota)

		def testData = getTestData(testEdges)
		def trainData = getTrainingData()
		def testUsers = testData.allUsers()
		Collections.shuffle(testUsers)
		testUsers = testUsers[0..<((numberOfUsersToTest < testUsers.size())? numberOfUsersToTest : testUsers.size())]
		def betas = [:]
		def cBeta = this.beta
		println "First beta: $cBeta"
		while(! optimizationDone2(cBeta)){
			def m = optimizationCycle(testData, trainData, testUsers, cBeta)
			betas.put(new ArrayList(cBeta), m)
			cBeta = shiftBeta2(cBeta, getBestBeta(betas))
			setUserRecommendationsMap(new ConcurrentHashMap())
			println "All tested betas so far: $betas"
			println "Shifted beta: $cBeta"
			println "--- cycle done ---"
		}

		// select beta with highest value in metric
		return getBestBeta(betas)
	}

	List getBestBeta(Map betas){
		def bestBeta
		def bestM = 0.0
		println "New best beta found: $bestBeta ($bestM)"
		betas.each {k, v ->
			if (v > bestM){
				bestBeta = k
				bestM = v
				println "New best beta found: $bestBeta ($bestM)"
			}
		}
		return bestBeta
	}

	IPosOnlyFeedback getTestData(def testEdges){
		return getData(testEdges)
	}

	IPosOnlyFeedback getTrainingData(){
		def remainingE = graph.E.filter{it.label== userItemFeedbackEdgeLabel}.toList()
		return getData(remainingE)
	}

	IPosOnlyFeedback getData(def edges){
		IPosOnlyFeedback data = new PosOnlyFeedback(SparseBooleanMatrix.class)
		for (e in edges){
			def uId = e.outV.next().getProperty(myMediaLiteUserIdVertexProperty)
			def iId = e.inV.next().getProperty(myMediaLiteItemIdVertexProperty)
			data.add(uId, iId)
		}
		return data
	}

	double optimizationCycle(IPosOnlyFeedback testData, IPosOnlyFeedback trainData, def testUsers, def betaToTry){
		this.beta = betaToTry
		//Collection<Integer> relevant_users = testData.allUsers()//[0..numberOfUsersToTest]  // Users that will be taken into account in the evaluation.
		Collection<Integer> relevant_items = trainData.allItems()  // Items that will be taken into account in the evaluation.

		Map allM = Items.evaluateParallel(this, testData, trainData, testUsers, relevant_items,CandidateItems.OVERLAP, false)
		println allM
		return allM.get("AUC")
	}

	List removeEdgesRandomly(double quota){
		def allEdges = graph.E.filter{it.label== userItemFeedbackEdgeLabel}.toList()
		Collections.shuffle(allEdges)
		int resultTargetSize = Math.floor(quota * allEdges.size()).intValue()
		def result = []
		while(result.size() < resultTargetSize){
			def e = allEdges.pop()
			result.add(e)
			e.remove()
		}
		def b = 5
		println "First $b entries of removed feedback (size: ${result.size()}): ${result[0..<((b < result.size())? b: result.size())]}"
		return result
	}

	//	double evaluate(def testData){
	//		double m = 0.0
	//		for (e in testData){
	//			def nM = AUC.compute(getItemIdRankList(e.key), e.value, [])
	//			println "AUC: $nM"
	//			m = m + nM
	//		}
	//		m = m / testData.size()
	//		println "Tot AUC: $m"
	//		return m
	//	}

	List getItemIdRankList(def userId){
		def r = []
		def vList = getRankList(userId)
		for(i in vList){
			r.add(i.getProperty(myMediaLiteItemIdVertexProperty))
		}
		return r
	}

	List shiftBeta2(def beta, def bestBeta){
		for(i in 0..2){
			if (! betaDone[i]){
				beta[i] = beta[i] + stepSize
				if (beta[i] > upperB){
					betaDone[i] = true
					beta[i] = bestBeta[i]
					if (i+1 < beta.size()){
						beta[i+1] = lowerB
					}
				}
				return beta
			}
		}
		return beta
	}

	List shiftBeta(def beta){
		beta[2] = beta[2] + stepSize
		if (beta[2] > upperB){
			beta[2] = lowerB
			beta[1] = beta[1] + stepSize
			if (beta[1] > upperB){
				beta[1] = lowerB
				beta[0] = beta[0] + stepSize
			}
		}
		println "Shifted beta: $beta"
		return beta
	}

	boolean optimizationDone2(def currentBeta){
		return betaDone[2]
	}

	boolean optimizationDone(def currentBeta){
		if (currentBeta[0] > upperB){
			return true
		}
		return false
	}

	int getMaxDegree(def type){
	}
}
