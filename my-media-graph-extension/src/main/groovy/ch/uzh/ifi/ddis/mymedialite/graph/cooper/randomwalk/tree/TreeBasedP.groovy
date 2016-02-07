package ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree

import ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.AbstractP

public abstract class TreeBasedP extends AbstractP {

	public TreeBasedP(def power, def alpha) {
		super(power, alpha)
	}

	def buildRandomWalkDatastructure(def userVertex){
		def tree = createSearchTree(userVertex)
		return convertTreeToListStack(tree)[0]
	}

	def convertTreeToListStack(def tree){
		def res = []
		tree.each{k,subtree ->
			if (subtree.isEmpty()) {
				res.add(k)
			}
			else{
				res.add(convertTreeToListStack(subtree))
			}
		}
		return res
	}

	def abstract createSearchTree(def userVertex)

	def performRandomWalk(def randomWalkMatrix, def rand){
		// equivalent P3 implementation
		//		def i1 = rand.nextInt(randomWalkMatrix.size())
		//		def i2 = rand.nextInt(randomWalkMatrix[i1].size())
		//		def i3 = rand.nextInt(randomWalkMatrix[i1][i2].size())
		//		return randomWalkMatrix[i1][i2][i3]

		def r = randomWalkMatrix
		def l = getPower()
		for (c in 0..<l){
			r = r[rand.nextInt(r.size())]
		}
		return r
	}
}
