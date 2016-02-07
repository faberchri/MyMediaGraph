package ch.uzh.ifi.ddis.mymedialite.graph.randomwalk

import ch.uzh.ifi.ddis.mymedialite.graph.util.Updateable

public class RandomWalkCount implements Updateable<Double>, Comparable<RandomWalkCount> {

	double count = 0.0
	final def vertex

	public RandomWalkCount(def vertex) {
		this.vertex = vertex
	}

	@Override
	public int compareTo(RandomWalkCount other) {
		int c = Double.compare(other.count, this.count)
		if (c != 0){
			return c
		}
		return this.vertex.id.compareTo(other.vertex.id)
	}

	@Override
	public void update() {
		count = count + 1.0
	}

	@Override
	public void update(Double increase) {
		count = count + increase
	}

	@Override
	public int hashCode() {
		final int prime = 31
		int result = 1
		result = prime * result + ((vertex == null) ? 0 : vertex.hashCode())
		return result
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true
		if (obj == null)
			return false
		if (getClass() != obj.getClass())
			return false
		RandomWalkCount other = (RandomWalkCount) obj
		if (vertex == null) {
			if (other.vertex != null)
				return false
		} else if (!vertex.equals(other.vertex))
			return false
		return true
	}

	@Override
	public String toString() {
		return "RWCount-$vertex-(c: $count)"
	}
}
