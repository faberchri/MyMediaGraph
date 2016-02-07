package ch.uzh.ifi.ddis.mymedialite.graph.fouss

import com.tinkerpop.blueprints.Vertex

class ReturnPath extends OneWayPath {

	final Vertex start

	boolean hit = false

	final startHits = []

	public ReturnPath(Vertex target, Vertex start) {
		super(target)
		this.start = start
	}

	@Override
	public boolean targetReached(Vertex current) {
		pathLength++
		if (start.equals(current)){
			startHits.add(pathLength)
		}
		updatePath(current)
		//println "$pathLength : $current"
		if (!hit) {
			// outbound
			if (target.equals(current)) {
				hit = true
			}
		} else {
			// inbound
			if (start.equals(current)){
				return true
			}
		}
		return false
	}


	void collectPaths(Map pathsLengths, Map targetsCounts) {
		path.each{vertex, pathStep ->
			def length = null
			for (l in startHits){
				if (l > pathStep){
					length = l
					break
				}
			}
			pathsLengths[vertex] = pathsLengths[vertex] + length
			targetsCounts[vertex] = targetsCounts[vertex] + 1
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
		builder.append("ReturnPath [target=")
		builder.append(target)
		builder.append(", path=")
		builder.append(path)
		builder.append(", pathLength=")
		builder.append(pathLength)
		builder.append(", start=")
		builder.append(start)
		builder.append(", hit=")
		builder.append(hit)
		builder.append(", startHits=")
		builder.append(startHits)
		builder.append("]")
		return builder.toString()
	}


}
