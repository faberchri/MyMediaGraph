package ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.convergence


abstract class TopConstantOrderConvergenceTester extends
CompleteConstantOrderConvergenceTester {

	public TopConstantOrderConvergenceTester(Object evaluationInterval) {
		super(evaluationInterval)
	}

	def lengthValid(Object currentOrder) {
		return previousOrder.size() > getNumberOfItemsToEvaluate()
	}

	def sameSequence(def oldOrder, def newOrder){
		//		println "new order: ${newOrder[0..10]}"
		def oldIterator = oldOrder.iterator()
		def newIterator = newOrder.iterator()
		int c = 0
		while(c <= getNumberOfItemsToEvaluate()){
			def vertexOld = oldIterator.next()
			def vertexNew = newIterator.next()
			if (! vertexNew.equals(vertexOld)){
				return false
			}
			c++
		}
		return true
	}

	abstract def getNumberOfItemsToEvaluate()
}
