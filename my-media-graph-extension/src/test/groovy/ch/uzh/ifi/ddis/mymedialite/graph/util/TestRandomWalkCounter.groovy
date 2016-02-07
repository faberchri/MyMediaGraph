package ch.uzh.ifi.ddis.mymedialite.graph.util

import ch.uzh.ifi.ddis.mymedialite.graph.randomwalk.counter.ConstantlyOrderedRandomWalkCounter;

class TestRandomWalkCounter extends GroovyTestCase {

	class DummyVertex{
		static idGen = 1

		def final id

		DummyVertex(){
			id = idGen++
		}

		@Override
		public String toString() {
			return "DummyVertex-$id"
		}
	}

	void test(){
		def counter = new ConstantlyOrderedRandomWalkCounter()

		def vertex1 = new DummyVertex()
		def vertex2 = new DummyVertex()
		def vertex3 = new DummyVertex()
		def vertex4 = new DummyVertex()

		counter.updateCount(vertex1)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex1)

		counter.updateCount(vertex2)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex1, vertex2)

		counter.updateCount(vertex4)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex1, vertex2, vertex4)

		counter.updateCount(vertex3)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex1, vertex2, vertex3, vertex4)

		counter.updateCount(vertex4)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex4, vertex1, vertex2, vertex3)

		counter.updateCount(vertex3)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex3, vertex4, vertex1, vertex2)

		counter.updateCount(vertex3)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex3, vertex4, vertex1, vertex2)

		counter.updateCount(vertex1)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex3, vertex1, vertex4, vertex2)

		counter.updateCount(vertex1)
		assertLengthAndOrder(counter.getRankedRandomWalks(), vertex1, vertex3, vertex4, vertex2)

		assertLengthAndOrder(counter.copyRankedRandomWalks(), vertex1, vertex3, vertex4, vertex2)

		def vertexCountsMap = counter.getCopyOfVertxCountsInOrderedMap()
		println "vertexCountsMap: $vertexCountsMap"
		assertEquals(4, vertexCountsMap.size())
		def it = vertexCountsMap.iterator()

		def entry = it.next()
		assertEquals(vertex1, entry.key)
		assertEquals(3, entry.value)

		entry = it.next()
		assertEquals(vertex3, entry.key)
		assertEquals(3, entry.value)

		entry = it.next()
		assertEquals(vertex4, entry.key)
		assertEquals(2, entry.value)

		entry = it.next()
		assertEquals(vertex2, entry.key)
		assertEquals(1, entry.value)
	}

	def assertLengthAndOrder(def set, DummyVertex... args){
		println "Expected: $args"
		println "Actual: $set"
		println "-------"
		assertEquals(args.length, set.size())
		def setIt = set.iterator()
		for(a in args){
			def actualVertex = setIt.next().vertex
			assertEquals(actualVertex, a)
		}
	}
}
