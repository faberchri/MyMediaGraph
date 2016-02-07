package ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.convergence

class TopAbsoluteConstantOrderConvergenceTester extends
TopConstantOrderConvergenceTester {

	def final topItemNumber

	public TopAbsoluteConstantOrderConvergenceTester(int evaluationInterval,
	int numberOfItemsToEvaluate) {
		super(evaluationInterval)
		this.topItemNumber = numberOfItemsToEvaluate
	}

	@Override
	public Object getNumberOfItemsToEvaluate() {
		return topItemNumber
	}
}
