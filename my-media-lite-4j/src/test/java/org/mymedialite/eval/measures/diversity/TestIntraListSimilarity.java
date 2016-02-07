package org.mymedialite.eval.measures.diversity;

import java.util.List;
import java.util.ArrayList;

import org.mymedialite.eval.measures.IMeasure.NoUserIdMeasure;

import junit.framework.TestCase;

public class TestIntraListSimilarity extends TestCase {

	public void testCompute() {
		NoUserIdMeasure ils = new IntraListSimilarity(
				new IItemSimilarity() {

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

		double ilsV = ils.compute(r, null, new ArrayList<Integer>());
		assertEquals(6.0, ilsV);

		r.add(4);
		
		List<Integer> i = new ArrayList<>();
		i.add(1);
		ilsV = ils.compute(r, null, i);
		assertEquals(6.0, ilsV);

		ils = new IntraListSimilarity(new IItemSimilarity() {

			@Override
			public double getSimilarity(int itemIdA, int itemIdB) {
				return 1.0;
			}
			
			@Override
			public String getName() {
				return null;
			}
		}, 3);

		ilsV = ils.compute(r, null, new ArrayList<Integer>());
		assertEquals(3.0, ilsV);
	}

}
