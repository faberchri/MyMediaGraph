package ch.uzh.ifi.ddis.mymedialite.graph.util

import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import com.google.common.collect.TreeRangeMap

class ProbabilityTree<E> {

	private RangeMap<Double, E> rangeMap = TreeRangeMap.create()

	private double probabilitySum = 0.0

	public ProbabilityTree(Map<E,Double> ranges) {
		def sortedByValue = ranges.sort { a,b -> a.value <=> b.value }
		for (r in sortedByValue){
			add(r.key, r.value)
		}
	}

	public ProbabilityTree(){
	}

	public E get(double k){
		return rangeMap.get(k)
	}

	void validateProbabilitySum(double addition){
		if (probabilitySum + addition > 1.0000000000001d ){
			throw new IllegalStateException("$probabilitySum + $addition = ${probabilitySum+addition} > 1.0000000000001d")
		}
	}

	public void add(E e, double prob){
		validateProbabilitySum(prob)
		rangeMap.put(Range.closedOpen(probabilitySum, probabilitySum + prob), e)
		probabilitySum += prob
	}

	//	private Node root
	//
	//	public ProbabilityTree(Map<E,Double> ranges) {
	//		def sortedByValue = ranges.sort { a,b -> a.value <=> b.value }
	//		root = createTree(sortedByValue)
	//	}
	//
	//	public ProbabilityTree(){
	//	}
	//
	//	private Node createTree(Map<E, Double> ranges){
	//		Node stub = null
	//		for (r in ranges) {
	//			if (stub == null){
	//				stub = new Node(r.key, r.value)
	//			} else {
	//				Node right = new Node(r.key, r.value)
	//				Node internal = new Node(stub, right)
	//				stub = internal
	//			}
	//		}
	//		return stub
	//	}
	//
	//	boolean firstSplit = true
	//
	//	public void add(E e, double prob){
	//		Node leaf = new Node(e, prob)
	//		if (root == null){
	//			root = leaf
	//		} else {
	//			if (firstSplit){
	//				if (leaf.pivot < root.pivot){
	//					root = new Node(leaf, root)
	//				} else {
	//					root = new Node(root,leaf)
	//				}
	//				firstSplit = false
	//			} else {
	//				root.add(leaf)
	//			}
	//		}
	//	}
	//
	//	public E get(double k){
	//		return root.get(k)
	//	}
	//
	//	class Node {
	//		private E e
	//
	//		private double pivot
	//
	//		private Node leftChild
	//
	//		private Node rightChild
	//
	//		// leaf constructor
	//		Node(E e, double prob){
	//			this.e = e
	//			this.pivot = prob
	//		}
	//
	//		// internal constructor
	//		Node(Node leftChild, Node rightChild){
	//			this.pivot = leftChild.getPivotSum()
	//			this.leftChild = leftChild
	//			this.rightChild = rightChild
	//		}
	//
	//		void add(Node leaf){
	//			if (leaf.pivot < this.pivot){
	//				if (leftChild.isLeaf()){
	//					if (leaf.pivot < leftChild.pivot){
	//						leftChild = new Node(leaf, leftChild)
	//					} else {
	//						leftChild = new Node(leftChild,leaf)
	//					}
	//				} else {
	//					leftChild.add(leaf)
	//				}
	//				this.pivot = leftChild.getPivotSum()
	//			} else {
	//				if (rightChild.isLeaf()){
	//					if (leaf.pivot < rightChild.pivot){
	//						rightChild = new Node(leaf, rightChild)
	//					} else {
	//						rightChild = new Node(rightChild,leaf)
	//					}
	//				} else {
	//					rightChild.add(leaf)
	//				}
	//			}
	//		}
	//
	//		double udatePivot(double spread){
	//			if (isLeaf()){
	//				return spread + pivot
	//			} else {
	//				pivot = leftChild.getPivotSum() + spread
	//			}
	//
	//
	//		}
	//
	//		boolean isLeaf(){
	//			return e != null
	//		}
	//
	//		double getPivotSum(){
	//			if (isLeaf()){
	//				return pivot
	//			}
	//			double sum = 0.0
	//			sum += leftChild.getPivotSum()
	//			sum += rightChild.getPivotSum()
	//			return sum
	//		}
	//
	//		E get(double k){
	//			if (isLeaf()){
	//				return e
	//			} else{
	//				if (k < pivot){
	//					return leftChild.get(k)
	//				} else {
	//					return rightChild.get(k)
	//				}
	//			}
	//		}
	//
	//		@Override
	//		public String toString() {
	//			StringBuilder builder = new StringBuilder()
	//			builder.append("[")
	//			if (e == null){
	//				builder.append("INT")
	//			} else {
	//				builder.append(e)
	//			}
	//			builder.append("-")
	//			builder.append(pivot)
	//			if (leftChild != null){
	//				builder.append(", L=")
	//				builder.append(leftChild)
	//			}
	//			if (rightChild != null){
	//				builder.append(", R=")
	//				builder.append(rightChild)
	//			}
	//			builder.append("]")
	//			return builder.toString()
	//		}
	//	}

	@Override
	public String toString() {
		return rangeMap.toString()
	}
}
