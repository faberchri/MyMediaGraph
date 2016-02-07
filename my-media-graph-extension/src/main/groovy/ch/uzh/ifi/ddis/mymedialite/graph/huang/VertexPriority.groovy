package ch.uzh.ifi.ddis.mymedialite.graph.huang

import ch.uzh.ifi.ddis.mymedialite.graph.util.Updateable

import com.tinkerpop.blueprints.Vertex

class VertexPriority implements Updateable<Double>, Comparable<VertexPriority> {

	final Vertex vertex

	double activationLevel

	public VertexPriority(Vertex vertex, double activationLevel) {
		this.vertex = vertex
		this.activationLevel = activationLevel
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(Double activationLevelIncrease) {
		activationLevel = activationLevel + activationLevelIncrease
	}

	@Override
	public int compareTo(VertexPriority o) {
		int t = Double.compare(o.activationLevel, this.activationLevel)
		if (t == 0) {
			t = this.vertex.id.compareTo(o.vertex.id)
		}
		return t
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
		builder.append("VertexPriority [vertex=")
		builder.append(vertex)
		builder.append(", activationLevel=")
		builder.append(activationLevel)
		builder.append("]")
		return builder.toString()
	}
}
