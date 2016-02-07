package ch.uzh.ifi.ddis.mymedialite.graph.fouss

import no.uib.cipr.matrix.Matrix
import ch.uzh.ifi.ddis.mymedialite.graph.cooper.TestGraph



class TestLaplacianPseudoinverse extends GroovyTestCase {

	def groundTruthIndexToCalculatedMatrixIndexMapping =[1:5, 2:1, 3:6, 4:7, 5:2, 6:3, 7:8, 8:9, 9:4, 10:10]

	/**
	 * Mathematica code to calculate ground truth:
	 * 
	 * g = Graph[{1 <-> 2, 2 <-> 3, 2 <-> 4, 3 <-> 5, 4 <-> 5, 4 <-> 6, 5 <-> 7, 6 <-> 7, 6 <-> 8, 8 <-> 9, 9 <-> 10} ]
	 *
	 * GraphPlot[t2, VertexLabeling -> True]
	 * 
	 * getPseudoInverseLaplacian[g_] := PseudoInverse[DiagonalMatrix[VertexDegree[g]] - AdjacencyMatrix[g]]
	 *
	 * pseudoInvLap = getPseudoInverseLaplacian[g]
	 * 
	 * TableForm[N[PseudoInvLap], TableHeadings -> Automatic]
	 * 
	 * N[PseudoInvLap, 10]
	 *
	 *
	 */
	double[][] laplacePseudoinverse = [
		[
			1.476000000,
			0.5760000000,
			0.2560000000,
			0.09600000000,
			0.03600000000,
			-0.2240000000,
			-0.1440000000,
			-0.5240000000,
			-0.7240000000,
			-0.8240000000
		],
		[
			0.5760000000,
			0.6760000000,
			0.3560000000,
			0.1960000000,
			0.1360000000,
			-0.1240000000,
			-0.04400000000,
			-0.4240000000,
			-0.6240000000,
			-0.7240000000
		],
		[
			0.2560000000,
			0.3560000000,
			0.7693333333,
			0.1426666667,
			0.2826666667,
			-0.1106666667,
			0.03600000000,
			-0.4106666667,
			-0.6106666667,
			-0.7106666667
		],
		[
			0.09600000000,
			0.1960000000,
			0.1426666667,
			0.4493333333,
			0.1893333333,
			0.06266666667,
			0.07600000000,
			-0.2373333333,
			-0.4373333333,
			-0.5373333333
		],
		[
			0.03600000000,
			0.1360000000,
			0.2826666667,
			0.1893333333,
			0.5293333333,
			0.002666666667,
			0.2160000000,
			-0.2973333333,
			-0.4973333333,
			-0.5973333333
		],
		[
			-0.2240000000,
			-0.1240000000,
			-0.1106666667,
			0.06266666667,
			0.002666666667,
			0.4093333333,
			0.1560000000,
			0.1093333333,
			-0.09066666667,
			-0.1906666667
		],
		[
			-0.1440000000,
			-0.04400000000,
			0.03600000000,
			0.07600000000,
			0.2160000000,
			0.1560000000,
			0.6360000000,
			-0.1440000000,
			-0.3440000000,
			-0.4440000000
		],
		[
			-0.5240000000,
			-0.4240000000,
			-0.4106666667,
			-0.2373333333,
			-0.2973333333,
			0.1093333333,
			-0.1440000000,
			0.8093333333,
			0.6093333333,
			0.5093333333
		],
		[
			-0.7240000000,
			-0.6240000000,
			-0.6106666667,
			-0.4373333333,
			-0.4973333333,
			-0.09066666667,
			-0.3440000000,
			0.6093333333,
			1.409333333,
			1.309333333
		],
		[
			-0.8240000000,
			-0.7240000000,
			-0.7106666667,
			-0.5373333333,
			-0.5973333333,
			-0.1906666667,
			-0.4440000000,
			0.5093333333,
			1.309333333,
			2.209333333
		]
	]

	void testTrain(){
		TestGraph g = new TestGraph()

		LaplacianPseudoinverse recom = new LaplacianPseudoinverse()
		recom.createLaplacianPseudoinverse(g.getAllVerticesSortedByName())
		Matrix m = recom.queryMatrix

		for (int r = 0; r < laplacePseudoinverse.length; r++) {
			for (int c = 0; c < laplacePseudoinverse[0].length; c++) {

				def calcIndexR = groundTruthIndexToCalculatedMatrixIndexMapping[r+1]-1
				def calcIndexC = groundTruthIndexToCalculatedMatrixIndexMapping[c+1]-1
				//println("$r vs. $calcIndexR ")
				//println("$c vs. $calcIndexC")
				assertEquals(laplacePseudoinverse[r][c], m.get(
						calcIndexR,
						calcIndexC), 0.000001)
			}
		}
	}
}
