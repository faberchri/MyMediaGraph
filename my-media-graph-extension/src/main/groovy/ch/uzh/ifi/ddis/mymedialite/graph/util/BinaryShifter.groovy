package ch.uzh.ifi.ddis.mymedialite.graph.util


public class BinaryShifter implements Iterable, Iterator{

	private double lowerLim

	private double upperLim

	private double threshold

	private double minBoundDistance

	private double pivot = lowerLim

	private Map allLimits = [:]

	private hasNext = true

	public BinaryShifter(double lowerLim, double upperLim, double threshold, double minBoundDistance) {
		this.lowerLim = lowerLim
		this.upperLim = upperLim
		this.threshold = threshold
		this.minBoundDistance = minBoundDistance
	}

	public void update(double newResult){
		allLimits[pivot] = newResult
		//		println "update(): pivot: $pivot | value: $newResult"
		this.hasNext = !converged()
		shift()
	}

	private boolean converged(){
		//		println "hasNext(): allLimits: $allLimits"
		if (!allLimits.containsKey(lowerLim) || !allLimits.containsKey(upperLim)){
			return false
		}
		if(Math.abs(lowerLim - upperLim) < minBoundDistance){
			return true
		}
		boolean thresholdReached = Math.abs(allLimits[lowerLim] - allLimits[upperLim]) < threshold
		//		println "hasNext(): thresholdReached: $thresholdReached (${Math.abs(allLimits[lowerLim] - allLimits[upperLim])}, lower: $lowerLim, upper: $upperLim)"
		return thresholdReached
	}

	@Override
	public boolean hasNext() {
		return hasNext
	}

	@Override
	public Object next() {
		//		println "next(): returned pivot $pivot (allLimits: $allLimits)"
		return pivot
	}

	private void shift(){
		if (allLimits[lowerLim] == null){
			pivot = lowerLim
		} else if (allLimits[upperLim] == null){
			pivot = upperLim
		} else {
			def halfDistance = Math.abs(lowerLim - upperLim) / 2.0
			if (allLimits[lowerLim] < allLimits[upperLim]){
				lowerLim = lowerLim + halfDistance
				pivot = lowerLim
			} else {
				upperLim = upperLim - halfDistance
				pivot = upperLim
			}
		}
	}

	@Override
	public Iterator iterator() {
		return this
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException()
	}
}
