package ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.convergence

class TopQuotaConstantOrderConvergenceTester extends TopConstantOrderConvergenceTester {

	def final topItemNumber

	public TopQuotaConstantOrderConvergenceTester(int evaluationInterval, int totalNumberOfItemsToDetect, double topQuotaToEvaluate) {
		super(evaluationInterval)
		def tmp = Math.floor(totalNumberOfItemsToDetect * topQuotaToEvaluate).intValue()
		if (tmp < 1){
			topItemNumber = 1
		} else {
			topItemNumber = tmp
		}
		println "topItemNumber $topItemNumber"
	}

	@Override
	public Object getNumberOfItemsToEvaluate() {
		return topItemNumber
	}
}
