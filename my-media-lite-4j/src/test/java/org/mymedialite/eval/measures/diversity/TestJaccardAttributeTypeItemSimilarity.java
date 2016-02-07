package org.mymedialite.eval.measures.diversity;

public class TestJaccardAttributeTypeItemSimilarity extends TestAttributeTypeItemSimilarity {

	public void testGetSimilarity() {

		IItemSimilarity is = new JaccardAttributeTypeItemSimilarity(getSmallOverlapMatrix());
		double a = is.getSimilarity(0, 1);
		double e = 1.0 / 4.0;
		assertEquals(e, a, 0.0);
		
		is = new JaccardAttributeTypeItemSimilarity(getBigOverlapMatrix());
		a = is.getSimilarity(0, 1);
		e = 3.0 / 4.0;
		assertEquals(e, a, 0.0);
		
		is = new JaccardAttributeTypeItemSimilarity(getZeroOverlapMatrix());
		a = is.getSimilarity(0, 1);
		e = 0.0;
		assertEquals(e, a, 0.0);
		
		is = new JaccardAttributeTypeItemSimilarity(getAllOverlapMatrix());
		a = is.getSimilarity(0, 1);
		e = 1.0;
		assertEquals(e, a, 0.0);
	}
	
	public void testGetSimilarity2() {
		IItemSimilarity sim = new JaccardAttributeTypeItemSimilarity(getSmallOverlapMatrix2());
		
		double expected = 0.5;
		
		double similarity = sim.getSimilarity(0, 1);	
		assertEquals(expected, similarity, 0.0);
		
		similarity = sim.getSimilarity(1,0);
		assertEquals(expected, similarity, 0.0);
	}
	

}
