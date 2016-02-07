package ch.uzh.ifi.ddis.mymedialite.graph.fouss

import groovy.transform.CompileStatic

import com.tinkerpop.blueprints.Vertex

class OneWayPath {

	final Vertex target

	final Map path = [:]

	int pathLength = 0

	public OneWayPath(Vertex target) {
		this.target = target
	}

	@CompileStatic
	boolean targetReached(Vertex current){
		pathLength++
		//println "$pathLength : $current"
		updatePath(current)
		return target.equals(current)
	}

	@CompileStatic
	void updatePath(Vertex current){
		if (pathLength % 2 == 1 && !path.containsKey(current)){
			path.put(current, pathLength)
		}
	}

	void collectPaths(Map pathsLengths, Map targetsCounts){
		path.each{vertex, length ->
			pathsLengths[vertex] = pathsLengths[vertex] + length
			targetsCounts[vertex] = targetsCounts[vertex] + 1
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
		builder.append("OneWayPath [target=")
		builder.append(target)
		builder.append(", path=")
		builder.append(path)
		builder.append(", pathLength=")
		builder.append(pathLength)
		builder.append("]")
		return builder.toString()
	}
}
