package ch.uzh.ifi.ddis.mymedialite.graph.util


class TestBinaryShifter extends GroovyTestCase {

	public void testHasNext() {
		def bs = new BinaryShifter(-4.5, 11.2, 0.05, 0.001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())

		assertEquals(-4.5, bs.next(), 0.0)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		assertEquals(-4.5, bs.next(), 0.0)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		bs.update(0.2)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())

		assertEquals(11.2, bs.next(), 0.0)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		assertEquals(11.2, bs.next(), 0.0)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		bs.update(0.35)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())

		assertEquals(3.35, bs.next(), 0.000000000000001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		assertEquals(3.35, bs.next(), 0.000000000000001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		bs.update(0.55)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())

		assertEquals(7.275, bs.next(), 0.000000000000001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		assertEquals(7.275, bs.next(), 0.000000000000001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		bs.update(0.42)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())

		assertEquals(5.3125, bs.next(), 0.000000000000001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		assertEquals(5.3125, bs.next(), 0.000000000000001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		bs.update(0.44)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())

		assertEquals(4.33125, bs.next(), 0.000000000000001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		assertEquals(4.33125, bs.next(), 0.000000000000001)
		assertEquals(true, bs.hasNext())
		assertEquals(true, bs.hasNext())
		bs.update(0.57)
		assertEquals(false, bs.hasNext())
		assertEquals(false, bs.hasNext())
	}

	public void testHasNext2() {
		def bs = new BinaryShifter(-2.0, 8.0, 0.05, 0.001)
		bs.eachWithIndex {b, i ->
			if (i == 0){
				assertEquals(-2.0, b)
				bs.update(0.6)
			}
			if (i == 1){
				assertEquals(8.0, b)
				bs.update(0.2)
			}
			if (i == 3){
				assertEquals(3.0, b)
				bs.update(0.4)
			}
			if (i == 4){
				assertEquals(0.5, b)
				bs.update(0.66)
			}
			if (i == 5){
				assertEquals(-0.75, b)
				bs.update(0.68)
				assertEquals(false, bs.hasNext())
			}
		}
	}

	public void testHasNext3() {
		def bs = new BinaryShifter(-2.0, 8.0, 0.05, 0.001)
		bs.eachWithIndex {b, i ->
			if (i == 0){
				assertEquals(-2.0, b)
				bs.update(0.6)
			}
			if (i == 1){
				assertEquals(8.0, b)
				bs.update(0.2)
			}
			if (i == 3){
				assertEquals(3.0, b)
				bs.update(0.1)
			}
			if (i == 4){
				assertEquals(0.5, b)
				bs.update(0.3)
			}
			if (i == 5){
				assertEquals(-0.75, b)
				bs.update(0.59)
				assertEquals(false, bs.hasNext())
			}
		}
	}
}
