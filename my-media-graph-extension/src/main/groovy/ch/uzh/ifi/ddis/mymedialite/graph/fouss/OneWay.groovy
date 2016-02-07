package ch.uzh.ifi.ddis.mymedialite.graph.fouss

import com.tinkerpop.blueprints.Vertex

class OneWay extends Commute {

	public Object getPath(Vertex target, Vertex start) {
		return new OneWayPath(target)
	}
}
