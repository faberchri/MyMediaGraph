package org.mymedialite.eval.measures.diversity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

public class TestGiniDiversity extends TestCase {

	public void testNormalize() {

		List<Integer> dummy = new ArrayList<>();

		GiniDiversity gini = new GiniDiversity(10, 3);

		// rec(i): 1=1, 2=2, 3=2, 4=3, 5=2, 6=2, 7=1, 8=1, 9=1, 10=0
		// total = 15
		// n = 10
		//
		//
		// i = 1: 10 = 0: 10/11 * 0/15
		// i = 2: 1 = 1: 9/11 * 1/15
		// i = 3: 7 = 1: 8/11 * 1/15
		// i = 4: 8 = 1: 7/11 * 1/15
		// i = 5: 9 = 1: 6/11 * 1/15
		// i = 6: 2 = 2: 5/11 * 2/15
		// i = 7: 3 = 2: 4/11 * 2/15
		// i = 8: 5 = 2: 3/11 * 2/15
		// i = 9: 6 = 2: 2/11 * 2/15
		// i =10: 4 = 3: 1/11 * 3/15
		//
		//
		// 10/11 * 0/15 + 9/11 * 1/15 + 8/11 * 1/15 + 7/11 * 1/15 + 6/11 * 1/15
		// + 5/11 * 2/15 + 4/11 * 2/15 + 3/11 * 2/15 + 2/11 * 2/15 + 1/11 * 3/15
		// = 61 / 165

		double expected = 2.0 * (61.0 / 165.0); // = 0.73939..

		gini.compute(Lists.newArrayList(1, 2, 3), null, dummy);
		gini.compute(Lists.newArrayList(2, 3, 4), null, dummy);
		gini.compute(Lists.newArrayList(4, 5, 6), null, dummy);
		gini.compute(Lists.newArrayList(4, 5, 6), null, dummy);
		gini.compute(Lists.newArrayList(7, 8, 9), null, dummy);

		double actual = gini.normalize(Double.NaN, Double.NaN);

		assertEquals(expected, actual, 0.0);
	}

	public void testNormalize2() {

		List<Integer> dummy = new ArrayList<>();

		GiniDiversity gini = new GiniDiversity(10, 3);

		// rec(i): 1=1, 2=2, 3=2, 4=2, 5=2, 6=2, 7=1, 8=1, 9=1, 10=1
		// total = 15
		// n = 10
		//
		//
		// i = 1: 10 = 1: 10/11 * 1/15
		// i = 2: 1 = 1: 9/11 * 1/15
		// i = 3: 7 = 1: 8/11 * 1/15
		// i = 4: 8 = 1: 7/11 * 1/15
		// i = 5: 9 = 1: 6/11 * 1/15
		// i = 6: 2 = 2: 5/11 * 2/15
		// i = 7: 3 = 2: 4/11 * 2/15
		// i = 8: 5 = 2: 3/11 * 2/15
		// i = 9: 6 = 2: 2/11 * 2/15
		// i =10: 4 = 2: 1/11 * 2/15
		//
		//
		// 10/11 * 1/15 + 9/11 * 1/15 + 8/11 * 1/15 + 7/11 * 1/15 + 6/11 * 1/15
		// + 5/11 * 2/15 + 4/11 * 2/15 + 3/11 * 2/15 + 2/11 * 2/15 + 1/11 * 2/15
		// = 14 / 33

		double expected = 2.0 * (14.0 / 33.0); // = 0.8484..

		gini.compute(Lists.newArrayList(1, 2, 3), null, dummy);
		gini.compute(Lists.newArrayList(2, 3, 4), null, dummy);
		gini.compute(Lists.newArrayList(4, 5, 6), null, dummy);
		gini.compute(Lists.newArrayList(10, 5, 6), null, dummy);
		gini.compute(Lists.newArrayList(7, 8, 9), null, dummy);

		double actual = gini.normalize(Double.NaN, Double.NaN);

		assertEquals(expected, actual, 0.0);
	}

	public void testNormalize3() {
		List<Integer> dummy = new ArrayList<>();

		GiniDiversity gini = new GiniDiversity(10, 3);

		// rec(i): 1=0, 2=2, 3=2, 4=4, 5=2, 6=2, 7=1, 8=1, 9=1, 10=0
		// total = 15
		// n = 10
		//
		//
		// i = 1: 10 = 0: 10/11 * 0/15
		// i = 2: 1 = 0: 9/11 * 1/15
		// i = 3: 7 = 1: 8/11 * 1/15
		// i = 4: 8 = 1: 7/11 * 1/15
		// i = 5: 9 = 1: 6/11 * 1/15
		// i = 6: 2 = 2: 5/11 * 2/15
		// i = 7: 3 = 2: 4/11 * 2/15
		// i = 8: 5 = 2: 3/11 * 2/15
		// i = 9: 6 = 2: 2/11 * 2/15
		// i =10: 4 = 4: 1/11 * 3/15
		//
		//
		// 10/11 * 0/15 + 9/11 * 0/15 + 8/11 * 1/15 + 7/11 * 1/15 + 6/11 * 1/15
		// + 5/11 * 2/15 + 4/11 * 2/15 + 3/11 * 2/15 + 2/11 * 2/15 + 1/11 * 4/15
		// = 53 / 165

		double expected = 2.0 * (53.0 / 165.0); // = 0.642..

		gini.compute(Lists.newArrayList(4, 2, 3), null, dummy);
		gini.compute(Lists.newArrayList(2, 3, 4), null, dummy);
		gini.compute(Lists.newArrayList(4, 5, 6), null, dummy);
		gini.compute(Lists.newArrayList(4, 5, 6), null, dummy);
		gini.compute(Lists.newArrayList(7, 8, 9), null, dummy);

		double actual = gini.normalize(Double.NaN, Double.NaN);

		assertEquals(expected, actual, 0.0000000001);
	}

	public void testComparisonBug() {
		List<Integer> dummy = new ArrayList<>();

		int numUsers = 2000;
		int numItems = 1000;
		int topN = 100;

		GiniDiversity gini = new GiniDiversity(numItems, topN);

		List<Integer> initialRecs = new ArrayList<>(numItems);
		for (int i = 0; i < numItems; i++) {
			initialRecs.add(i);
		}
		// System.out.println(initialRecs);

		for (int i = 0; i < numUsers; i++) {
			List<Integer> recommendations = new ArrayList<>(initialRecs);
			Collections.shuffle(recommendations);
			// System.out.println(recommendations);
			gini.compute(recommendations, null, dummy);
			double r = gini.normalize(Double.NaN, Double.NaN);
			// System.out.println(r);
			// System.out.println(i + " / " + numUsers + ": " + r);
		}

	}
	
	public void testComparisonBug2() {
		List<Integer> dummy = new ArrayList<>();

		int numUsers = 2000;
		int numItems = 1000;
		int topN = 100;

		GiniDiversity gini = new GiniDiversity(numItems, topN);

		List<Integer> initialRecs = new ArrayList<>(numItems);
		for (int i = 0; i < numItems; i++) {
			initialRecs.add(0);
		}
		// System.out.println(initialRecs);

		for (int i = 0; i < numUsers; i++) {
			List<Integer> recommendations = new ArrayList<>(initialRecs);
			// System.out.println(recommendations);
			gini.compute(recommendations, null, dummy);
			double r = gini.normalize(Double.NaN, Double.NaN);
			// System.out.println(r);
			// System.out.println(i + " / " + numUsers + ": " + r);
		}

	}

}
