package ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.convergence

import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter.RandomWalkCounter

class CompleteConstantOrderConvergenceTester {

	def previousOrder

	def evaluationInterval

	def previousTestCycle = 0

	public CompleteConstantOrderConvergenceTester(def evaluationInterval) {
		this.evaluationInterval = evaluationInterval
	}

	def setPreviousOrder(order){
		//printOrder(previousOrder, "old")
		previousOrder = order.copyRankedRandomWalks()
		//printOrder(previousOrder, "new")
		//println "----"
	}

	def printOrder(def o, def what){
		if (o == null){
			println "$what order: null"
		} else if (o.size() >10){
			println "$what order: ${o[0..10]}"
		} else {
			println "$what order: $o"
		}
	}

	def boolean converged(RandomWalkCounter currentOrder){
		if (previousOrder == null) {
			setPreviousOrder(currentOrder)
			return false
		}
		def res = false
		if (currentOrder.effectiveWalksCount > previousTestCycle
		&& currentOrder.effectiveWalksCount % evaluationInterval == 0) {
			//println "effective walks: $currentOrder.effectiveWalksCount | null walks: $currentOrder.nullWalksCount"
			res = evaluateOrder(currentOrder)
			previousTestCycle = currentOrder.effectiveWalksCount
		}
		return res
	}

	def evaluateOrder(def currentOrder){
		if (! lengthValid(currentOrder)){
			setPreviousOrder(currentOrder)
			return false
		}
		def currentOrderCollection = currentOrder.getRankedRandomWalks()
		def res = sameSequence(previousOrder,currentOrderCollection)
		setPreviousOrder(currentOrder)
		return res
	}

	def lengthValid(def currentOrder){
		return currentOrder.size() == previousOrder.size()
	}

	def getListOfSortedVertices(def m){
		return new ArrayList(sortMap(m).keySet())
	}

	def sortMap(def m){
		return m.sort{a, b ->
			def c = b.value <=> a.value
			if (c == 0){
				c = a.key.id <=> b.key.id
			}
			return c
		}
	}

	def sameSequence(oldOrder, newOrder){
		//println "old order: $oldOrder"
		//println "new order: $newOrder"
		def oldIterator = oldOrder.iterator()
		for (vertexNew in newOrder){
			def vertexOld = oldIterator.next()
			if (! vertexNew.equals(vertexOld)){
				return false
			}
		}
		return true
	}
}
