package org.mymedialite.eval.measures.diversity;

import org.mymedialite.datatype.IBooleanMatrix;
import org.mymedialite.datatype.SparseBooleanMatrix;

import junit.framework.TestCase;

public abstract class TestAttributeTypeItemSimilarity extends TestCase {

	protected IBooleanMatrix getBigOverlapMatrix() {
		IBooleanMatrix m = new SparseBooleanMatrix();
		
		m.set(0, 0, true);
		m.set(0, 1, true);
		m.set(0, 2, true);
		m.set(0, 3, false);
		m.set(0, 4, false);
		m.set(0, 5, false);
		
		m.set(1, 0, true);
		m.set(1, 1, true);
		m.set(1, 2, true);
		m.set(1, 3, true);
		m.set(1, 4, false);
		m.set(1, 5, false);
		
		return m;
	}

	protected IBooleanMatrix getSmallOverlapMatrix() {
		IBooleanMatrix m = new SparseBooleanMatrix();
		
		m.set(0, 0, true);
		m.set(0, 1, true);
		m.set(0, 2, true);
		m.set(0, 3, false);
		m.set(0, 4, false);
		m.set(0, 5, false);
		
		m.set(1, 0, true);
		m.set(1, 1, false);
		m.set(1, 2, false);
		m.set(1, 3, true);
		m.set(1, 4, false);
		m.set(1, 5, false);
		
		return m;
	}
	
	protected IBooleanMatrix getSmallOverlapMatrix2(){
		IBooleanMatrix m = new SparseBooleanMatrix();
		m.set(0, 10, true);
		m.set(0, 12, true);
		m.set(0, 14, true);
		
		m.set(1, 12, true);
		m.set(1, 14, true);
		m.set(1, 16, true);
		
		return m;
	}

	protected IBooleanMatrix getAllOverlapMatrix() {
		IBooleanMatrix m = new SparseBooleanMatrix();
		
		m.set(0, 0, true);
		m.set(0, 1, true);
		m.set(0, 2, true);
		m.set(0, 3, false);
		m.set(0, 4, false);
		m.set(0, 5, false);
		
		m.set(1, 0, true);
		m.set(1, 1, true);
		m.set(1, 2, true);
		m.set(1, 3, false);
		m.set(1, 4, false);
		m.set(1, 5, false);
		
		return m;
	}

	protected IBooleanMatrix getZeroOverlapMatrix() {
		IBooleanMatrix m = new SparseBooleanMatrix();
		
		m.set(0, 0, true);
		m.set(0, 1, true);
		m.set(0, 2, true);
		m.set(0, 3, false);
		m.set(0, 4, false);
		m.set(0, 5, false);
		
		m.set(1, 0, false);
		m.set(1, 1, false);
		m.set(1, 2, false);
		m.set(1, 3, true);
		m.set(1, 4, true);
		m.set(1, 5, false);
		
		return m;
	}

}