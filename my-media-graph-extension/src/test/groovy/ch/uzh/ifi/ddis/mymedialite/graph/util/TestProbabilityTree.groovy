package ch.uzh.ifi.ddis.mymedialite.graph.util


class TestProbabilityTree extends GroovyTestCase {

	public void testGet() {
		println "--- testGet ---"
		def ranges = [ "Apfel":0.35, "Katze":0.25, "Maus":0.25, "Hund":0.15]

		ProbabilityTree<String> tree = new ProbabilityTree<String>(ranges)

		println tree

		assertEquals(null, tree.get(-0.0000001))
		assertEquals("Hund", tree.get(0.0))
		assertEquals("Hund", tree.get(0.14))
		assertEquals("Katze", tree.get(0.15))
		assertEquals("Katze", tree.get(0.35))
		assertEquals("Katze", tree.get(0.39999))
		assertEquals("Maus", tree.get(0.40))
		assertEquals("Maus", tree.get(0.45))
		assertEquals("Maus", tree.get(0.649999))
		assertEquals("Apfel", tree.get(0.66))
		assertEquals("Apfel", tree.get(0.999999))
		assertEquals(null, tree.get(1.0))
	}

	public void testAdd() {
		println "--- testAdd ---"
		ProbabilityTree<String> tree = new ProbabilityTree<String>()
		println tree
		tree.add("Hund", 0.15)
		println tree
		tree.add("Katze", 0.25)
		println tree
		tree.add("Maus", 0.25)
		println tree
		tree.add("Apfel", 0.35)
		println tree

		assertEquals(null, tree.get(-0.0000001))
		assertEquals("Hund", tree.get(0.0))
		assertEquals("Hund", tree.get(0.14))
		assertEquals("Katze", tree.get(0.15))
		assertEquals("Katze", tree.get(0.35))
		assertEquals("Katze", tree.get(0.39999))
		assertEquals("Maus", tree.get(0.40))
		assertEquals("Maus", tree.get(0.45))
		assertEquals("Maus", tree.get(0.649999))
		assertEquals("Apfel", tree.get(0.66))
		assertEquals("Apfel", tree.get(0.999999))
		assertEquals(null, tree.get(1.0))

		// tree.add("Error", 0.001)
	}
}
