package org.mymedialite.eval.measures.diversity;

public class TestCosineAttributeTypeItemSimilarity extends
		TestAttributeTypeItemSimilarity {

	public void testGetSimilarity() {

		IItemSimilarity is = new CosineAttributeTypeItemSimilarity(getSmallOverlapMatrix());
		double a = is.getSimilarity(0, 1);
		double e = 1.0 - 0.591752;
		assertEquals(e, a, 0.00001);
		
		is = new CosineAttributeTypeItemSimilarity(getBigOverlapMatrix());
		a = is.getSimilarity(0, 1);
		e = 1.0 - 0.133975;
		assertEquals(e, a, 0.00001);
		
		is = new CosineAttributeTypeItemSimilarity(getZeroOverlapMatrix());
		a = is.getSimilarity(0, 1);
		e = 0.0;
		assertEquals(e, a, 0.0);
		
		is = new CosineAttributeTypeItemSimilarity(getAllOverlapMatrix());
		a = is.getSimilarity(0, 1);
		e = 1.0;
		assertEquals(e, a, 0.0);
	}
	
	public void testGetSimilarity2() {
		IItemSimilarity sim = new CosineAttributeTypeItemSimilarity(getSmallOverlapMatrix2());
		
		double expected = 2.0 / 3.0;
		
		double similarity = sim.getSimilarity(0, 1);	
		assertEquals(expected, similarity, 0.0000000001);
		
		similarity = sim.getSimilarity(1,0);
		assertEquals(expected, similarity, 0.0000000001);
	}

}
