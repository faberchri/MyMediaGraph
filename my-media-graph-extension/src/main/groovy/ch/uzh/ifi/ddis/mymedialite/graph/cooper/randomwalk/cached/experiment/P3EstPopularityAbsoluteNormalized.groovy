package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.BaseCooper
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.PPopularityAbsoluteNormalized
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.LeverageEstimator.EstimatorAdapter

class P3EstPopularityAbsoluteNormalized extends PPopularityAbsoluteNormalized {

	def estA = new EstimatorAdapter()

	public P3EstPopularityAbsoluteNormalized() {
		super(BaseCooper.P3_DEFAULT_POWER, 0.8)
	}

	@Override
	public void train() {
		super.train()
		estA.train(graph, this)
		super.train()
	}

	@Override
	public String toString() {
		return super.toString() + " " + estA.toString()
	}

	//	def lowerBound = -0.5
	//
	//	def upperBound = 1.5
	//
	//	def initialStepSize = 0.5
	//
	//	def numberOfTestUsers = 200
	//
	//	def optimizationMeasure = new AucAdapter()
	//
	//	def plateauSize = 3
	//
	//	def numCycles = 40
	//
	//	def epsilon = 0.003
	//
	//	@Override
	//	public void train() {
	//		super.train()
	//		LeverageEstimator est = new LeverageEstimator(graph, this, numberOfTestUsers)
	//		def shifter = new LinearSearchLeverageShifter(lowerBound, upperBound, initialStepSize)
	//		def test = new CyclesOptTest(numCycles, plateauSize)
	//		// def test = new EpsilonOptTest(epsilon, plateauSize)
	//
	//		// this.leverage = est.estimateEps(0.003, 3, new AucAdapter())
	//		this.leverage = est.estimate(test, shifter, new AucAdapter())
	//
	//		println "final leverage: $leverage"
	//		super.train()
	//		//		this.popularityRank = initPopRanks(leverage)
	//	}
	//
	//	@Override
	//	public String toString() {
	//		StringBuilder builder = new StringBuilder(super.toString())
	//		builder.append(" lowerBound=")
	//		builder.append(lowerBound)
	//		builder.append(" upperBound=")
	//		builder.append(upperBound)
	//		builder.append(" initialStepSize=")
	//		builder.append(initialStepSize)
	//		builder.append(" numberOfTestUsers=")
	//		builder.append(numberOfTestUsers)
	//		builder.append(" optimizationMeasure=")
	//		builder.append(optimizationMeasure)
	//		builder.append(" plateauSize=")
	//		builder.append(plateauSize)
	//		builder.append(" numCycles=")
	//		builder.append(numCycles)
	//		builder.append(" epsilon=")
	//		builder.append(epsilon)
	//		return builder.toString()
	//	}
}
