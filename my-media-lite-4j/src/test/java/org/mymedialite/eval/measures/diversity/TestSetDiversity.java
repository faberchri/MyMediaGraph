package org.mymedialite.eval.measures.diversity;

import java.util.ArrayList;
import java.util.List;

import org.mymedialite.eval.measures.IMeasure.NoUserIdMeasure;

import junit.framework.TestCase;

public class TestSetDiversity extends TestCase {

	public void testCompute() {
		NoUserIdMeasure div = new SetDiversity(new IItemSimilarity() {
			
			@Override
			public double getSimilarity(int itemIdA, int itemIdB) {
				return 1.0;
			}
			
			@Override
			public String getName() {
				return null;
			}
		}, 4);
		
		List<Integer> r = new ArrayList<>();
		
		r.add(0);
		r.add(1);
		r.add(2);
		r.add(3);
		
		double divV = div.compute(r, null, new ArrayList<Integer>());
		assertEquals(0.0, divV);
		
		div = new SetDiversity(new IItemSimilarity() {
			
			@Override
			public double getSimilarity(int itemIdA, int itemIdB) {
				if (itemIdA == itemIdB) return 1.0;
				return 0.5;
			}
			
			@Override
			public String getName() {
				return null;
			}
		}, 4);
		
		divV = div.compute(r, null, new ArrayList<Integer>());
		assertEquals(0.5, divV);
	}

}
